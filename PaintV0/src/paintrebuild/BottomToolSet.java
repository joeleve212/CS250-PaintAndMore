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

        currentToolSet = new ToolBar(undoBtn,redoBtn);

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
            if(event.getCode() == KeyCode.ENTER){
                int lineWidth = widthChoose.getValue();
                menus.setLineWidth((double) lineWidth);
            }
        });

    }
    public ToolBar getToolBar(){
        return currentToolSet;
    }
    public void updateTools(int drawMode){
        ToolBar newToolBar;
        switch(drawMode){
            case 1: //Line
                newToolBar = new ToolBar();
                //line width, line color
                break;
            case 2: //Rectangle
                //Line width/color, fill color
                break;
            case 3: //Free Draw
                //line width/color
                break;
            default:
                //blank icon
        }
        newToolBar = new ToolBar();
    }


}
