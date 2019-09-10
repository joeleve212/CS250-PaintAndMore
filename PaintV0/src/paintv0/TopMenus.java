package paintv0;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

import javax.imageio.ImageIO;
import java.awt.*;
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

    Image img;
    Canvas imgCanv;
    TopMenus(Stage primaryStage, GridPane gridPane){

        int DEFAULT_CANV_W = 400;
        int DEFAULT_CANV_H = 400;
        topMenu = new MenuBar();
        ImageView placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();

        final javafx.scene.control.Menu fileMenu = new javafx.scene.control.Menu("File");    //Populate the first menu pull-down - File
        javafx.scene.control.MenuItem imageSave = new javafx.scene.control.MenuItem("Save Image");
        javafx.scene.control.MenuItem exitBtn = new javafx.scene.control.MenuItem("Exit Program");
        javafx.scene.control.MenuItem openBtn = new javafx.scene.control.MenuItem("Open Image");
        fileMenu.getItems().add(imageSave);
        fileMenu.getItems().add(openBtn);
        fileMenu.getItems().add(exitBtn);

        final javafx.scene.control.Menu toolMenu = new javafx.scene.control.Menu("Tools"); //Populate the next menu pull-down - Options
        javafx.scene.control.MenuItem cutter = new javafx.scene.control.MenuItem("Cut");
        javafx.scene.control.MenuItem copier = new javafx.scene.control.MenuItem("Copy");
        javafx.scene.control.MenuItem line = new javafx.scene.control.MenuItem("Line");
        javafx.scene.control.MenuItem eraser = new javafx.scene.control.MenuItem("Erase");
        toolMenu.getItems().add(line);
        toolMenu.getItems().add(copier);
        toolMenu.getItems().add(cutter);
        toolMenu.getItems().add(eraser);

        final javafx.scene.control.Menu helpMenu = new javafx.scene.control.Menu("Help"); //Creating Help pull-down for later use
        javafx.scene.control.MenuItem about = new MenuItem("About");
        helpMenu.getItems().add(about);

        topMenu.getMenus().addAll(fileMenu, toolMenu, helpMenu); //Plopping the menu pull-downs onto the menuBar

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
                    imgCanv = new Canvas(DEFAULT_CANV_W,DEFAULT_CANV_H);


                    //TODO: Image scaling
                    placedImgView.setImage(img);        //Specifying placement & sizing of selected image
                    placedImgView.fitWidthProperty();
                    placedImgView.fitHeightProperty();
                    placedImgView.setPreserveRatio(true);
                    gridPane.add(placedImgView, 0, 0);
                    gridPane.setAlignment(Pos.CENTER);

                    GraphicsContext gc = imgCanv.getGraphicsContext2D();
                    gc.drawImage(img, img.getWidth(),img.getHeight());
                   // gridPane.add(imgCanv,0,0);

                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });

        about.setOnAction((e) -> {
            InfoPopup aboutPop = new InfoPopup(primaryStage);
        });

//        imgCanv.addEventHandler(MouseEvent.MOUSE_PRESSED,
//                (event)->{
//                    double x0 = event.getX();
//                    double y0 = event.getY();
//                }
//        );
    }
    MenuBar getMenuBar(){return topMenu;}

}
