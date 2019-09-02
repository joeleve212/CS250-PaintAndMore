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
        
        ColumnConstraints column1 = new ColumnConstraints(); //Setting widths of columns to 
        column1.setPercentWidth(35);                        //percentages of the window width
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(15);
        gridPane.getColumnConstraints().addAll(column1, column2);
        
        //CLEANCanvas canvas = new Canvas(500, 700);
        //GraphicsContext selectImage = canvas.getGraphicsContext2D();
        
        ScrollBar vertScroll = new ScrollBar();
        vertScroll.setOrientation(Orientation.VERTICAL);
        //TODO: Connect this to control the page
        //TODO: Stretch this to the length of the page
        gridPane.add(vertScroll, 10, 1);
        
        ImageView resizeIm = new ImageView();
        
        
        Button openFileBtn = new Button();
        openFileBtn.setText("Open Image File");
        gridPane.add(openFileBtn, 1, 0);
        openFileBtn.setOnAction((e)->{
            //TODO: User choose the file to open         DONE
            FileChooser openFile = new FileChooser();
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
                    resizeIm.setImage(img);
                    resizeIm.setFitWidth(100);
                    resizeIm.setFitHeight(100);
                    resizeIm.setPreserveRatio(true);
                    //CLEAN resizeIm.drawImage();
                    //TODO: don't crop off part of image   DONE?
                    gridPane.add(resizeIm, 1,1);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });
        
        //TODO: Menu bar in screen                DONE
        MenuBar topMenu = new MenuBar();
        final Menu menu1 = new Menu("File");
        
//TODO: User save opened file
        MenuItem imageSave = new MenuItem("Save Image");
        menu1.getItems().add(imageSave);
        
        imageSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                System.out.println("Saving image file...");
        //TODO: save as fileChooser 
                FileChooser saveImage = new FileChooser();
                saveImage.setTitle("Save Image As");
                
                File savedIm = saveImage.showSaveDialog(primaryStage);
                //saveToFile(img);
                //CLEAN savedIm.createTempFile("Image", ".png");
                //ImageIO.write(new RenderedImage(img), String, File);
            }
        });
        
        final Menu menu2 = new Menu("Options");
        final Menu menu3 = new Menu("Help");
        topMenu.getMenus().addAll(menu1, menu2, menu3);
        gridPane.add(topMenu, 0, 0);
        
        //CLEAN gridPane.add(canvas, 0, 1);
        
        Scene scene = new Scene(gridPane); //Placing the grid on the screen

        primaryStage.setTitle("Paint v0"); //Set the window title
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        
//TODO: Close button to shut down program           DONE?
//TODO: Update release notes
    }
    
    public static void saveToFile(Image image) {
        File outputFile = new File("./");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
          ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
//POSSIBLE TODO: use scene.getWidth()/getHeight() for properly scaling elements
    public static void main(String[] args) {
        launch(args);
    }
    
}
