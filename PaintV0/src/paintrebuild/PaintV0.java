/* Project - OO Development with Pain(t)
 * Author - Joe Leveille
 * Sprint #02 - 9/6/19 to 9/13/19
 * Release Notes in //PaintV0/ExtraDocs/ReleaseNotes.txt
 */
package paintv0;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.EventHandler;


import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Stack;

public class PaintV0 extends Application {
    public int INIT_WINDOW_WIDTH = 400;
    public int INIT_WINDOW_HEIGHT = 400;
    public boolean imageHasBeenSaved = false;
    public TopMenus menus;
    private Canvas imgCanv = new Canvas(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT);
    private Stack<WritableImage> prevVersions = new Stack<>();
    private Stack<WritableImage> undidVersions = new Stack<>();
    private TextField textInput;
    @Override
    public void start(Stage primaryStage) {
        Region target = new StackPane(imgCanv);
        Group gr = new Group(target);
        BorderPane bordPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        bordPane.setCenter(gr);

        ScrollPane scrollPane = new ScrollPane(bordPane);
//TODO: Decide on these 2 lines:
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        ChoiceBox widthChoose = new ChoiceBox();
        widthChoose.setValue("1px"); //set default width

        ColorPicker outlineColor = new ColorPicker(Color.BLACK); //set default outline color
        ColorPicker fillColor = new ColorPicker(Color.BLACK);//set default fill color
        textInput = new TextField("Text Input");
        Button undoBtn = new Button("Undo"); //Self explanatory
        Button redoBtn = new Button("Redo"); //Self explanatory
        outlineColor.setOnAction(new EventHandler() { //trigger color picker when button is clicked
            public void handle(Event t) {
                Color c = outlineColor.getValue();
                menus.setShapeLineColor(c);
            }
        });
        fillColor.setOnAction(event -> {
            Color fillC = fillColor.getValue();
            menus.setShapeFillColor(fillC);
        });
        undoBtn.setOnAction((event) -> {
            undo();
        });
        redoBtn.setOnAction((event) -> {
            redo();
        });
//TODO: Allow custom input for width choosing, possibly diff units
        widthChoose.getItems().addAll("1px", "2px", "3px", "5px", "10px"); //A few default widths to choose
//TODO: Place necessary controls on toolbar for each edit tool
        ToolBar windowBar = new ToolBar(widthChoose, outlineColor, fillColor, textInput, undoBtn, redoBtn); //Creates the toolbar to hold both choosers

        menus = new TopMenus(primaryStage, gr, prevVersions, windowBar);
        MenuBar topMenu = menus.getMenuBar();        //Create a menu bar to contain all menu pull-downs

        VBox screenContent = new VBox(topMenu, scrollPane, windowBar); //Placing menuBar above pane that contains the rest
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        Scene scene = new Scene(screenContent);


        primaryStage.setTitle("Paint v0"); //Set the window title text
        primaryStage.setScene(scene);      //and build stage before showing
        primaryStage.sizeToScene();
        primaryStage.show();

        scene.setOnKeyPressed((event) -> {
            KeyCode press = event.getCode(); //store pressed key in variable for reuse
            imgCanv = menus.getCanv(); //grab imgCanv from TopMenus class
            if (imgCanv == null) {
//TODO: make CTRL+S saveAs first time                menus.imgSave.handle();
                return;
            }
            if (press == KeyCode.ESCAPE) { //ESC exits any drawing mode
                menus.setDrawMode(0);
                menus.updateMenus();
            } else if (press == KeyCode.S && event.isControlDown()) { //CTRL+S updates the image in the existing file
                saveImage();
            } else if (press == KeyCode.Z && event.isControlDown()) {
                undo();
            }
        });
        widthChoose.setOnAction((event) -> { //Grabbing new width setting and updating Line width
            String widthVal = widthChoose.getValue().toString();
            //pulling the numeric value of width w/o units
            int lineWidth = Integer.parseInt(widthVal.substring(0, widthVal.lastIndexOf("p")));
            menus.setLineWidth((double) lineWidth);
        });
        primaryStage.setOnCloseRequest((event) -> {
            if (!imageHasBeenSaved) {
                event.consume();
            }
            //TODO: implement smart save (exit button checks if work is saved)
            InfoPopup smartSave = new InfoPopup(primaryStage, "exitSave");
            smartSave.saveBtn.setOnAction(e->{
                if(!imageHasBeenSaved){ //If this is the first time image is
                    //TODO: call saveAs function
                }else {
                    saveImage(); //save on saveBtn press
                }
            });
        });
        bordPane.setOnScroll(event -> {
            if (event.isControlDown()) { //CTRL + Scroll triggers zooming
                boolean scrollDown;
                if(event.getDeltaY() < 0){ //store direction of scroll before consuming event
                    scrollDown = false;
                }else{
                    scrollDown = true;
                }
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
                menus.placedImgView.setScaleX(imgCanv.getScaleX()); //scale the imageView same as the canvas
                menus.placedImgView.setScaleY(imgCanv.getScaleY());
                target.setScaleX(imgCanv.getScaleX()); //scale the Region same as the canvas
                target.setScaleY(imgCanv.getScaleY());
                scrollPane.setHvalue(imgCanv.getWidth()); //attempt to make the scrollPane the same size as the canvas
                scrollPane.setVvalue(imgCanv.getHeight());
            }
        });
        MainScreenButtonHandlers handlers = new MainScreenButtonHandlers(menus, primaryStage, gr);
    }
    public Canvas getCanv(){return imgCanv;}
    public void saveImage(){ //TODO: export to handlers file?
        try{
            WritableImage wImage = new WritableImage((int) imgCanv.getWidth(), (int) imgCanv.getHeight());
            imgCanv.snapshot(null, wImage);
            ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), menus.ext, menus.savedImg);
            imageHasBeenSaved=true;
        } catch (IOException ex) { //Throw a simple error if saving dies
            System.out.println("Save Failed!");
        }
    }
    public void undo(){ //TODO: export to handlers file?
        if (prevVersions.size()>1){
            WritableImage removed = prevVersions.peek();
            prevVersions.pop();
            undidVersions.push(removed);
            menus.setCanvVersion(prevVersions.peek());
            imageHasBeenSaved=false;
        }
    }
    public void redo(){ //TODO: export to handlers file?
        if(undidVersions.size()>0){
            WritableImage redid = undidVersions.peek();
            undidVersions.pop();
            menus.setCanvVersion(redid);
            prevVersions.push(redid);
            imageHasBeenSaved=false;
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}