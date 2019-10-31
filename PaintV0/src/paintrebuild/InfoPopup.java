package paintv0;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * The InfoPopup class is meant to create a seperate window
 * to display text and an exit button, such as the help window.
 *
 * @author  Joe Leveille
 * @version 1.0
 * @since   2019-10-02
 */
public class InfoPopup {
    private int dialogWidth = 300,dialogHeight = 300;
    private int ins_TOP = 5, ins_RIGHT = 15, ins_LEFT = 15, ins_BOT = 20;
    private Text content = new Text();
    private Button cancelBtn = new Button("Cancel");
    public Button saveBtn;
    public Stage popupStage;
    InfoPopup(Stage primaryStage, String popupType) {
        //Below sets up the default window components in every popup
        Insets lowCenterBtn = new Insets(ins_TOP,ins_RIGHT,ins_BOT,ins_LEFT); //Sets position for button later
        ButtonBar optionButtons = new ButtonBar();
        BorderPane popBorderPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        Scene popupScene = new Scene(popBorderPane);
        popupStage = new Stage();
        popBorderPane.setPrefSize(dialogWidth, dialogHeight); //

        //Below sets the title and text according to the popup type
        if(popupType=="exitSave"){
            popupStage.setTitle("Exit Save");
            saveBtn = new Button("Save Image");
            content.setText("There are unsaved changes!\n" +
                    "-------------------------------\n " +
                    "Are you sure you want to exit?");
            optionButtons.getButtons().addAll(cancelBtn,saveBtn);
        } else if(popupType=="About"){
            popupStage.setTitle("About Program");
            content.setText("Pain(t) Version 1.0 - Authored by Joe Leveille");
        } else if(popupType=="helpInfo"){
            popupStage.setTitle("Help Information");
            content.setText("This program is meant to modify images\n" +
                    " with custom shapes and image tools");
        } else if(popupType=="saveWarn"){
            popupStage.setTitle("Save Types Warning");
            content.setText("Careful when saving to a file type different\n" +
                    "from your original file type!\n" +
                    " ***This may cause data loss***");
        }
        Button exitPopup = new Button("Exit");
        content.setTextAlignment(TextAlignment.CENTER);
        popBorderPane.setCenter(content); //places text in middle of window
        optionButtons.getButtons().addAll(exitPopup); //Makes sure that the exit button is always included
        popBorderPane.setBottom(optionButtons);//Set placement of buttons
        popBorderPane.setAlignment(optionButtons, Pos.BOTTOM_CENTER);
        popBorderPane.setMargin(optionButtons,lowCenterBtn);

        //Below sets key window properties before making the window visible
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.setAlwaysOnTop(true);
        popupStage.setScene(popupScene);
        popupStage.show();

        /**
         * This handler closes the popup on a button press,
         * as well as the program if it is a specific popup.
         *
         * @param Event e is the only parameter for the handler
         */
        exitPopup.setOnAction((e) -> { //handle exit button
            popupStage.close();
            if(popupType=="exitSave"){ //Closes program if popup was triggered
                primaryStage.close();  //by closing without saving
            }
        });
        cancelBtn.setOnAction((e)->{ //handle cancel button
            popupStage.close();
        });
    }
}
