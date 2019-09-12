package paintv0;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TopMenus {
    MenuBar topMenu;
    public String OPENER_FILE_LOC = "../../../Phone Backups";
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
    int DEFAULT_CANV_W = 400;
    int DEFAULT_CANV_H = 400;
    GraphicsContext gc;
    Image img;
    Canvas imgCanv = new Canvas(DEFAULT_CANV_W,DEFAULT_CANV_H);
    private double x0,y0,x1,y1;
    public boolean imgInserted = false;
    public int drawMode = 0; //0 for none, 1 for line, 2 for rect, 3 for circ - Index in 'draw shape' list
    TopMenus(Stage primaryStage, GridPane gridPane){


        ImageView placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();

        final Menu fileMenu = new Menu("File");    //Populate the first menu pull-down - File
        MenuItem imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        MenuItem openBtn = new MenuItem("Open Image");
        fileMenu.getItems().add(imageSave);
        fileMenu.getItems().add(openBtn);
        fileMenu.getItems().add(exitBtn);

        final Menu toolMenu = new Menu("Tools"); //Populate the next menu pull-down - Options
        MenuItem cutter = new MenuItem("Cut");
        MenuItem copier = new MenuItem("Copy");
        Menu shape = new Menu("Draw Shape");
        MenuItem line = new MenuItem("Line");
        MenuItem eraser = new MenuItem("Erase");
        shape.getItems().addAll(line);
        toolMenu.getItems().addAll(copier, cutter, eraser, shape);

        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        helpMenu.getItems().add(about);

        ChoiceBox widthChoose = new ChoiceBox();
        widthChoose.getItems().addAll("1px", "2px", "3px");
//TODO: implement width choosing and add to screen
        ToolBar windowBar = new ToolBar(widthChoose);

        topMenu = new MenuBar(fileMenu,toolMenu,helpMenu); //Plopping the menu pull-downs onto the menuBar


    //if mode = ____, then add ____ options to menuBar

        imageSave.setOnAction((e)->{
            System.out.println("Saving image file...");
            FileChooser saveImageChoose = new FileChooser();
            saveImageChoose.setTitle("Save Image As");
            saveImageChoose.getExtensionFilters().addAll( //allow saving in different file formats
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("BMP Files", "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF Files", "*.gif"));
            File savedImg = saveImageChoose.showSaveDialog(null);
            String name = savedImg.getName();
            String ext = name.substring(1+name.lastIndexOf(".")).toLowerCase(); //grab only the file extension of the image

            BufferedImage bImage = SwingFXUtils.fromFXImage(placedImgView.getImage(), null);
            try {           //attempt to make a save file from the inserted image
                ImageIO.write(bImage, ext, savedImg);
            } catch (IOException o) {   //If the above line breaks, throw an exception
                throw new RuntimeException(o);
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
            if (insImg != null) {
                try {
                    InputStream io = new FileInputStream(insImg);
                    img = new Image(io);

                    //TODO: Image scaling
                    placedImgView.setImage(img);        //Specifying placement & sizing of selected image
                    placedImgView.setFitWidth(img.getWidth());
                    placedImgView.setFitHeight(img.getHeight());
                    placedImgView.setPreserveRatio(true);
                    //gridPane.add(placedImgView, 0, 0);
                    gridPane.setAlignment(Pos.CENTER);

                    gc = imgCanv.getGraphicsContext2D();
                    gc.getCanvas().setWidth(img.getWidth());
                    gc.getCanvas().setHeight(img.getHeight());
                    gc.drawImage(img, 0,0, img.getWidth(),img.getHeight());
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

        imgCanv.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>(){

                @Override
                public void handle(MouseEvent event) {
                    x0 = event.getX();
                    y0 = event.getY();
                }
            }
        );
        imgCanv.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>(){

                @Override
                public void handle(MouseEvent event) {
                    x1 = event.getX();
                    y1 = event.getY();
                    drawShape();
                    gc.save();
                }
            }
        );

        line.setOnAction((e)->{
            drawMode = 1;
            updateMenus();
        });
    }
    int getDrawMode(){return drawMode;}
    void setDrawMode(int x){drawMode = x;}
    MenuBar getMenuBar(){return topMenu;}
    boolean drawShape(){
//TODO: add if state for each type of shape
        if(drawMode == 1){
//TODO: Place line between x0,y0 & x1,y1
            //Line newLine = new Line(x0,y0,x1,y1);
            gc.strokeLine(x0,y0,x1,y1);
            System.out.println("Attempted to make line");
        }
        return true;
    }
    boolean updateMenus(){
        return false;
//TODO: implement this to check drawMode (and other?) to adjust the menu buttons
    }

}
