/* Project - OO Development with Pain(t)
 * Author - Joe Leveille
 * Sprint #02 - 9/6/19 to 9/13/19
 * Release Notes in //PaintV0/ExtraDocs/ReleaseNotes.txt
 */
package paintv0;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.EventHandler;

public class PaintV0 extends Application {
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
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

        VBox screenContent = new VBox(topMenu, scrollPane);
        Scene scene = new Scene(screenContent);
        //    menuBox.getChildren().addAll(topMenu);
//CLEAN: topMenu.prefWidthProperty().bind(primaryStage.widthProperty());
//CLEAN:bordPane.setTop(topMenu);
        bordPane.setCenter(gridPane);

//TODO: Make ESC key set drawMode to 0
//        scene.onKeyPressed(new EventHandler<KeyEvent>() {
//            public void handle(final KeyEvent keyEvent){
//                if(keyEvent.getCode() == KeyCode.ESCAPE){
//                    menus.setDrawMode(0);
//                }
//            }
//        });

        primaryStage.setTitle("Paint v0"); //Set the window title text
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
//TODO: use scene.getWidth()/getHeight() for properly scaling elements
    public static void main(String[] args) {
        launch(args);
    }

}
