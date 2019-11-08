package paintv0;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Stack;

/**
 * The PaintV0 program is meant to import, edit and
 * save custom images. This is the main class that
 * creates the program window and it's components
 * Release Notes in //PaintV0/ExtraDocs/ReleaseNotes.txt
 *
 * @author  Joe Leveille
 * @version 1.0
 * @since   2019-10-02
 */
public class PaintV0 extends Application {
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
    public ColorPicker outlineColor, fillColor;
    public TextField textInput;
    public Spinner<Integer> widthChoose;
    public TopMenus menus;
    private int spinnerMin = 1, spinnerMax = 100, spinnerInit = 3;
    private String INIT_TIMER_VAL = "120";
    private Canvas imgCanv = new Canvas(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT);
    private Stack<WritableImage> prevVersions = new Stack<>();
    private Stack<WritableImage> undidVersions = new Stack<>();
    @Override
    public void start(Stage primaryStage) {
        Region target = new StackPane(imgCanv);
        Group gr = new Group(target);
        BorderPane bordPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        bordPane.setCenter(gr);

        ScrollPane scrollPane = new ScrollPane(bordPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button undoBtn = new Button("Undo");
        Button redoBtn = new Button("Redo");

        widthChoose = new Spinner<Integer>(spinnerMin,spinnerMax,spinnerInit);
        widthChoose.setEditable(true);
        outlineColor = new ColorPicker(Color.BLACK); //set default outline color
        fillColor = new ColorPicker(Color.BLACK);//set default fill color
        textInput = new TextField("Text Input");
        Text timerVal = new Text(INIT_TIMER_VAL);
        ToolBar windowBar = new ToolBar(widthChoose,outlineColor,fillColor,textInput,undoBtn,redoBtn,timerVal);

        menus = new TopMenus(primaryStage, gr, prevVersions, windowBar);
        MenuBar topMenu = menus.getMenuBar();        //Create a menu bar to contain all menu pull-downs

        VBox screenContent = new VBox(topMenu, scrollPane, windowBar); //Placing menuBar above pane that contains the rest
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        Scene scene = new Scene(screenContent);

        FunctionHandlers handlers = new FunctionHandlers(menus, primaryStage, gr, timerVal);

        primaryStage.setTitle("Paint v0"); //Set the window title text
        primaryStage.setScene(scene);      //and build stage before showing
        primaryStage.sizeToScene();
        primaryStage.show();
        /**
         * These methods handle the following settings on the bottom tool bar:
         * line color, fill color, line width & the text input.
         *
         * @param Event event is the only parameter for the handler
         */
        outlineColor.setOnAction(event ->  { //trigger color picker when button is clicked
            Color c = outlineColor.getValue();
            menus.setShapeLineColor(c);
        });
        fillColor.setOnAction(event -> {
            Color fillC = fillColor.getValue();
            menus.setShapeFillColor(fillC);
        });
        widthChoose.setOnMouseClicked((event) -> { //Grabbing new width setting and updating Line width
            double lineWidth = widthChoose.getValue();
            menus.setLineWidth(lineWidth);
        });
        textInput.setOnAction(e->{
            String currString = textInput.getText();
            menus.setInputString(currString);
        });
        /**
         * The undo and redo handlers only call their
         * respective functions from below.
         *
         * @param Event event is the only parameter for these handlers
         */
        undoBtn.setOnAction((event) -> undo());
        redoBtn.setOnAction((event) -> redo());
        /**
         * This method handles keyboard
         * shortcuts that occur on the main window of the program.
         * This includes CTRL+S,CTRL+H,CTRL+Z & ESC.
         *
         * @param KeyEvent event is the only parameter for the handler
         */
        scene.setOnKeyPressed((event) -> {
            KeyCode press = event.getCode(); //store pressed key in variable for reuse
            imgCanv = menus.getCanv(); //grab imgCanv from TopMenus class
            if (press == KeyCode.ESCAPE) { //ESC exits any drawing mode
                menus.setDrawMode(0);
                menus.updateMenus();
            } else if (press == KeyCode.S && event.isControlDown()) { //CTRL+S updates the image in the existing file
                if(menus.imageHasBeenSaved){ //if it was previously saved,
                    handlers.saveImage(); //then update save file
                }else{
                    menus.imageSave.fire(); //Save as
                }

            } else if (press == KeyCode.Z && event.isControlDown()) {
                undo();
            } else if(press == KeyCode.H && event.isControlDown()) {
                if(timerVal.isVisible()) {
                    timerVal.setVisible(false);
                } else{
                    timerVal.setVisible(true);
                }
            }
        });
        /**
         * This method handles
         * when the user closes the program -
         * makes sure that the user saves their work before closing.
         *
         * @param Event event is the only parameter for the handler
         */
        primaryStage.setOnCloseRequest((event) -> {
            if (!menus.imageHasBeenSaved) { //if the image has not saved,
                event.consume(); //do nothing with the close request
            }
            menus.toolTimer.end(); //Trigger toolTimer thread to make .log file
            InfoPopup smartSave = new InfoPopup(primaryStage, "exitSave");
            smartSave.saveBtn.setOnAction(e->{ //when save button on popup is pressed, do the same as CTRL + S
                if(menus.imageHasBeenSaved){ //If this is the first time image is being saved
                    handlers.saveImage(); //save on saveBtn press
                }else {
                    menus.imageSave.fire(); //Trigger the save as function
                }
            });
        });
        /**
         * This method handles scrolling,
         * which is used to trigger zooming.
         *
         * @param Event event is the only parameter for the handler
         */
        bordPane.setOnScroll(event -> {
            if (event.isControlDown()) { //CTRL + Scroll triggers zooming
                boolean scrollDown;
                scrollDown = (event.getDeltaY() < 0); //store direction of scroll before consuming event
                event.consume(); //consuming event
                double xCanvScale = imgCanv.getScaleX(); //grabbing current canvas scale
                double yCanvScale = imgCanv.getScaleY(); //in each direction
                double scaleMod = .1; //The size step in scale to take each scroll-click
                if(scrollDown) {
                    imgCanv.setScaleX(xCanvScale - scaleMod); //Zoom in
                    imgCanv.setScaleY(yCanvScale - scaleMod);
                }else{
                    imgCanv.setScaleX(xCanvScale + scaleMod); //Zoom out
                    imgCanv.setScaleY(yCanvScale + scaleMod);
                }
            }
        });
    }

    /**
     * Reverts the canvas to the previous version before
     * the most recent change.
     */
    public void undo(){
        if (prevVersions.size()>1){
            WritableImage removed = prevVersions.peek();
            prevVersions.pop();
            undidVersions.push(removed);
            menus.setCanvVersion(prevVersions.peek());
            menus.imageHasBeenSaved=false;
        }
    }
    /**
     * Redoes the change that was most recently undone.
     */
    public void redo(){
        if(undidVersions.size()>0){
            WritableImage redid = undidVersions.peek();
            undidVersions.pop();
            menus.setCanvVersion(redid);
            prevVersions.push(redid);
            menus.imageHasBeenSaved=false;
        }
    }
    /**
     * Gives the top item on the stack of canvas snapshots.
     *
     * @return WritableImage is given by looking at the top item on the version stack
     */
    public WritableImage getCurrVersion(){
        return prevVersions.peek();
    }
    public static void main(String[] args) {
        launch(args);
    }
}