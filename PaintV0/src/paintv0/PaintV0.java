/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintv0;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
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
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.showOpenDialog(stage);
    //TODO: User choose the file to open
        //FileView? FileChooser?
    //TODO: Menu bar in screen
        //MenuButton, 
    MenuBar topMenu = new MenuBar();
    final Menu menu1 = new Menu("File");
    final Menu menu2 = new Menu("Options");
    final Menu menu3 = new Menu("Help");
    topMenu.getMenus().addAll(menu1, menu2, menu3);
    //TODO: User save opened file
    
    //TODO: Close button to shut down program
    //TODO: Update release notes
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
