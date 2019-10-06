package paintv0;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.tools.Tool;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class TopMenus {
    public int DEFAULT_CANV_W = 400, DEFAULT_CANV_H = 400;
    public ImageView placedImgView;
    public int drawMode = 0; //-1 = color grab, 0 for none, 1 for line, 2 for rect, 3 for circ, etc
    public boolean fill = false;
    public Image img;
    public Canvas imgCanv = new Canvas(DEFAULT_CANV_W,DEFAULT_CANV_H);
    public MenuItem imageSave, openBtn;
    public boolean imageHasBeenSaved = false; //TODO: eliminate separate instances of this var?
    public int IMG_POS_X=0, IMG_POS_Y=0, numSides;
    public GraphicsContext gc;
    public double x0,y0,x1,y1, lineWidth;
    public ToolBar toolBar;
    public double[] xCoord, yCoord; //for use with polygon drawing
    public String releaseNotesPath = "src/../ExtraDocs/ReleaseNotes.txt";
    public Text modeLabel = new Text("No Tool Selected");
    private Stack prevVersions;
    private String inputText = "Kevin";
    private MenuBar pinnedMenu;
    TopMenus(Stage primaryStage, Group group, Stack versions, ToolBar toolBar){
        this.toolBar = toolBar;
        toolBar.getItems().add(modeLabel);
        placedImgView = new ImageView();
        prevVersions = versions;

        Menu fileMenu = new Menu("File");    //Populate the first menu pull-down - File
        imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        openBtn = new MenuItem("Open Image");
        fileMenu.getItems().addAll(imageSave, openBtn, exitBtn);

        Menu toolMenu = new Menu("Tools"); //Populate the next menu pull-down - Options
        MenuItem cutter = new MenuItem("Cut");
        MenuItem copier = new MenuItem("Copy");
        MenuItem text = new MenuItem("Text");
        MenuItem eraser = new MenuItem("Erase");
        MenuItem grabber = new MenuItem("Grab Color");

        Menu shapeMenu = new Menu("Draw Shape"); //Populate Drawing tools pull-down
        MenuItem line = new MenuItem("Line");
        MenuItem rect = new MenuItem("Rectangle");
        MenuItem free = new MenuItem("Free Draw");
        MenuItem oval = new MenuItem("Ellipse");
        MenuItem circ = new MenuItem("Circle");
        MenuItem regPoly = new MenuItem("Polygon");
        MenuItem freePoly = new MenuItem("Free Poly");

        shapeMenu.getItems().addAll(line, rect, free, oval, circ, regPoly,freePoly);
        toolMenu.getItems().addAll(copier, cutter, text, eraser, grabber);

        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        MenuItem release_notes = new MenuItem("Release Notes"); //TODO: implement popup w/ release notes read from file
        MenuItem toolHelp = new MenuItem("Help");           //TODO: implement popup w/ info on current tool selected
        helpMenu.getItems().addAll(about, release_notes, toolHelp);

        pinnedMenu = new MenuBar(fileMenu,toolMenu,shapeMenu,helpMenu); //Plopping the menu pull-downs onto the menuBar

        cutter.setOnAction((e)->{
            drawMode = -2; //negative num for non-drawing tool
            updateMenus();
        });

        toolHelp.setOnAction((e)->{
            InfoPopup helpInfo = new InfoPopup(primaryStage, "helpInfo");
        });

        release_notes.setOnAction((ev)->{
            File ReleaseNotesDoc = new File(releaseNotesPath);
            try{
                Desktop.getDesktop().open(ReleaseNotesDoc);
            } catch (IOException e){}
        });

        eraser.setOnAction((e)->{
            drawMode = 3; //set drawMode - same as freeDraw except auto white
            gc.setStroke(Color.WHITE);
            updateMenus(); //SHOULD show only needed items
        });
        exitBtn.setOnAction((e)->{ //Define the behavior on click for exit button
            if(imageHasBeenSaved){
                Platform.exit();
            }
            else{
                InfoPopup smartSave = new InfoPopup(primaryStage, "exitSave");
            }
        });

        text.setOnAction((e)->{
            drawMode = 8;
            updateMenus();
        });

        grabber.setOnAction((e)->{
            drawMode = -1; //It's not drawing, so negative for color grab mode
            updateMenus();
        });

        about.setOnAction((e) -> {
            InfoPopup aboutPop = new InfoPopup(primaryStage, "About");
        });

        line.setOnAction((e)->{
            drawMode = 1;
            updateMenus();
        });
        rect.setOnAction((e)->{
            drawMode = 2;
            updateMenus();
        });
        free.setOnAction((e)->{
            drawMode = 3;
            updateMenus();
        });
        oval.setOnAction((e)->{
            drawMode = 4;
            updateMenus();
        });
        circ.setOnAction((e)->{
            drawMode = 5;
            updateMenus();
        });
        regPoly.setOnAction((e)->{
            numSides = 3;// inputBox.getValue();
            try{numSides = Integer.parseInt(inputText);} //May need catch
            catch(Exception ev){System.out.println("Couldn't detect number");}
//            xCoord = new double[numSides];
//            yCoord = new double[numSides];
            drawMode = 6;
            updateMenus();
        });
        freePoly.setOnAction((e)->{
            drawMode=7;
            updateMenus();
        });
    }
    public int getDrawMode(){return drawMode;}
    public void setDrawMode(int x){drawMode = x;}
    public MenuBar getMenuBar(){return pinnedMenu;}
    public void setShapeLineColor(Color newColor){gc.setStroke(newColor);}
    public Paint getLineColor(){return gc.getStroke();}
    public double getLineWidth(){return gc.getLineWidth();}
    public void setShapeFillColor(Color newColor){gc.setFill(newColor);fill = true;}
    public Paint getFillColor(){return gc.getFill(); }
    public void setToolBar(ToolBar tools){toolBar=tools;}
    public void setInputString(String newString){inputText=newString;}
    public void drawPolygon(Polygon poly, double radius){
        xCoord = new double[poly.getPoints().size()/2];
        yCoord = new double[poly.getPoints().size()/2];
        int j = 0;
        for(int i=0;i< numSides;i++){
            xCoord[i]=radius*Math.cos(2*i*Math.PI/numSides)+x0;
            yCoord[i]=radius*Math.sin(2*i*Math.PI/numSides)+y0;
        }
        gc.strokePolygon(xCoord,yCoord,numSides);
        if(fill){
            gc.fillPolygon(xCoord,yCoord,numSides);
        }
        saveSnap();
    }
    public void drawSelfPoly(ArrayList<Double> polyPointsX, ArrayList<Double> polyPointsY){
        double[] polyArrayX = new double[100];
        double[] polyArrayY = new double[100];
        double firstX=polyPointsX.get(0),firstY=polyPointsX.get(0);
        for (int i = 0; i < polyPointsX.size(); i++) {
            polyArrayX[i] = polyPointsX.get(i);
            polyArrayY[i] = polyPointsY.get(i);
        }

        gc.fillPolygon(polyArrayX,polyArrayY,polyPointsX.size());
        gc.strokePolygon(polyArrayX,polyArrayY,polyPointsX.size());
        saveSnap();
    }
    public boolean drawShape(){
        gc.setLineWidth(lineWidth);
        switch(drawMode){ //Place line between x0,y0 & x1,y1
            case 1:
                gc.strokeLine(x0,y0,x1,y1);
                break;
            case 2: //Rectangle
                if(fill){
                    gc.fillRect(x0, y0, Math.abs(x1-x0), Math.abs(y0-y1));
                }
                //place rectangle between opposite corners x0,y0 & x1,y1
//TODO: allow drawing rect from corner other than top left
                gc.strokeRect(x0, y0, Math.abs(x1-x0), Math.abs(y0-y1));
                break;
            case 4:  //Ellipse
                double deltX = Math.abs(x1-x0);
                double deltY = Math.abs(y1-y0);
                if(fill){
                    gc.fillOval(x0-deltX,y0-deltY,2*deltX, 2*deltY);
                }
                gc.strokeOval(x0-deltX,y0-deltY,2*deltX, 2*deltY);
                break;
            case 5: //Circle
//TODO: allow drawing from center to edge
                double w = Math.abs(x1 - x0);
                //double h = Math.abs(x1 - x0);
                if (fill) {
                    gc.fillOval(x0-w, y0-w, 2*w, 2*w);
                }
                gc.strokeOval(x0-w, y0-w, 2*w, 2*w);
                break;
            case 8: //Text placement
                gc.fillText(inputText,x0,y0, Math.abs(x1-x0));
                break;
            default:
                System.out.println("Invalid Drawing Mode Selected");
        }
        saveSnap();
        return true;
    }
    void setLineWidth(double newLineW){lineWidth=newLineW;}
    public void updateMenus(){ //TODO: actually update toolbar controls
        switch (drawMode){ //TODO: check that I have all modes covered
            case 1:
                modeLabel.setText("Line");
                break;
            case 2:
                modeLabel.setText("Rectangle");
                break;
            case 3:
                modeLabel.setText("Free Draw");
                break;
            case 4:
                modeLabel.setText("Ellipse");
                break;
            case 5:
                modeLabel.setText("Circle");
                break;
            case 6:
                modeLabel.setText("Polygon");
                break;
            case -1:
                modeLabel.setText("Color Grab");
                break;
            case 8:
                modeLabel.setText("Text");
                break;
            case -2:
                modeLabel.setText("Cut/Paste");
                break;
            case 7:
                modeLabel.setText("Free Polygon");
                break;
            default:
                modeLabel.setText("No Tool Selected");
        }
    }
    public Canvas getCanv(){return imgCanv;}
    public void setCanvVersion(WritableImage currVersion){
        gc.drawImage(currVersion, IMG_POS_X,IMG_POS_Y, currVersion.getWidth(),currVersion.getHeight());
    }
    public void saveSnap(){
        WritableImage wImage = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        imgCanv.snapshot(null, wImage);
        prevVersions.push(wImage);
    }
}
