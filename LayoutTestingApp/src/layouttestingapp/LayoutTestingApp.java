/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layouttestingapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.geometry.Pos.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Joe Leveille
 */
public class LayoutTestingApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane bordPane = new BorderPane();
        bordPane.setPrefSize(500,400);
        GridPane grid = new GridPane();
        ToolBar tools = new ToolBar(
            //new Separator(true),
            new Button("Clean"),
            new Button("Compile"),
            new Button("Run"),
            //new Separator(true),
            new Button("Debug"),
            new Button("Profile")
        );
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        grid.add(btn, 3, 3);
        //grid.add(btn, 2, 2);
        
        
        bordPane.setTop(tools);
        //bordPane.setBottom(btn);
        bordPane.setCenter(grid);
        bordPane.setAlignment(tools, CENTER);
        
        Scene scene = new Scene(bordPane);
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
