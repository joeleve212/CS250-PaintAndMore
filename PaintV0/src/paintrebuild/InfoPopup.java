package paintv0;

import javafx.application.Platform;
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


public class InfoPopup {
    private int dialogWidth;
    private int dialogHeight;
    private Text content = new Text();
    private Button cancelBtn = new Button("Cancel");
    public Button saveBtn;
    public Stage popupStage;
    InfoPopup(Stage primaryStage, String popupType) {
        dialogHeight = 300;
        dialogWidth = 300;
        Insets lowCenterBtn = new Insets(5,5,20,5);
        ButtonBar optionButtons = new ButtonBar();
        BorderPane popBorderPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        Scene popupScene = new Scene(popBorderPane);
        popupStage = new Stage();
        popBorderPane.setPrefSize(dialogWidth, dialogHeight);
        if(popupType=="exitSave"){
            popupStage.setTitle("Exit Save");
            saveBtn = new Button("Save Image");
            content.setText("There are unsaved changes!\n" +
                    "-------------------------------\n " +
                    "Are you sure you want to exit?");
            optionButtons.getButtons().addAll(cancelBtn,saveBtn);
        } else if(popupType=="About"){
            popupStage.setTitle("About Program");
            content.setText("Pain(t) Version 0.4 - Authored by Joe Leveille");
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
        popBorderPane.setCenter(content);
        optionButtons.getButtons().addAll(exitPopup);
        popBorderPane.setBottom(optionButtons);
        popBorderPane.setAlignment(optionButtons, Pos.BOTTOM_LEFT);
        popBorderPane.setMargin(optionButtons,lowCenterBtn);

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.setAlwaysOnTop(true);
        popupStage.setScene(popupScene);
        popupStage.show();

        exitPopup.setOnAction((e) -> {
            popupStage.close();
            if(popupType=="exitSave"){
                primaryStage.close();
            }
        });
        cancelBtn.setOnAction((e)->{
            popupStage.close();
        });
    }
}
