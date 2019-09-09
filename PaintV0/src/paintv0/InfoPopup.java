package paintv0;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class InfoPopup {
    private int dialogWidth;
    private int dialogHeight;
    InfoPopup(Stage primaryStage) {
        dialogHeight = 300;
        dialogWidth = 300;

        BorderPane popBorderPane = new BorderPane(); //Using borderPane to easily place things on screen edge
        popBorderPane.setPrefSize(dialogWidth, dialogHeight);

        Text version = new Text("Pain(t) Version 0.1 - Authored by Joe Leveille");
        Button exitPopup = new Button("Exit");

        popBorderPane.setBottom(exitPopup);
        popBorderPane.setAlignment(exitPopup, Pos.BOTTOM_CENTER);
        popBorderPane.setMargin(exitPopup, new Insets(5,5,20,5));
        popBorderPane.setCenter(version);

        Scene popupScene = new Scene(popBorderPane);
        Stage popupStage = new Stage();
        popupStage.setTitle("About Program");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.setScene(popupScene);
        popupStage.show();

        exitPopup.setOnAction((e) -> {
            popupStage.close();
        });

    }
}
