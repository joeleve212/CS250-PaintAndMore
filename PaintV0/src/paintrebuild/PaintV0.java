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
    @Override
    public void start(Stage primaryStage) {
//        GridPane gridPane = new GridPane(); //Create the blank grid
        Region target = new StackPane(imgCanv);
        Group gr = new Group(target);
        BorderPane bordPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        //CLEAN?bordPane.setPrefSize(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT);
        bordPane.setCenter(gr);

        ScrollPane scrollPane = new ScrollPane(bordPane);
        //scrollPane.setPrefSize(INIT_WINDOW_WIDTH, INIT_WINDOW_HEIGHT);
//TODO: May want these 2 lines:
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        menus = new TopMenus(primaryStage, gr, prevVersions);
        MenuBar topMenu = menus.getMenuBar();        //Create a menu bar to contain all menu pull-downs

//        bordPane.setCenter(gridPane);

        ChoiceBox widthChoose = new ChoiceBox();
        widthChoose.setValue("1px"); //set default width

        ColorPicker outlineColor = new ColorPicker(Color.BLACK); //set default outline color
        ColorPicker fillColor = new ColorPicker(Color.BLACK);//set default fill color
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
        ToolBar windowBar = new ToolBar(widthChoose, outlineColor, fillColor, undoBtn, redoBtn); //Creates the toolbar to hold both choosers

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
                System.out.println("asdfasdf");
                saveImage();
                //TODO: implement saving on saveBtn press
            });
        });
        bordPane.setOnScroll(event -> { //TODO : recomment zooming
            if (event.isControlDown()) {
                boolean scrollUp;
                if(event.getDeltaY() < 0){
                    scrollUp = false;
                }else{ scrollUp = true;}
                event.consume();
                // These numbers need to be hardcoded for standard zoom factor
                final double zoomFactor = event.getDeltaY() > 0 ? 1.2 : 1 / 1.2;
        Bounds groupBounds = gr.getLayoutBounds();
                final Bounds viewportBounds = scrollPane.getViewportBounds();
                // calculate pixel offsets from [0, 1] range
                double valX = scrollPane.getHvalue() * (groupBounds.getWidth() - viewportBounds.getWidth());
                double valY = scrollPane.getVvalue() * (groupBounds.getHeight() - viewportBounds.getHeight());
                // convert content coordinates to target coordinates
        Point2D posInZoomTarget = target.parentToLocal(gr.parentToLocal(new Point2D(event.getX(), event.getY())));
                // calculate adjustment of scroll position (pixels)
                Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

                target.setScaleX(zoomFactor * target.getScaleX());
                target.setScaleY(zoomFactor * target.getScaleY());
                // refresh ScrollPane scroll positions & content bounds
                scrollPane.layout();
                // convert back to [0, 1] range
                // (too large/small values are automatically corrected by ScrollPane)
        groupBounds = gr.getLayoutBounds();
                scrollPane.setHvalue((valX + adjustment.getX()) / (groupBounds.getWidth() - viewportBounds.getWidth()));
                scrollPane.setVvalue((valY + adjustment.getY()) / (groupBounds.getHeight() - viewportBounds.getHeight()));
       // imgCanv.resize(scrollPane.getWidth(), scrollPane.getHeight()); //Hopefully changes canvas sizes
//     USEFUL?           imgCanv.setScaleX(target.getWidth()/imgCanv.getWidth());
//     USEFUL?           imgCanv.setScaleY(target.getHeight()/imgCanv.getHeight());
                double xCanvScale = imgCanv.getScaleX();
                double yCanvScale = imgCanv.getScaleY();
                double scaleMod = .1;
                if(scrollUp) {
                    imgCanv.setScaleX(xCanvScale - scaleMod); //Zoom in
                    imgCanv.setScaleY(yCanvScale - scaleMod);
                }else{
                    imgCanv.setScaleX(xCanvScale + scaleMod); //Zoom out
                    imgCanv.setScaleY(yCanvScale + scaleMod);
                }
                target.setScaleX(imgCanv.getScaleX());
                target.setScaleY(imgCanv.getScaleY());
//                menus.updateCanv(imgCanv);
//                imgCanv.widthProperty().bind(gr.widthProperty());
//                imgCanv.heightProperty().bind(target.heightProperty());
//                //menus.getIma.setWidth(groupBounds.getWidth());
//                menus.img.setWidth(groupBounds.getHeight());
        //imgCanv.autosize();
            }
        });
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
        if (!prevVersions.empty()){
            WritableImage removed = prevVersions.peek();
            prevVersions.pop();
            undidVersions.push(removed);
            menus.setCanvVersion(prevVersions.peek());
            imageHasBeenSaved=false;
        }
    }
    public void redo(){ //TODO: export to handlers file?
        if(!undidVersions.empty()){
            WritableImage redid = undidVersions.peek();
            undidVersions.pop();
            menus.setCanvVersion(redid);
            prevVersions.push(redid);
            imageHasBeenSaved=false;
        }
    }
//TODO: use scene.getWidth()/getHeight() for properly scaling elements
    public static void main(String[] args) {
        launch(args);
    }
//TODO: bilinear image scaling?? Try to find a better way
}