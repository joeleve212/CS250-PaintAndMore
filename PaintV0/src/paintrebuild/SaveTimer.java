package paintrebuild;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
/**
 * This class creates a new thread to control the
 * autosave timer.
 *
 * @author  Joe Leveille
 * @version 1.0
 * @since   2019-10-02
 */
public class SaveTimer implements Runnable{
    private int saveInterval = 120; //How many sleepTime intervals between saving
    private int sleepTime = 1000; //Time to sleep in milliseconds
    private Canvas currCanv;
    private String tmpFileLoc = "src/paintrebuild/tmp"; //allow multiple file types
    private File imageFile;
    private Text currTimer;

    /**
     * The constructor initializes the canvas, timer text
     * & creates a file to save to.
     *
     * @param imgCanv
     * @param timerVal
     */
    public SaveTimer(Canvas imgCanv, Text timerVal){
        currCanv=imgCanv;
        currTimer=timerVal;
        imageFile = new File(tmpFileLoc);
    }

    /**
     * run() infinitely loops once the thread starts in order to keep autosaving
     * after the set increment of time.
     */
    @Override
    public void run(){
        while(true) {
            for (int i = 0; saveInterval - i > 0; i++) {
                try {
                    Thread.sleep(sleepTime); //delay the counter by sleepTime ms
                    updateTimer(saveInterval - i);
                }catch(InterruptedException e){ //catch errors that break sleep() function
                    System.out.println("Timer delay interrupted");
                }
            }
            autoSave(); //call the function to actually save
        }
    }

    /**
     * This updates the displayed value of time left before autosaving
     *
     * @param newTimeLeft Is the new value to display on the countdown
     */
    public void updateTimer(int newTimeLeft){
        currTimer.setText(Integer.toString(newTimeLeft));
    }

    /**
     * This function actually handles the autosave by taking a snapshot
     * of the current canvas and placing it into imageFile.
     */
    private void autoSave(){
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
