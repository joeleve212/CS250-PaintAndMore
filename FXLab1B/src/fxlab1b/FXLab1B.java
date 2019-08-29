package fxlab1b;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * @author Joe Leveille
 */
public class FXLab1B extends Application {
    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane(); //Create the blank grid
        gridPane.setMinSize(400, 200);  //and set it's attributes
        gridPane.setVgap(5); 
        gridPane.setHgap(5);       
        gridPane.setAlignment(Pos.CENTER);         
        
        Button btn = new Button(); //Create the button
        btn.setText("Calculate");
                      
        Text xLabel = new Text("X: ");  //Creating each label and field
        Text yLabel = new Text("Y: ");
        Text outLabel = new Text("Answer: ");
        TextField xInput = new TextField();
        TextField yInput = new TextField();
        
        gridPane.add(xLabel, 0, 0); //Adding each component to the grid
        gridPane.add(xInput, 1, 0);
        gridPane.add(yLabel, 0, 1);
        gridPane.add(yInput, 1, 1);
        gridPane.add(outLabel, 1, 2);
        gridPane.add(btn, 3, 2);
        
        btn.setOnAction(new EventHandler<ActionEvent>() { //function for what happens on a button press
            @Override
            public void handle(ActionEvent event) {
                try{//making sure to only print an answer when ints are involved
                    outLabel.setText("Answer: "+ Integer.parseInt(xInput.getCharacters().toString())* Integer.parseInt(yInput.getCharacters().toString()));
                }
                catch(Exception exp){ //Yelling at users that don't put in an int
                    System.out.println("You entered a non-integer!");
                }
            }
        });
        
        Scene scene = new Scene(gridPane); //Placing the grid on the screen

        primaryStage.setTitle("Multiplier"); //Set the window title
        primaryStage.setScene(scene);
        primaryStage.show(); //opening the window on screen
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
