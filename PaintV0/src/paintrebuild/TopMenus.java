package paintv0;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class TopMenus {
    public int DEFAULT_CANV_W = 400, DEFAULT_CANV_H = 400;
    public boolean imgInserted = false;
    public String currImgPath, ext, OPENER_FILE_LOC = "../../../";
    public File savedImg;
    public ImageView placedImgView;
    public int drawMode = 0; //-1 = color grab, 0 for none, 1 for line, 2 for rect, 3 for circ, etc
    public boolean fill = false;
    public Image img;
    private GraphicsContext gc;
    private Canvas imgCanv = new Canvas(DEFAULT_CANV_W,DEFAULT_CANV_H);
    private MenuBar pinnedMenu;
    private double x0,y0,x1,y1, lineWidth;
    private int IMG_POS_X=0, IMG_POS_Y=0;
    private boolean primaryJustClicked = false;
    private boolean imageHasBeenSaved = false;
    private Stack prevVersions;
    private TextField textInput;
    TopMenus(Stage primaryStage, Group group, Stack versions, ToolBar toolBar){

        placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();
        prevVersions = versions;

        textInput = (TextField)toolBar.getItems().get(3);

        final Menu fileMenu = new Menu("File");    //Populate the first menu pull-down - File
        MenuItem imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        MenuItem openBtn = new MenuItem("Open Image");
        fileMenu.getItems().addAll(imageSave, openBtn, exitBtn);

        final Menu toolMenu = new Menu("Tools"); //Populate the next menu pull-down - Options
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

        shapeMenu.getItems().addAll(line, rect, free, oval, circ);
        toolMenu.getItems().addAll(copier, cutter, text, eraser, grabber);

        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        helpMenu.getItems().addAll(about);

        pinnedMenu = new MenuBar(fileMenu,toolMenu,shapeMenu,helpMenu); //Plopping the menu pull-downs onto the menuBar

        imageSave.setOnAction((e)->{
            System.out.println("Saving image file...");
            FileChooser saveImageChoose = new FileChooser();
            saveImageChoose.setTitle("Save Image As");
            saveImageChoose.getExtensionFilters().addAll( //allow saving in different file formats
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg"),
                    new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
                    new FileChooser.ExtensionFilter("BMP Files", "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF Files", "*.gif"));
            savedImg = saveImageChoose.showSaveDialog(null);
            String name = savedImg.getName();
            ext = name.substring(1+name.lastIndexOf(".")).toLowerCase(); //grab only the file extension of the image

            if(savedImg != null){
                try {
                    WritableImage wImage = new WritableImage((int)imgCanv.getWidth(), (int)img.getHeight());
                    imgCanv.snapshot(null, wImage);
                    RenderedImage rImage = SwingFXUtils.fromFXImage(wImage, null);
                    ImageIO.write(rImage, "png", savedImg);
                } catch (IOException ex) {
                    System.out.println("Initial save failed");
                }
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

        openBtn.setOnAction((e)->{ //This function defines the action when open file is clicked
            openFile.setInitialDirectory(new File(OPENER_FILE_LOC));
            openFile.setTitle("Open File");
            openFile.getExtensionFilters().addAll(              //Including filters for extension types
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File insImg = openFile.showOpenDialog(primaryStage);
            currImgPath = insImg.getPath();
            System.out.println(currImgPath);
            if (currImgPath != "") {
                try {
                    InputStream io = new FileInputStream(insImg);
                    img = new Image(io);
                    //TODO: Image scaling
                    placedImgView.setImage(img);        //Specifying placement & sizing of selected image
                    placedImgView.setPreserveRatio(true);

                    gc = imgCanv.getGraphicsContext2D();
                    gc.getCanvas().setWidth(img.getWidth());  //Setting canvas to the size of the image
                    gc.getCanvas().setHeight(img.getHeight());
                    gc.drawImage(img, IMG_POS_X,IMG_POS_Y, imgCanv.getWidth(),imgCanv.getHeight());
                    saveSnap();
                    group.getChildren().add(imgCanv);
                    imgInserted = true;

                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });

        text.setOnAction((e)->{
            drawMode = 8;
        });

        grabber.setOnAction((e)->{
            drawMode = -1; //It's not drawing, so negative for color grab mode
        });

        about.setOnAction((e) -> {
            InfoPopup aboutPop = new InfoPopup(primaryStage, "About");
        });

        imgCanv.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.isPrimaryButtonDown()) {
                        gc.beginPath();
                        x0 = event.getX();
                        y0 = event.getY();
                        primaryJustClicked = true;
                        if(drawMode==-1){
                            PixelReader colorSnag = img.getPixelReader();
                            Color newColor = colorSnag.getColor((int)x0, (int)y0);
                            setShapeLineColor(newColor);
                        }
                    }
                    if(drawMode==3){
                        x1=x0;
                        y1=y0;
                    }
                }
            }
        );
//TODO: implement mouse drag event to see shape during creation
        imgCanv.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.isPrimaryButtonDown() && drawMode==3) {
                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
            }
        });
        imgCanv.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(!event.isPrimaryButtonDown()&&primaryJustClicked&&drawMode!=3) {
                        x1 = event.getX();
                        y1 = event.getY();
                        drawShape();
                        gc.save();
                        primaryJustClicked = false;
                    }
                    else if(drawMode==3){
                        gc.closePath();
                        saveSnap();
                    }

                }
            }
        );

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
    }
    int getDrawMode(){return drawMode;}
    void setDrawMode(int x){drawMode = x;}
    MenuBar getMenuBar(){return pinnedMenu;}
    void setShapeLineColor(Color newColor){gc.setStroke(newColor);}
    void setShapeFillColor(Color newColor){gc.setFill(newColor);fill = true;}
    boolean drawShape(){
        gc.setLineWidth(lineWidth);
//TODO: make this a switch case
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
                //TODO:
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
    boolean updateMenus(){
        return false;
//TODO: implement this to check drawMode (and other?) to adjust the menu buttons
    }
    public Canvas getCanv(){return imgCanv;}
    public void setCanvVersion(WritableImage currVersion){
        gc.drawImage(currVersion, IMG_POS_X,IMG_POS_Y, currVersion.getWidth(),currVersion.getHeight());
    }
    private void saveSnap(){
        WritableImage wImage = new WritableImage((int)img.getWidth(), (int)img.getHeight());
        imgCanv.snapshot(null, wImage);
        prevVersions.push(wImage);
    }

}
