package hr.java.application.interfaces;

import hr.java.application.controllers.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public sealed interface ConfirmationWindow permits UpdateProfileController, PayController, CustomerInfoController, FirstTimeLoggingInController, ReserveASessionController, TrainerInfoController {

    default ButtonType confirmation(String titleText, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setMinWidth(550);
        Optional<ButtonType> option = alert.showAndWait();

        return option.get();
    }
}
