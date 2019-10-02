package paintrebuild;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SaveTimer implements Runnable{
    private int saveInterval = 120; //
    private int sleepTime = 1000; //Time to sleep in milliseconds
    private Canvas currCanv;
    private String tmpFileLoc = "src/paintrebuild/tmp.png";
    private File imageFile;
    private Text currTimer;
    public SaveTimer(Canvas imgCanv, File imgFile, Text timerVal){
        currCanv=imgCanv;
        currTimer=timerVal;
        imageFile = new File(tmpFileLoc);
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
        currTimer.setText(Integer.toString(newTimeLeft));
    }
    private void autoSave(){
        //TODO: implement saving currCanv to tmp.png
        Platform.runLater(()->{
            try{
                WritableImage wImage = new WritableImage((int) currCanv.getWidth(), (int) currCanv.getHeight());
                currCanv.snapshot(null, wImage);
                ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", imageFile);
            } catch (IOException ex) { //Throw a simple error if saving dies
                System.out.println("Save Failed!");
            }
        });
    }
}
