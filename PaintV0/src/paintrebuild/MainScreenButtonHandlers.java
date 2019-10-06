package paintv0;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.Group;
import paintrebuild.SaveTimer;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 *
 * @author Joe Leveille
 */
public class MainScreenButtonHandlers {
    public String OPENER_FILE_LOC = "../../../";
    //TODO: Bring in eventHandlers
    //TODO: Make instance of this class in main class
    TopMenus menuController;
    public String currImgPath, ext;
    public boolean imgInserted = false, primaryJustClicked = false;
    public File savedImg;
    public Canvas imgCanv;
    public int SELF_POLY_SIDES = 5; //Sets the default number of sides for a self-draw Polygon
    private WritableImage cutImage;
    private int recPoints = 0;
    private boolean imageGrabbed = false;
    private Line dragLine = new Line();
    private Rectangle dragRect = new Rectangle();
    private Ellipse dragEllip = new Ellipse();
    private Polygon nPoly = new Polygon();
    private WritableImage currSnap;
    private ArrayList<Double> polyPointsX = new ArrayList<Double>();
    private ArrayList<Double> polyPointsY = new ArrayList<Double>();
    MainScreenButtonHandlers(TopMenus menu, Stage primaryStage, Group group, Text timerVal){
        menuController=menu;

        Image img = menu.img;
        imgCanv = menu.imgCanv;
        menu.imageSave.setOnAction((e)->{
            System.out.println("Saving image file...");
            FileChooser saveImageChoose = new FileChooser();
            saveImageChoose.setTitle("Save Image As");
            saveImageChoose.getExtensionFilters().addAll( //allow saving in different file formats
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg"),
                    new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
                    new FileChooser.ExtensionFilter("BMP Files", "*.bmp"),
                    new FileChooser.ExtensionFilter("GIF Files", "*.gif"));
            savedImg = saveImageChoose.showSaveDialog(null);
            String name = savedImg.getName();
            ext = name.substring(1+name.lastIndexOf(".")).toLowerCase(); //grab only the file extension of the image

            if(savedImg != null){
                try {
                    WritableImage wImage = new WritableImage((int)menu.imgCanv.getWidth(), (int)menu.img.getHeight());
                    menu.imgCanv.snapshot(null, wImage);
                    RenderedImage rImage = SwingFXUtils.fromFXImage(wImage, null);
                    ImageIO.write(rImage, "png", savedImg);
                } catch (IOException ex) {
                    System.out.println("Initial save failed");
                }
            }
            menu.imageHasBeenSaved = true;
        });

        menu.openBtn.setOnAction((e)->{ //This function defines the action when open file is clicked
            FileChooser openFile= new FileChooser();
            openFile.setInitialDirectory(new File(OPENER_FILE_LOC));
            openFile.setTitle("Open File");
            openFile.getExtensionFilters().addAll(              //Including filters for extension types
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File insImg = openFile.showOpenDialog(primaryStage);
            currImgPath = insImg.getPath();
            System.out.println(currImgPath);
            if (currImgPath != "") {
                try {
                    InputStream io = new FileInputStream(insImg);
                    menu.img = new Image(io);
                    //TODO: Image scaling
                    menu.placedImgView.setImage(menu.img);        //Specifying placement & sizing of selected image
                    menu.placedImgView.setPreserveRatio(true);

                    menu.gc = menu.imgCanv.getGraphicsContext2D();
                    menu.gc.getCanvas().setWidth(menu.img.getWidth());  //Setting canvas to the size of the image
                    menu.gc.getCanvas().setHeight(menu.img.getHeight());
                    menu.gc.drawImage(menu.img, menu.IMG_POS_X,menu.IMG_POS_Y, imgCanv.getWidth(),imgCanv.getHeight());
                    menu.saveSnap();
                    group.getChildren().add(imgCanv);
                    imgInserted = true;

                } catch (IOException ex) {
                    System.out.println("Error!");
                }
                SaveTimer saveTimer = new SaveTimer(imgCanv,timerVal);
                Thread timeThread = new Thread(saveTimer); //Create separate thread using SaveTimer
                timeThread.setDaemon(true); //Autosave thread ends with the rest of the program
                timeThread.start(); //Starts run() function in SaveTimer
            }
        });
        imgCanv.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        menu.x0 = event.getX();
                        menu.y0 = event.getY();
                        WritableImage writable = new WritableImage((int)menu.img.getWidth(),(int)menu.img.getHeight());
                        currSnap = imgCanv.snapshot(null, writable);
                        if(imageGrabbed){
                            menu.gc.drawImage(cutImage, menu.x0, menu.y0);
                            menu.saveSnap();
                            imageGrabbed = false;
                            menu.drawMode = 0;
                            return;
                        }
                        if(event.isPrimaryButtonDown()) {
                            primaryJustClicked = true;
                            if (menu.drawMode == -1) {
                                PixelReader colorSnag = menu.img.getPixelReader();
                                Color newColor = colorSnag.getColor((int) menu.x0, (int) menu.y0);
                                menu.setShapeLineColor(newColor);
                            } else if(menu.drawMode == 3) {
                                menu.x1 = menu.x0;
                                menu.y1 = menu.y0;
                                menu.gc.beginPath();
                                menu.gc.setLineWidth(menu.lineWidth);
                            } else if(menu.drawMode == 1) {
                                dragLine.setStartX(menu.x0);
                                dragLine.setStartY(menu.y0);
                                dragLine.setEndX(menu.x0);
                                dragLine.setEndY(menu.y0);
                                dragLine.setStrokeWidth(menu.lineWidth);
                                dragLine.setStroke(menu.getLineColor());
                                group.getChildren().add(dragLine);
                            } else if(Math.abs(menu.drawMode) == 2) {
                                //rectangle
                                dragRect.setX(menu.x0);
                                dragRect.setY(menu.y0);
                                dragRect.setWidth(0);
                                dragRect.setHeight(0);
                                dragRect.setStrokeWidth(menu.lineWidth);
                                dragRect.setStroke(menu.getLineColor());
                                dragRect.setFill(null);
                                group.getChildren().add(dragRect);
                            } else if (menu.drawMode == 4 || menu.drawMode == 5) {
                                dragEllip.setCenterX(menu.x0);
                                dragEllip.setCenterY(menu.y0);
                                dragEllip.setRadiusX(0);
                                dragEllip.setRadiusY(0);
                                dragEllip.setStrokeWidth(menu.lineWidth);
                                dragEllip.setStroke(menu.getLineColor());
                                dragEllip.setFill(null);
                                group.getChildren().add(dragEllip);
                            } else if(menu.drawMode==6){
                                nPoly.setStroke(menu.getLineColor());
                                nPoly.setFill(null);
                                nPoly.setStrokeWidth(menu.lineWidth);
                                group.getChildren().add(nPoly);
                            } else if (menu.drawMode==7) {
                                if(recPoints<SELF_POLY_SIDES){
                                    polyPointsX.add(event.getX());
                                    polyPointsY.add(event.getY());
                                    recPoints++;
                                }else{      //Reset polygon points
                                    menu.drawSelfPoly(polyPointsX, polyPointsY);
                                    recPoints=0;
                                    polyPointsX.clear();
                                    polyPointsY.clear();
                                }
                            }
                        }
                    }
                });
        imgCanv.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.isPrimaryButtonDown() && menu.drawMode==3) {
                    menu.gc.lineTo(event.getX(), event.getY());
                    menu.gc.stroke();
                } else if(event.isPrimaryButtonDown()){
                    double deltX=Math.abs(event.getX()-menu.x0);
                    double deltY=Math.abs(event.getY()-menu.y0);
                    switch (menu.drawMode){
                        case 1:
                            dragLine.setEndX(event.getX());
                            dragLine.setEndY(event.getY());
                            menu.setCanvVersion(currSnap);
                            menu.gc.strokeLine(menu.x0,menu.y0,event.getX(),event.getY());
                            break;
                        case 2:
                            dragRect.setWidth(Math.abs(event.getX()-menu.x0));
                            dragRect.setHeight(Math.abs(event.getY()-menu.y0));
                            menu.setCanvVersion(currSnap);
                            menu.gc.strokeRect(menu.x0,menu.y0,deltX, deltY);
                            break;
                        case -2:
                            dragRect.setWidth(Math.abs(event.getX()-menu.x0));
                            dragRect.setHeight(Math.abs(event.getY()-menu.y0));
                            break;
                        case -3:
                            dragRect.setWidth(Math.abs(event.getX()-menu.x0));
                            dragRect.setHeight(Math.abs(event.getY()-menu.y0));
                            break;
                        case 4:
                            dragEllip.setRadiusX(deltX);
                            dragEllip.setRadiusY(deltY);
                            menu.setCanvVersion(currSnap);
                            menu.gc.strokeOval(menu.x0-deltX,menu.y0-deltY,2*deltX, 2*deltY);
                            break;
                        case 5:
                            dragEllip.setRadiusX(deltX);
                            dragEllip.setRadiusY(deltX);
                            menu.setCanvVersion(currSnap);
                            menu.gc.strokeOval(menu.x0-deltX,menu.y0-deltX,2*deltX, 2*deltX);
                            break;
                        case 6:
                            ArrayList<Double> corners= createPolyPoints(event);
                            nPoly.getPoints().setAll(corners);
                            break;
                        default:
                            //Nothing?
                    }
                }
                if(imageGrabbed){
                    //TODO: make grabbed image follow cursor
                }
            }
        });
        imgCanv.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(primaryJustClicked&&menu.drawMode!=3&&menu.drawMode!=0) {
                            menu.x1 = event.getX();
                            menu.y1 = event.getY();
                            menu.drawShape();
                            primaryJustClicked = false;
                            WritableImage writable = new WritableImage((int)menu.imgCanv.getWidth(),(int)menu.imgCanv.getHeight());
                            currSnap = imgCanv.snapshot(null,writable);
                            switch (menu.drawMode) {
                                case 1:
                                    group.getChildren().remove(dragLine);
                                    menu.setCanvVersion(currSnap);
                                    break;
                                case 2:
                                    group.getChildren().remove(dragRect);
                                    break;
                                case 4:
                                    group.getChildren().remove(dragEllip);
                                    break;
                                case 5:
                                    group.getChildren().remove(dragEllip);
                                    break;
                                case -2:
                                    group.getChildren().remove(dragRect);
                                    break;
                                case -3:
                                    group.getChildren().remove(dragRect);
                                    break;
                                case 6:
                                    double rad = Math.max(Math.abs(menu.x0- event.getX()),Math.abs(menu.y0-event.getY()));
                                    menu.drawPolygon(nPoly, rad);
                                    group.getChildren().remove(nPoly);
                                    break;
                                default:
                                    //
                            }
                        } else if(menu.drawMode==3){
                            menu.gc.closePath();
                            menu.saveSnap();
                        }
                        if(menu.drawMode==-2){//If tool is in cut mode
                            WritableImage wImage = new WritableImage((int)imgCanv.getWidth(), (int)imgCanv.getHeight());
                            imgCanv.snapshot(null, wImage);
                            cutImage = new WritableImage(wImage.getPixelReader() ,(int)menu.x0,(int)menu.y0,(int)Math.abs(menu.x0-menu.x1),(int)Math.abs(menu.y0-menu.y1));
                            drawBlankRect();
                            imageGrabbed = true;
                        } else if(menu.drawMode==-3){
                            WritableImage wImage = new WritableImage((int)imgCanv.getWidth(), (int)imgCanv.getHeight());
                            imgCanv.snapshot(null, wImage);
                            cutImage = new WritableImage(wImage.getPixelReader() ,(int)menu.x0,(int)menu.y0,(int)Math.abs(menu.x0-menu.x1),(int)Math.abs(menu.y0-menu.y1));
                            imageGrabbed = true;
                        }
                    }
                });
    }
    public ArrayList<Double> createPolyPoints(MouseEvent event){
        //TODO: implement createNGon
        double x0 = menuController.x0;
        double y0 = menuController.y0;
        double x1 = event.getX();
        double y1 = event.getY();
        int sides = menuController.numSides;
        double rad = Math.max(Math.abs(x0- event.getX()),Math.abs(y0-event.getY()));
        ArrayList<Double> pointArr=new ArrayList<Double>(2*sides);
        for(int i=0;i<sides;i++){
            pointArr.add(rad*Math.cos(2*i*Math.PI/sides)+x0);
            pointArr.add(rad*Math.sin(2*i*Math.PI/sides)+y0);
        }
        return pointArr;
    }
    public void saveImage(){
        try{
            WritableImage wImage = new WritableImage((int) imgCanv.getWidth(), (int) imgCanv.getHeight());
            imgCanv.snapshot(null, wImage);
            ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), ext, savedImg);
            menuController.imageHasBeenSaved=true;
        } catch (IOException ex) { //Throw a simple error if saving dies
            System.out.println("Save Failed!");
        }
    }
    public void drawBlankRect(){
        double x0 = menuController.x0;
        double x1 = menuController.x1;
        double y0 = menuController.y0;
        double y1 = menuController.y1;
        Paint color = menuController.getFillColor();
        menuController.setShapeFillColor(Color.WHITE);
        menuController.gc.fillRect(x0, y0, Math.abs(x1-x0), Math.abs(y0-y1));
        menuController.setShapeFillColor((Color)color);
        menuController.saveSnap();
    }
}
