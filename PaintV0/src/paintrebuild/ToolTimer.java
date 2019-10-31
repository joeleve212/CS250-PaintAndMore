package paintrebuild;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
/**
 * The ToolTimer class is meant to keep track of
 * how long each tool is selected, then output the
 * list of those times to a log file.
 *
 * @author  Joe Leveille
 * @version 1.0
 * @since   2019-10-02
 */
public class ToolTimer implements Runnable {
    private String drawMode, newLog;
    private long startTime,endTime;
    private double totTime;
    private List<String> logEntries = new ArrayList<>();

    @Override
    public void run() {/*This thread does not utilize run() */ }

    /**
     * This function is meant to run once when ToolTimer is initialized
     * in order to begin tracking tools.
     *
     * @param startTool The string holding the name of the first selected tool
     */
    public void beginTimer(String startTool){
        drawMode = startTool; //bring in the initial mode from TopMenus
        startTime = System.currentTimeMillis();
    }
    /**
     * This function is meant to run every time the user chooses a different tool
     * to create individual log entries for each tool.
     *
     * @param newTool The string holding the name of the next selected tool
     */
    public void switchTool(String newTool){
        //add previous timer to ArrayList, reset, start new timer
        endTime = System.currentTimeMillis();
        totTime = ((double)endTime-(double)startTime)/1000; //calculate time on that tool in seconds
        newLog = drawMode+" was selected for "+totTime+" seconds";
        logEntries.add(newLog); //add new tool entry to ArrayList
        drawMode = newTool; //change mode to new tool
        startTime = System.currentTimeMillis(); //grab start time for new tool
    }
    /**
     * This function is meant to run when the program is about to close
     * to write the ArrayList of entries to a log file.
     *
     */
    public void end(){ //Transfers logEntries data to .log file as thread ends
        switchTool("");
        Path file = Paths.get("toolTimes.log"); //place log file in PaintV0 directory
        try {
            Files.write(file, logEntries, StandardCharsets.UTF_8);
        }catch(IOException e){
            System.out.println("Failed to write log file");
        }
    }
}