package paintv0;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import paintrebuild.ToolTimer;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
/**
 * This class creates the menu at the top of the window
 * and handles the operations controlled by those menus.
 *
 * @author  Joe Leveille
 * @version 0.4
 * @since   2019-10-02
 */
public class TopMenus{ //TODO: resize tool
    public int DEFAULT_CANV_W = 400, DEFAULT_CANV_H = 400;
    public ImageView placedImgView;
    public String OPENER_FILE_LOC = "../../../";
    public int drawMode = 0; //-1 = color grab, 0 for none, 1 for line, 2 for rect, 3 for circ, etc
    public boolean fill = false;
    public Image img;
    public Canvas imgCanv = new Canvas(DEFAULT_CANV_W,DEFAULT_CANV_H);
    public MenuItem imageSave, openBtn;
    public boolean imageHasBeenSaved = false;
    public int IMG_POS_X=0, IMG_POS_Y=0, numSides;
    public GraphicsContext gc;
    public double x0,y0,x1,y1, lineWidth;
    public ToolBar toolBar;
    public double[] xCoord, yCoord; //for use with polygon drawing
    public String releaseNotesPath = "src/../ExtraDocs/ReleaseNotes.txt";
    public Text modeLabel = new Text("No Tool Selected");
    public ToolTimer toolTimer;
    public ImageView stickerView;
    public Image stickerImg;
    private Stack prevVersions;
    private int minCanv=50,maxCanv=5000;
    private ObservableList<Node> origTools;
    private String inputText = "Kevin";
    private MenuBar pinnedMenu;
    private double defaultFitHeight=.4;
    TopMenus(Stage primaryStage, Group group, Stack versions, ToolBar origToolBar){
        this.toolBar = origToolBar;
        origTools = origToolBar.getItems();
        toolBar.getItems().add(modeLabel);
        placedImgView = new ImageView();
        prevVersions = versions;

        Menu fileMenu = new Menu("File");    //Populate the first menu pull-down - File
        imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        openBtn = new MenuItem("Open Image");
        Menu resizeCanv = new Menu("Resize Canvas");
        //TODO: use spinners to actually change size
        Spinner<Integer> canvWidth = new Spinner<Integer>(minCanv,maxCanv,DEFAULT_CANV_W);
        Spinner<Integer> canvHeight = new Spinner<Integer>(minCanv,maxCanv,DEFAULT_CANV_H);
        canvHeight.setEditable(true);
        canvWidth.setEditable(true);
        CustomMenuItem canvWidthChoose = new CustomMenuItem(canvWidth);
        CustomMenuItem canvHeightChoose = new CustomMenuItem(canvHeight);
        resizeCanv.getItems().addAll(canvWidthChoose, canvHeightChoose);
        fileMenu.getItems().addAll(imageSave, openBtn, exitBtn, resizeCanv);

        Menu toolMenu = new Menu("Tools"); //Populate the next menu pull-down - Options
        MenuItem cutter = new MenuItem("Cut");
        MenuItem copier = new MenuItem("Copy");
        MenuItem text = new MenuItem("Text");
        MenuItem eraser = new MenuItem("Erase");
        MenuItem grabber = new MenuItem("Grab Color");
        MenuItem sticker = new MenuItem("Sticker");

        Menu shapeMenu = new Menu("Draw Shape"); //Populate Drawing tools pull-down
        MenuItem line = new MenuItem("Line");
        MenuItem rect = new MenuItem("Rectangle");
        MenuItem free = new MenuItem("Free Draw");
        MenuItem oval = new MenuItem("Ellipse");
        MenuItem circ = new MenuItem("Circle");
        MenuItem regPoly = new MenuItem("Polygon");
        MenuItem freePoly = new MenuItem("Free Poly");

        shapeMenu.getItems().addAll(line, rect, free, oval, circ, regPoly,freePoly);
        toolMenu.getItems().addAll(copier, cutter, text, eraser, grabber, sticker);

        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        MenuItem release_notes = new MenuItem("Release Notes");
        MenuItem toolHelp = new MenuItem("Help");
        helpMenu.getItems().addAll(about, release_notes, toolHelp);

        pinnedMenu = new MenuBar(fileMenu,toolMenu,shapeMenu,helpMenu); //Plopping the menu pull-downs onto the menuBar
        toolTimer = new ToolTimer();
        Thread toolThread = new Thread(toolTimer);
        toolTimer.beginTimer("No Tool");
        toolThread.setDaemon(true);
        toolThread.start();

        toolHelp.setOnAction((e)->{
            InfoPopup helpInfo = new InfoPopup(primaryStage, "helpInfo");
        });
        release_notes.setOnAction((e)->{
            File ReleaseNotesDoc = new File(releaseNotesPath);
            try{
                Desktop.getDesktop().open(ReleaseNotesDoc);
            } catch (IOException ev){
                System.out.println("Cannot find release notes file");
            }
        });
        exitBtn.setOnAction((e)->{ //Define the behavior on click for exit button
            if(imageHasBeenSaved){
                Platform.exit();
            }
            else{
                InfoPopup smartSave = new InfoPopup(primaryStage, "exitSave");
            }
        });
        resizeCanv.setOnAction((e)->{
            //TODO: implement spinners to set x size & y size
            //TODO: adjust any image present to fit w/ same aspect ratio

        });
//TODO: insert Javadoc for all drawMode buttons
        sticker.setOnAction((e)->{
            drawMode = 9;
            updateMenus();
            FileChooser openStick = new FileChooser(); //Picking an image
            openStick.setInitialDirectory(new File(OPENER_FILE_LOC));
            openStick.setTitle("Open Sticker Image");
            File stickImgFile = openStick.showOpenDialog(primaryStage);
            String stickPath = stickImgFile.getPath();
            if (stickPath != "") {
                try {
                    stickerImg = new Image(new FileInputStream(stickImgFile), 100, 200, true, true);
                    stickerView = new ImageView(stickerImg); //ImageView is needed in order for image to show up,
                    stickerView.setPreserveRatio(true);      //despite the image being placed directly
                    stickerView.setFitHeight(defaultFitHeight);
                } catch (IOException ex) {
                    System.out.println("That file is invalid");
                }
            }
        });
        text.setOnAction((e)->{
            drawMode = 8;
            updateMenus();
        });
        cutter.setOnAction((e)->{
            drawMode = -2; //non-drawing tool ID
            updateMenus();
        });
        copier.setOnAction((e)->{
            drawMode = -3; //non-drawing tool ID
            updateMenus();
        });
        eraser.setOnAction((e)->{
            drawMode = 3; //set drawMode - same as freeDraw except auto white
            if(img!=null){gc.setStroke(Color.WHITE);}
            ColorPicker eraseColor = (ColorPicker)toolBar.getItems().get(1);
            eraseColor.setValue(Color.WHITE);
            updateMenus(); //SHOULD show only needed items
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
            drawMode = 6;
            updateMenus();
        });
        freePoly.setOnAction((e)->{
            drawMode=7;
            updateMenus();
        });
    }

    public int getDrawMode(){return drawMode;}
    public Paint getLineColor(){return gc.getStroke();}
    public double getLineWidth(){return gc.getLineWidth();}
    public Paint getFillColor(){return gc.getFill(); }
    public MenuBar getMenuBar(){return pinnedMenu;}
    public Canvas getCanv(){return imgCanv;} //Used to connect imgCanv in PaintV0
    public void setLineWidth(double newLineW){lineWidth=newLineW;}
    public void setDrawMode(int x){drawMode = x;}
    public void setShapeLineColor(Color newColor){gc.setStroke(newColor);}
    public void setShapeFillColor(Color newColor){gc.setFill(newColor);fill = true;}
    public void setInputString(String newString){inputText=newString;}
    public void setCanvVersion(WritableImage currVersion){ //used by main class for undo & redo
        gc.drawImage(currVersion, IMG_POS_X,IMG_POS_Y, currVersion.getWidth(),currVersion.getHeight());
    }
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
                gc.strokeRect(Math.min(x0,x1), Math.min(y0,y1), Math.abs(x1-x0), Math.abs(y0-y1));
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

    public void updateMenus(){
        String toolName="";
        boolean visibility[]={true,true,true,true};
        switch (drawMode){
            case 1:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "Line";
                break;
            case 2:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=true;
                visibility[3]=false;
                toolName = "Rectangle";
                break;
            case 3:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "Free Draw";
                break;
            case 4:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=true;
                visibility[3]=false;
                toolName = "Ellipse";
                break;
            case 5:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=true;
                visibility[3]=false;
                toolName = "Circle";
                break;
            case 6:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=true;
                visibility[3]=false;
                toolName = "Polygon";
                break;
            case 7:
                visibility[0]=true;
                visibility[1]=true;
                visibility[2]=true;
                visibility[3]=false;
                toolName = "Free Polygon";
                break;
            case 8:
                visibility[0]=false;
                visibility[1]=false;
                visibility[2]=false;
                visibility[3]=true;
                toolName = "Text";
                break;
            case 9:
                visibility[0]=false;
                visibility[1]=false;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "Sticker";
                break;
            case -1:
                visibility[0]=false;
                visibility[1]=true;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "Color Grab";
                break;
            case -2:
                visibility[0]=false;
                visibility[1]=false;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "Cut/Paste";
                break;
            case -3:
                visibility[0]=false;
                visibility[1]=false;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "Copy/Paste";
                break;
            default:
                visibility[0]=false;
                visibility[1]=false;
                visibility[2]=false;
                visibility[3]=false;
                toolName = "No Tool Selected";
        }
        for(int i = 0; i<4;i++){
            toolBar.getItems().get(i).setVisible(visibility[i]);
        }
        modeLabel.setText(toolName);
        toolTimer.switchTool(toolName);
    }
    public void saveSnap(){
        WritableImage wImage = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        imgCanv.snapshot(null, wImage);
        prevVersions.push(wImage);
    }
}
