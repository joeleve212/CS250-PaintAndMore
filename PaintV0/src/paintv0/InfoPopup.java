package paintv0;

import javafx.scene.Scene;
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

        //VBox infoBox = new VBox(50);
        Text version = new Text("Pain(t) Version 0.1 - Authored by Joe Leveille");

        //infoBox.getChildren().add(version);
        popBorderPane.setCenter(version);

        Scene popupScene = new Scene(popBorderPane);
        Stage popupStage = new Stage();
        popupStage.setTitle("About Program");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.setScene(popupScene);
        popupStage.show();

    }
}
