/* Project - OO Development with Pain(t)
 * Author - Joe Leveille
 * Sprint #02 - 9/6/19 to 9/13/19
 * Release Notes in //PaintV0/ExtraDocs/ReleaseNotes.txt
 */
package paintv0;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.EventHandler;

import javax.imageio.ImageIO;
import java.io.IOException;

public class PaintV0 extends Application {
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
    private Canvas imgCanv;
    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane(); //Create the blank grid

        BorderPane bordPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        bordPane.setPrefSize(INIT_WINDOW_WIDTH,INIT_WINDOW_HEIGHT);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(INIT_WINDOW_WIDTH,INIT_WINDOW_HEIGHT);
        scrollPane.setContent(bordPane);

        TopMenus menus = new TopMenus(primaryStage, gridPane);
        MenuBar topMenu = menus.getMenuBar();        //Create a menu bar to contain all menu pull-downs

        bordPane.setCenter(gridPane);

        ChoiceBox widthChoose = new ChoiceBox();
        widthChoose.setValue("1px"); //set default width

        ColorPicker colorChoose = new ColorPicker(Color.BLACK); //set default color
        colorChoose.setOnAction(new EventHandler() { //trigger color picker when button is clicked
            public void handle(Event t) {
                Color c = colorChoose.getValue();
                menus.setShapeColor(c);
            }
        });
//TODO: Allow custom input for width choosing, possibly diff units
        widthChoose.getItems().addAll("1px", "2px", "3px", "5px", "10px"); //A few default widths to choose
//TODO: Place necessary controls on toolbar for each edit tool
        ToolBar windowBar = new ToolBar(widthChoose, colorChoose); //Creates the toolbar to hold both choosers
        //bordPane.setTop(windowBar);

        VBox screenContent = new VBox(topMenu, scrollPane, windowBar); //Placing menuBar above pane that contains the rest
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        Scene scene = new Scene(screenContent);

        primaryStage.setTitle("Paint v0"); //Set the window title text
        primaryStage.setScene(scene);      //and build stage before showing
        primaryStage.sizeToScene();
        primaryStage.show();

        scene.setOnKeyPressed((event)-> {
            KeyCode press = event.getCode(); //store pressed key in variable for reuse
            if (press == KeyCode.ESCAPE) { //ESC exits any drawing mode
                menus.setDrawMode(0);
            } else if (press == KeyCode.S && event.isControlDown()) { //CTRL+S updates the image in the existing file
                try {
                    imgCanv = menus.getCanv(); //grab imgCanv from TopMenus class
                    WritableImage wImage = new WritableImage((int) imgCanv.getWidth(), (int) imgCanv.getHeight());
                    imgCanv.snapshot(null, wImage);
                    ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), menus.ext, menus.savedImg);
                } catch (IOException ex) { //Throw a simple error if saving dies
                    System.out.println("Save Failed!");
                }
            }
        });

        widthChoose.setOnAction((event)-> { //Grabbing new width setting and updating Line width
            String widthVal = widthChoose.getValue().toString();
            //pulling the numeric value of width w/o units
            int lineWidth = Integer.parseInt(widthVal.substring(0, widthVal.lastIndexOf("p")));
            menus.setLineWidth((double) lineWidth);
        });
    }
    public Canvas getCanv(){return imgCanv;}
//TODO: use scene.getWidth()/getHeight() for properly scaling elements
    public static void main(String[] args) {
        launch(args);
    }

}