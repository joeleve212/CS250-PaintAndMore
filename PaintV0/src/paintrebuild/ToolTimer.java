package paintrebuild;

import javafx.beans.Observable;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class ToolTimer implements Runnable {
    private String drawMode, newLog;
    private long startTime,endTime;
    private double totTime;
    private List<String> logEntries = new ArrayList<>();
    public void beginTimer(String startTool){
        drawMode = startTool; //bring in the initial mode from TopMenus
        startTime = System.currentTimeMillis();
    }
    @Override
    public void run() {
        //TODO: check tool, start timer for that
    }
    public void switchTool(String newTool){
        //TODO: add previous timer to ArrayList, reset, start new timer
        endTime = System.currentTimeMillis();
        totTime = ((double)endTime-(double)startTime)/1000; //calculate time on that tool in seconds
        newLog = drawMode+" was selected for "+totTime+" seconds";
        logEntries.add(newLog);
        drawMode = newTool;
        startTime = System.currentTimeMillis();
        System.out.println("New ArrayList:");
        for(int i = 0;i<logEntries.size();i++){
            System.out.println(logEntries.get(i));
        }
    }
    public void end(){
        //TODO: create log file
        switchTool("");
        Path file = Paths.get("toolTimes.log");
        try {
            Files.write(file, logEntries, StandardCharsets.UTF_8);
        }catch(IOException e){
            System.out.println("Failed to write log file");
        }
    }
}