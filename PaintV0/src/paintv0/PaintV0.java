/* Project - OO Development with Pain(t) 
 * Author - Joe Leveille
 * Sprint #02 - 9/6/19 to 9/13/19
 * Release Notes in //PaintV0/ExtraDocs/ReleaseNotes.txt
 */
package paintv0;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class PaintV0 extends Application {
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
    public String OPENER_FILE_LOC = "../..";
    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane(); //Create the blank grid
        
        BorderPane bordPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        bordPane.setPrefSize(INIT_WINDOW_WIDTH,INIT_WINDOW_HEIGHT);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(INIT_WINDOW_WIDTH,INIT_WINDOW_HEIGHT);
        scrollPane.setContent(bordPane);
        
        Scene scene = new Scene(scrollPane);
        

        ImageView placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();
<<<<<<< Updated upstream
        
        
        Button openFileBtn = new Button();
        openFileBtn.setText("Open Image File");

    //    HBox menuBox=new HBox();
        MenuBar topMenu = new MenuBar();        //Create a menu bar to contain all menu pull-downs
    //    menuBox.getChildren().addAll(topMenu);
//CLEAN: HBox.setHGrow(topMenu, Priority.ALWAYS);
//CLEAN: topMenu.prefWidthProperty().bind(primaryStage.widthProperty());
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
        MenuItem line = new MenuItem("Line");
        MenuItem eraser = new MenuItem("Erase");
        toolMenu.getItems().add(line);
        toolMenu.getItems().add(copier);
        toolMenu.getItems().add(cutter);
        toolMenu.getItems().add(eraser);
        
        final Menu helpMenu = new Menu("Help"); //Creating Help pull-down for later use
        MenuItem about = new MenuItem("About");
        helpMenu.getItems().add(about);
=======
>>>>>>> Stashed changes

        TopMenus topMenu = new TopMenus(primaryStage,placedImgView,openFile,gridPane);
        MenuBar menus = topMenu.getMenuBar();

         //Plopping the menu pull-downs onto the menuBar
        bordPane.setTop(menus);
        bordPane.setCenter(gridPane);


<<<<<<< Updated upstream
        openBtn.setOnAction((e)->{ //This function defines the action when open file is clicked
            openFile.setInitialDirectory(new File(OPENER_FILE_LOC));
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
                    Canvas imgCanv = new Canvas(); // Canvas imgCanv = new Canvas(); //


                    //TODO: Image scaling
                    placedImgView.setImage(img);        //Specifying placement & sizing of selected image
                    placedImgView.setFitWidth(INIT_WINDOW_WIDTH);
                    placedImgView.setFitHeight(INIT_WINDOW_HEIGHT);
                    placedImgView.setPreserveRatio(true);
                    gridPane.add(placedImgView, 0, 0);
                    gridPane.setAlignment(Pos.CENTER);
                    GraphicsContext gc = imgCanv.getGraphicsContext2D();
                    //gridPane.setMargin(imgCanv, new Insets(10,10,10,10));
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });
        
    //TODO: outsource button handlers to buttonHandlers.java ???
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
=======
    //TODO: outsource button handlers to buttonHandlers.java
>>>>>>> Stashed changes


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
