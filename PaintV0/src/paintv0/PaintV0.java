/* Project - OO Development with Pain(t) 
 * Author - Joe Leveille
 * Sprint #01 - 8/28/19 to 9/4/19
 * Release Notes in //PaintV0/ExtraDocs/ReleaseNotes.md
 */
package paintv0;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class PaintV0 extends Application {
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane(); //Create the blank grid
        
        BorderPane bordPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        bordPane.setPrefSize(INIT_WINDOW_WIDTH,INIT_WINDOW_HEIGHT);
            
        ScrollBar vertScroll = new ScrollBar();
        vertScroll.setOrientation(Orientation.VERTICAL);
        ScrollBar horizScroll = new ScrollBar();
        horizScroll.setOrientation(Orientation.HORIZONTAL);
//TODO: Connect scroll bar to control the page
        
        ImageView placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();
        
        
        Button openFileBtn = new Button();
        openFileBtn.setText("Open Image File");
        
        
        MenuBar topMenu = new MenuBar();        //Create a menu bar to contain all menu pull-downs
        final Menu fileMenu = new Menu("File");    //Populate the first menu pull-down - File
        MenuItem imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        MenuItem openBtn = new MenuItem("Open Image");
        fileMenu.getItems().add(imageSave);
        fileMenu.getItems().add(openBtn);
        fileMenu.getItems().add(exitBtn);
        
        final Menu toolMenu = new Menu("Tools"); //Populate the next menu pull-down - Options
        MenuItem cutter = new MenuItem("Cut");
        MenuItem eraser = new MenuItem("Erase");
        toolMenu.getItems().add(cutter);
        toolMenu.getItems().add(eraser);
        
        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        
        topMenu.getMenus().addAll(fileMenu, toolMenu, helpMenu); //Plopping the menu pull-downs onto the menuBar
        bordPane.setTop(topMenu);
        bordPane.setCenter(gridPane);
        bordPane.setRight(vertScroll);
        bordPane.setBottom(horizScroll);
                
        exitBtn.setOnAction((e)->{ //Define the behavior on click for exit button
            Platform.exit();
        });
        
        openBtn.setOnAction(new EventHandler<ActionEvent>(){ //This function defines the action when open file is clicked
            @Override 
            public void handle(ActionEvent e) {
                openFile.setInitialDirectory(new File("../../"));
                openFile.setTitle("Open File");
                openFile.getExtensionFilters().addAll(              //Including filters for extension types
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"),
                    new ExtensionFilter("Text Files", "*.txt"),
                    new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                    new ExtensionFilter("All Files", "*.*")
                );
                File insImg = openFile.showOpenDialog(primaryStage);
                if (insImg != null) {
                    try {
                        InputStream io = new FileInputStream(insImg);
                        Image img = new Image(io);

        //TODO: Image scaling
                        placedImgView.setImage(img);        //Specifying placement & sizing of selected image
                        placedImgView.setFitWidth(INIT_WINDOW_WIDTH);
                        placedImgView.setFitHeight(INIT_WINDOW_HEIGHT);
                        placedImgView.setPreserveRatio(true);
                        gridPane.add(placedImgView, 4,4);
                    } catch (IOException ex) {
                        System.out.println("Error!");
                    }
                }
            }
        });
        
    //TODO: outsource button handlers to buttonHandlers.java
        imageSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
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
            }
        });
                      
        Scene scene = new Scene(bordPane); //Placing the grid on the screen

        primaryStage.setTitle("Paint v0"); //Set the window title text
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
//POSSIBLE TODO: USE CANVAS FOR IMAGE?
//POSSIBLE TODO: use scene.getWidth()/getHeight() for properly scaling elements
    public static void main(String[] args) {
        launch(args);
    }
    
}
