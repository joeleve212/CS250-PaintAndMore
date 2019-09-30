package paintrebuild;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SaveTimer implements Runnable{
    private int saveInterval = 300; //
    private int sleepTime = 100; //Time to sleep in milliseconds
    private Canvas currCanv;
    private String tmpFileLoc = "src/paintrebuild/";
    private File imageFile;
    SaveTimer(Canvas imgCanv, File imgFile){
        currCanv=imgCanv; //
        imageFile = imgFile;
        //TODO: pull in canvas in order to save/update temp file
    }
    @Override
    public void run(){
        while(true) {
            for (int i = 0; saveInterval - i > 0; i++) {
                try {
                    Thread.sleep(sleepTime);
                    updateTimer(saveInterval - i);
                }catch(InterruptedException e){
                    System.out.println("Timer delay interupted");
                }
            }
            autoSave();
        }
    }
    public void updateTimer(int newTimeLeft){
        //TODO: update displayed time in window
    }
    private void autoSave(){
        //TODO: implement saving currCanv to tmp.png
        try{
            WritableImage wImage = new WritableImage((int) currCanv.getWidth(), (int) currCanv.getHeight());
            currCanv.snapshot(null, wImage);
            ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", imageFile);
        } catch (IOException ex) { //Throw a simple error if saving dies
            System.out.println("Save Failed!");
        }
    }
}
