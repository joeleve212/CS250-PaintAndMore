/*
 * This file is meant to contain the functions - Not implemented for Sprint 1
 */
package paintv0;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.Group;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    private Line dragLine = new Line();
    private Rectangle dragRect = new Rectangle();
    private Ellipse dragEllip = new Ellipse();
    MainScreenButtonHandlers(TopMenus menu, Stage primaryStage, Group group){
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
            }
        });
        imgCanv.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.isPrimaryButtonDown()) {
                            menu.gc.beginPath();
                            menu.x0 = event.getX();
                            menu.y0 = event.getY();
                            primaryJustClicked = true;
                            if(menu.drawMode==-1){
                                PixelReader colorSnag = menu.img.getPixelReader();
                                Color newColor = colorSnag.getColor((int)menu.x0, (int)menu.y0);
                                menu.setShapeLineColor(newColor);
                            }
                        }

                        if(menu.drawMode==3){
                            menu.x1=menu.x0;
                            menu.y1=menu.y0;
                        }
                        if(menu.drawMode==1){
                            //TODO: add dummy line to group
                            dragLine.setStartX(menu.x0);
                            dragLine.setStartY(menu.y0);
                            dragLine.setEndX(menu.x1);
                            dragLine.setEndY(menu.y1);
                            dragLine.setStroke(menu.getLineColor());
                            group.getChildren().add(dragLine);
                        } else if(menu.drawMode==2){
                            //rectangle
                            dragRect.setX(menu.x0);
                            dragRect.setY(menu.y0);
                            dragRect.setWidth(0);
                            dragRect.setHeight(0);
                            group.getChildren().add(dragRect);
                        } else if(menu.drawMode>3&&menu.drawMode<6){
                            //TODO: start up circle/ellipse
                        }
                    }
                }
        );
//TODO: implement mouse drag event to see shape during creation
        imgCanv.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.isPrimaryButtonDown() && menu.drawMode==3) {
                    menu.gc.lineTo(event.getX(), event.getY());
                    menu.gc.stroke();
                } else if(event.isPrimaryButtonDown()){
                    switch (menu.drawMode){
                        case 1:
                            dragLine.setEndX(event.getX());
                            dragLine.setEndY(event.getY());
                            break;
                        case 2:
                            dragRect.setWidth(Math.abs(event.getX()-menu.x0));
                            dragRect.setHeight(Math.abs(event.getY()-menu.y0));
                            break;
                        default:
                            //Nothing?
                    }
                }
            }
//TODO: for seeing shape during drag, make instance shapes separate from gc - setEndX(xCurr) Add this to group
        });
        imgCanv.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(!event.isPrimaryButtonDown()&&primaryJustClicked&&menu.drawMode!=3) {
                            menu.x1 = event.getX();
                            menu.y1 = event.getY();
                            menu.drawShape();
                            primaryJustClicked = false;
                            switch (menu.drawMode) {
                                case 1:
                                    group.getChildren().remove(dragLine);
                                    break;
                                case 2:
                                    group.getChildren().remove(dragRect);
                                    break;
                                default:
                                    //
                            }
                        } else if(menu.drawMode==3){
                            menu.gc.closePath();
                            menu.saveSnap();
                        }
//TODO: remove dummy shape from group
                    }
                }
        );
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
}
