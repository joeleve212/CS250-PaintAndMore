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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class PaintV0 extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane(); //Create the blank grid
        gridPane.setMinSize(400, 400);  //and set it's attributes
        gridPane.setVgap(0); 
        gridPane.setHgap(5);       
        gridPane.setAlignment(Pos.TOP_LEFT); 
//TODO: implement stackPane w/ or borderPane?        
        ColumnConstraints column1 = new ColumnConstraints(); //Setting widths of columns to 
        column1.setPercentWidth(35);                        //percentages of the window width
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(15);
        gridPane.getColumnConstraints().addAll(column1, column2);
                
        ScrollBar vertScroll = new ScrollBar();
        vertScroll.setOrientation(Orientation.VERTICAL);
    //TODO: Connect this to control the page
    //TODO: Stretch this to the length of the page
        gridPane.add(vertScroll, 10, 1);
        
        ImageView placedImgView = new ImageView();
        FileChooser openFile= new FileChooser();
        
        
        Button openFileBtn = new Button();
        openFileBtn.setText("Open Image File");
        gridPane.add(openFileBtn, 1, 0);
        openFileBtn.setOnAction(new EventHandler<ActionEvent>(){
            //TODO: User choose the file to open         DONE
            @Override 
            public void handle(ActionEvent e) {
                openFile.setInitialDirectory(new File("../../"));
                openFile.setTitle("Open File");
                openFile.getExtensionFilters().addAll(
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
                        placedImgView.setImage(img);
                        placedImgView.setFitWidth(100);
                        placedImgView.setFitHeight(100);
                        placedImgView.setPreserveRatio(true);
                        gridPane.add(placedImgView, 1,1);
                    } catch (IOException ex) {
                        System.out.println("Error!");
                    }
                }
            }
        });
        
        MenuBar topMenu = new MenuBar();
        final Menu menu1 = new Menu("File");
        
        MenuItem imageSave = new MenuItem("Save Image");
        MenuItem exitBtn = new MenuItem("Exit Program");
        menu1.getItems().add(imageSave);
        menu1.getItems().add(exitBtn);
                
        exitBtn.setOnAction((e)->{
            Platform.exit();
        });
        
        //POSSIBLE CHANGE: outsource imageSave.setOnAction(e -> saveToFile(image));
        imageSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                System.out.println("Saving image file..."); 
                FileChooser saveImageChoose = new FileChooser();
                saveImageChoose.setTitle("Save Image As");
                saveImageChoose.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("BMP Files", "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF Files", "*.gif"));
                File savedImg = saveImageChoose.showSaveDialog(null);
                String name = savedImg.getName();
                String ext = name.substring(1+name.lastIndexOf(".")).toLowerCase(); //grab only the file extension of the image
                
                BufferedImage bImage = SwingFXUtils.fromFXImage(placedImgView.getImage(), null);
                try {
                  ImageIO.write(bImage, ext, savedImg);
                } catch (IOException o) {
                  throw new RuntimeException(o);
                }
            }
        });
        
        final Menu menu2 = new Menu("Options");
        final Menu menu3 = new Menu("Help");
        topMenu.getMenus().addAll(menu1, menu2, menu3);
        gridPane.add(topMenu, 0, 0);
        
        Scene scene = new Scene(gridPane); //Placing the grid on the screen

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
