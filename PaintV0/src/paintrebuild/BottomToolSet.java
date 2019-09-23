package paintv0;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Stack;


public class BottomToolSet {
    public ToolBar currentToolSet;
    public Button undoBtn,redoBtn;
    public TextField textInput;
    public ColorPicker outlineColor, fillColor;
    public Spinner<Integer> widthChoose;
    private TopMenus menus;
    BottomToolSet(TopMenus menus, Button undoBtn, Button redoBtn){
        this.redoBtn = redoBtn;
        this.undoBtn = undoBtn;
        this.menus = menus;

        widthChoose = new Spinner<Integer>(1,100,5);
        widthChoose.setEditable(true);
        outlineColor = new ColorPicker(Color.BLACK); //set default outline color
        fillColor = new ColorPicker(Color.BLACK);//set default fill color
        textInput = new TextField("Text Input");
//TODO: Place necessary controls on toolbar for each edit tool
        //ToolBar windowBar = new ToolBar(widthChoose, outlineColor, fillColor, textInput, undoBtn, redoBtn); //Creates the toolbar to hold both choosers

        currentToolSet = new ToolBar(widthChoose,outlineColor,fillColor,textInput,undoBtn,redoBtn);

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
        widthChoose.setOnKeyPressed((event) -> { //Grabbing new width setting and updating Line width
            int lineWidth = widthChoose.getValue();
            menus.setLineWidth((double) lineWidth);
        });
    }
    public void setToolBar(ToolBar newTools){currentToolSet=newTools;}
    public ToolBar getToolBar(){
        return currentToolSet;
    }
    public ToolBar updateTools(int mode){
//        int mode = menus.getDrawMode();
//        ToolBar newToolBar = new ToolBar();
        if(mode==1||mode==3) { //TODO: Change to if statement?
            //Line & free draw
            //line width, line color
            widthChoose.setVisible(true);
            outlineColor.setVisible(true);
            fillColor.setVisible(false);
            textInput.setVisible(false);
        } else if(mode==2||(mode>3&&mode<8)) {//Rectangle, circle, ellipse, polygon, star
//              //Line width/color, fill color
            widthChoose.setVisible(true);
            outlineColor.setVisible(true);
            fillColor.setVisible(true);
            textInput.setVisible(false);
        }else if(mode==8){
            widthChoose.setVisible(false);
            outlineColor.setVisible(false);
            fillColor.setVisible(false);
            textInput.setVisible(true);
        } else {
            System.out.println("Draw mode " + mode + " invalid");
        }
        //newToolBar.getItems().addAll(undoBtn,redoBtn);
        //currentToolSet = newToolBar;
        return currentToolSet;
    }
}
