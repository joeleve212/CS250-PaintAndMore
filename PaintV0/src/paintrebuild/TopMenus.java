package paintv0;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.tools.Tool;
import java.io.File;
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
    public int IMG_POS_X=0, IMG_POS_Y=0;
    public GraphicsContext gc;
    public double x0,y0,x1,y1, lineWidth;
    public ToolBar toolBar;
    public double[] xCoord, yCoord; //for use with polygon drawing
    private Stack prevVersions;
    private paintv0.BottomToolSet bottomTools;
    private TextField textInput;
    private MenuBar pinnedMenu;
    TopMenus(Stage primaryStage, Group group, Stack versions, BottomToolSet bottomTools){
        this.bottomTools = bottomTools;
        this.toolBar = bottomTools.getToolBar();
        placedImgView = new ImageView();
        prevVersions = versions;
        textInput = new TextField("Kevin");//Needs to be improved: (TextField)toolBar.getItems().get(3);

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
        MenuItem poly = new MenuItem("Polygon");

        shapeMenu.getItems().addAll(line, rect, free, oval, circ);
        toolMenu.getItems().addAll(copier, cutter, text, eraser, grabber);

        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        MenuItem release_notes = new MenuItem("Release Notes"); //TODO: implement popup w/ release notes read from file
        MenuItem toolHelp = new MenuItem("Help");           //TODO: implement popup w/ info on current tool selected
        helpMenu.getItems().addAll(about, release_notes, toolHelp);

        pinnedMenu = new MenuBar(fileMenu,toolMenu,shapeMenu,helpMenu); //Plopping the menu pull-downs onto the menuBar

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
        poly.setOnAction((e)->{
            int numSides = 3;// inputBox.getValue();
            //TODO: Prompt for number of sides - Reuse
            xCoord = new double[numSides];
            xCoord = new double[numSides];
            drawMode = 10;
            updateMenus();//TODO: update this method to account for higher drawModes
        });
    }
    public int getDrawMode(){return drawMode;}
    public void setDrawMode(int x){drawMode = x;}
    public MenuBar getMenuBar(){return pinnedMenu;}
    public void setShapeLineColor(Color newColor){gc.setStroke(newColor);}
    public Paint getLineColor(){return gc.getStroke();}
    public void setShapeFillColor(Color newColor){gc.setFill(newColor);fill = true;}
    public void setToolBar(ToolBar tools){toolBar=tools;}
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
                if(fill){
                    gc.fillOval(x0,y0,Math.abs(x1-x0), Math.abs(y0-y1));
                }
                gc.strokeOval(x0,y0,Math.abs(x1-x0), Math.abs(y0-y1));
                break;
            case 5: //Circle
//TODO: allow drawing from center to edge
                double w = Math.abs(x1 - x0);
                //double h = Math.abs(x1 - x0);
                if (fill) {
                    gc.fillOval(x0, y0, w, w);
                }
                gc.strokeOval(x0, y0, w, w);
                break;
            case 6: //Regular Polygon
                //TODO: implement
                break;
            case 7: //Choice shape - TBD
                //TODO: decide on shape & implement
                break;
            case 8: //Text placement
                String inputString = textInput.getCharacters().toString(); //pull in the current typed string
                gc.fillText(inputString,x0,y0, Math.abs(x1-x0));
                break;
            default:
                System.out.println("Invalid Drawing Mode Selected");
        }

        saveSnap();
        return true;
    }
    void setLineWidth(double newLineW){lineWidth=newLineW;}
    public void updateMenus(){ //TODO: fix this to actually update toolbar
//TODO: idea-set visibility to false for unused pieces
        toolBar = bottomTools.updateTools(drawMode);
        //Call BottomToolSet.updateTools(int drawMode)
//TODO: implement this to check drawMode (and other?) to adjust the toolBar buttons
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
