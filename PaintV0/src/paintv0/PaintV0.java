/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintv0;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Joe Leveille
 */
public class PaintV0 extends Application {
    
    //TODO: View an image from a file  - https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
        //ImageIO?
    /*FileChooser fileChooser = new FileChooser();
    FileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Text Files", "*.txt"),
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
            new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
            new ExtensionFilter("All Files", "*.*"));
    File selectedFile = fileChooser.showOpenDialog(mainStage);
    if (selectedFile != null) {
       mainStage.display(selectedFile);
    }*/
        
    //TODO: User choose the file to open
        //FileView? FileChooser?
    
    //TODO: User save opened file
    
    //TODO: Close button to shut down program
    //TODO: Update release notes
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        
        GridPane gridPane = new GridPane(); //Create the blank grid
        gridPane.setMinSize(400, 400);  //and set it's attributes
        gridPane.setVgap(5); 
        gridPane.setHgap(5);       
        gridPane.setAlignment(Pos.CENTER); 
        
        
        /*btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        */
        btn.setOnAction((e)->{
            FileChooser openFile = new FileChooser();
            openFile.setTitle("Open File");
            File file = openFile.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    InputStream io = new FileInputStream(file);
                    Image img = new Image(io);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });
        
        //TODO: Menu bar in screen
            //MenuButton, 
        MenuBar topMenu = new MenuBar();
        final Menu menu1 = new Menu("File");
        final Menu menu2 = new Menu("Options");
        final Menu menu3 = new Menu("Help");
        topMenu.getMenus().addAll(menu1, menu2, menu3);
        
        gridPane.add(btn, 3, 2);
        gridPane.add(topMenu, 0, 0);
        
        Scene scene = new Scene(gridPane); //Placing the grid on the screen

        primaryStage.setTitle("Paint v0"); //Set the window title
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
