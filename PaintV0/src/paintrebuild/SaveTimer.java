package paintrebuild;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SaveTimer implements Runnable{
    private int saveInterval = 50; //
    private int sleepTime = 100; //Time to sleep in milliseconds
    private Canvas currCanv;
    private String tmpFileLoc = "src/paintrebuild/tmp.png";
    private File imageFile;
    public SaveTimer(Canvas imgCanv, File imgFile){
        currCanv=imgCanv; //
        imageFile = new File(imgFile,tmpFileLoc);
        File tempName = new File(tmpFileLoc);
        imageFile.renameTo(tempName); //possibly breaking things
        //TODO: pull in canvas in order to save/update temp file
        System.out.println("Timer created");
    }
    @Override
    public void run(){
        System.out.println("Timer started");
        while(true) {
            for (int i = 0; saveInterval - i > 0; i++) {
                try {
                    Thread.sleep(sleepTime);
                    updateTimer(saveInterval - i);
                }catch(InterruptedException e){
                    System.out.println("Timer delay interrupted");
                }
            }
            autoSave();
            System.out.println("Timer finished");
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
