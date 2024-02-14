package hr.java.application.interfaces;

import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.scene.control.Alert;

public interface AlertScreen extends NewScreen{

    default void showAlertError(String titleText, String headerText, String contentText){
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setMinWidth(550);
        alert.showAndWait();
    }

    static void showAlertConfirmation(String titleText, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setMinWidth(550);
        alert.showAndWait();
    }

    static void showAlertInfo(String titleText, String headerText, String contentText, String screen){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setMinWidth(550);
        alert.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = alert.showAndWait();

        if(option.isPresent() && option.get().equals(ButtonType.OK)){
            NewScreen.showNewScreen(screen);
        }
        else if(option.isPresent() && option.get().equals(ButtonType.CANCEL)){
            alert.close();
        }
    }
}