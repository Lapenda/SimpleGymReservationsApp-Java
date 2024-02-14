package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.databaseUtils.Database;
import hr.java.application.hash.PasswordHashing;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Customer;
import hr.java.application.model.Trainer;
import hr.java.application.model.User;
import hr.java.application.threads.GetCurrentUserThread;
import hr.java.application.threads.GetTrainersThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.h2.store.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileViewController {

    @FXML
    private Label addressLabel;

    @FXML
    private Label eDatelLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label groupLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phNumLabel;

    @FXML
    private Label sDateLabel;

    @FXML
    private Label scheduleLabel;

    @FXML
    private Label trainerLabel;

    @FXML
    void goToUpdateProfile() {
       NewScreen.showNewScreen("updateProfile-view.fxml");
    }

    public void initialize(){
        GetTrainersThread getTrainersThread = new GetTrainersThread();
        Platform.runLater(getTrainersThread);
        List<Trainer> trainerList = getTrainersThread.getTrainers();

        Platform.runLater(() -> setValues(trainerList));
    }

    private void setValues(List<Trainer> trainerList) {
        GetCurrentUserThread getCurrentUserThread = new GetCurrentUserThread(MainApplication.userString);
        Platform.runLater(getCurrentUserThread);
        Customer currentCustomer = getCurrentUserThread.getCurrentUser(MainApplication.userString);

        addressLabel.setText(currentCustomer.getAddress());
        eDatelLabel.setText(currentCustomer.getEndDate().toString());
        sDateLabel.setText(currentCustomer.getStartDate().toString());
        scheduleLabel.setText(currentCustomer.getSchedule());
        Optional<String> trainer = trainerList.stream().filter(trainer2 -> trainer2.getID().equals(currentCustomer.getTrainerID())).map(Trainer::getName).findFirst();
        trainerLabel.setText(trainer.get() + " (ID: " + currentCustomer.getTrainerID() + ")");
        phNumLabel.setText(currentCustomer.getPhoneNum().toString());
        nameLabel.setText(currentCustomer.getName());
        genderLabel.setText(currentCustomer.getGender());
        groupLabel.setText(currentCustomer.getGroup());
        idLabel.setText(currentCustomer.getID().toString());
    }
}
