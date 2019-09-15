package paintv0;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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

public class TopMenus {
    public int DEFAULT_CANV_W = 400, DEFAULT_CANV_H = 400;
    public boolean imgInserted = false;
    public String currImgPath, ext, OPENER_FILE_LOC = "../../../";
    public File savedImg;
    public int drawMode = 0; //0 for none, 1 for line, 2 for rect, 3 for circ - Index in 'draw shape' list
    public boolean fill = false;
    GraphicsContext gc;
    Image img;
    private Canvas imgCanv = new Canvas(DEFAULT_CANV_W,DEFAULT_CANV_H);
    MenuBar pinnedMenu;
    private double x0,y0,x1,y1, lineWidth;
    private int IMG_POS_X=0, IMG_POS_Y=0;
    private boolean primaryJustClicked = false;

    TopMenus(Stage primaryStage, GridPane gridPane){

        ImageView placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();

        final Menu fileMenu = new Menu("File");    //Populate the first menu pull-down - File
        MenuItem imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        MenuItem openBtn = new MenuItem("Open Image");
        fileMenu.getItems().addAll(imageSave, openBtn, exitBtn);

        final Menu toolMenu = new Menu("Tools"); //Populate the next menu pull-down - Options
        MenuItem cutter = new MenuItem("Cut");
        MenuItem copier = new MenuItem("Copy");
        MenuItem eraser = new MenuItem("Erase");
        Menu shape = new Menu("Draw Shape");
        MenuItem line = new MenuItem("Line");
        MenuItem rect = new MenuItem("Rectangle");
        MenuItem free = new MenuItem("Free Draw");
        MenuItem oval = new MenuItem("Ellipse");
        MenuItem circ = new MenuItem("Circle");

        shape.getItems().addAll(line, rect, free, oval, circ);
        toolMenu.getItems().addAll(copier, cutter, eraser);

        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        helpMenu.getItems().addAll(about);

        pinnedMenu = new MenuBar(fileMenu,toolMenu,shape,helpMenu); //Plopping the menu pull-downs onto the menuBar

    //if mode = ____, then add needed options to menuBar

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
            Platform.exit();
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
                    placedImgView.setFitWidth(img.getWidth());
                    placedImgView.setFitHeight(img.getHeight());
                    placedImgView.setPreserveRatio(true);
                    gridPane.setAlignment(Pos.CENTER);

                    gc = imgCanv.getGraphicsContext2D();
                    gc.getCanvas().setWidth(img.getWidth());
                    gc.getCanvas().setHeight(img.getHeight());
                    gc.drawImage(img, IMG_POS_X,IMG_POS_Y, img.getWidth(),img.getHeight());
                    gridPane.add(imgCanv,0,0);
                    imgInserted = true;

                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });

        about.setOnAction((e) -> {
            InfoPopup aboutPop = new InfoPopup(primaryStage);
        });

        imgCanv.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.isPrimaryButtonDown()) {
                        gc.beginPath();
                        x0 = event.getX();
                        y0 = event.getY();
                        primaryJustClicked = true;
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
                    gc.closePath();
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
//TODO: add if state for each type of shape
        if(drawMode == 1){
//Place line between x0,y0 & x1,y1
            gc.strokeLine(x0,y0,x1,y1);
        }
        else if(drawMode==2){
            if(fill){
                gc.fillRect(x0, y0, Math.abs(x1-x0), Math.abs(y0-y1));
            }
            //place rectangle between opposite corners x0,y0 & x1,y1
//TODO: allow drawing rect from corner other than top left
            gc.strokeRect(x0, y0, Math.abs(x1-x0), Math.abs(y0-y1));
        }
        else if(drawMode==4){ //Ellipse
            if(fill){
                gc.fillOval(x0,y0,Math.abs(x1-x0), Math.abs(y0-y1));
            }
            gc.strokeOval(x0,y0,Math.abs(x1-x0), Math.abs(y0-y1));
        }
        else if(drawMode==5) { //Circle
            double w = Math.abs(x1 - x0);
            //double h = Math.abs(x1 - x0);
            if (fill) {
                gc.fillOval(x0, y0, w, w);
            }
            gc.strokeOval(x0, y0, w, w);
        }
        return true;
    }
    void setLineWidth(double newLineW){lineWidth=newLineW;}
    boolean updateMenus(){
        return false;
//TODO: implement this to check drawMode (and other?) to adjust the menu buttons
    }
    public Canvas getCanv(){return imgCanv;}

}
