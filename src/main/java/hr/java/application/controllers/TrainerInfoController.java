package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.enums.Gender;
import hr.java.application.enums.TrainerStatus;
import hr.java.application.exceptions.unchecked.WrongNameAddressException;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.ConfirmationWindow;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Trainer;
import hr.java.application.model.TrainerBuilder;
import hr.java.application.model.User;
import hr.java.application.threads.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public non-sealed class TrainerInfoController implements NewScreen, AlertScreen, ConfirmationWindow {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableColumn<Trainer, String> TableColumnTrainerAddress;

    @FXML
    private TableColumn<Trainer, String> TableColumnTrainerGender;

    @FXML
    private TableColumn<Trainer, Integer> TableColumnTrainerID;

    @FXML
    private TableColumn<Trainer, String> TableColumnTrainerName;

    @FXML
    private TableColumn<Trainer, Integer> TableColumnTrainerPhone;

    @FXML
    private TableColumn<Trainer, String> TableColumnTrainerStatus;

    @FXML
    private TextField trainerAddressTextField;

    @FXML
    private ComboBox<String> trainerGenderComboBox;

    @FXML
    private TextField trainerNameTextField;

    @FXML
    private TextField trainerPhoneNumTextField;

    @FXML
    private ComboBox<String> trainerStatusComboBox;

    @FXML
    private TableView<Trainer> trainerTableView;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button addBtn;

    @FXML
    private TextField searchTrainersTextField;

    public void initialize(){
        trainerGenderComboBox.getItems().setAll(Gender.MALE.toString(), Gender.FEMALE.toString(), Gender.OTHER.toString());
        trainerStatusComboBox.getItems().setAll(TrainerStatus.ACTIVE.toString(), TrainerStatus.INACTIVE.toString());

        TableColumnTrainerID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Trainer, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Trainer, Integer> param) {
                return new ObservableValueBase<Integer>() {
                    @Override
                    public Integer getValue() {
                        return param.getValue().getID();
                    }
                };
            }
        });

        TableColumnTrainerName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Trainer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Trainer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        TableColumnTrainerPhone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Trainer, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Trainer, Integer> param) {
                return new ObservableValueBase<Integer>() {
                    @Override
                    public Integer getValue() {
                        return param.getValue().getPhoneNum();
                    }
                };
            }
        });

        TableColumnTrainerGender.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Trainer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Trainer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getGender());
            }
        });

        TableColumnTrainerStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Trainer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Trainer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStatus());
            }
        });

        TableColumnTrainerAddress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Trainer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Trainer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress());
            }
        });

        trainerTableView.setOnMouseClicked(event -> handleTableViewClick(trainerTableView));

        Platform.runLater(this::refreshTrainersTable);
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();

    private void handleTableViewClick(TableView<Trainer> tableView) {
        Trainer selectedTrainer = tableView.getSelectionModel().getSelectedItem();
        if (selectedTrainer != null) {
            trainerNameTextField.setText(selectedTrainer.getName());
            trainerGenderComboBox.setValue(selectedTrainer.getGender());
            trainerStatusComboBox.setValue(selectedTrainer.getStatus());
            trainerPhoneNumTextField.setText(selectedTrainer.getPhoneNum().toString());
            trainerAddressTextField.setText(selectedTrainer.getAddress());
            if(selectedTrainer.getID().equals(100)){
                updateBtn.setDisable(false);
                deleteBtn.setDisable(true);
                addBtn.setDisable(true);
            }
            else{
                deleteBtn.setDisable(false);
                updateBtn.setDisable(false);
                addBtn.setDisable(true);
            }
        }
    }

    public void addTrainer(){
        ButtonType option = confirmation("Trainer", "Add trainer?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            try {
                String name = trainerNameTextField.getText();
                String address = trainerAddressTextField.getText();
                String gender = trainerGenderComboBox.getValue();
                Integer phoneNum = Integer.valueOf(trainerPhoneNumTextField.getText());
                String status = trainerStatusComboBox.getValue();

                checkNameAndAddress();

                User<Trainer> user = new User<>(new TrainerBuilder().setName(name).setAddress(address).setStatus(status).setPhoneNum(phoneNum).setGender(gender).createTrainer());

                AddUserThread addUserThread = new AddUserThread(user);
                executor.execute(addUserThread);

                Platform.runLater(this::refreshTrainersTable);
                reset();

            } catch (NumberFormatException e) {
                showAlertError("Error!", "Wrong phone number input", "Wrong input");
                logger.error(e.getMessage());
            } catch (WrongNameAddressException e){
                showAlertError("Error", "Check Name and Address fields", "Wrong input");
            }
        }
    }

    private void refreshTrainersTable() {
        GetTrainersThread getTrainersThread = new GetTrainersThread();
        executor.execute(getTrainersThread);
        List<Trainer> trainerList = getTrainersThread.getTrainers();

        ObservableList<Trainer> observableTrainerList = FXCollections.observableArrayList(trainerList);
        trainerTableView.setItems(observableTrainerList);
    }

    public void reset(){
        trainerNameTextField.clear();
        trainerAddressTextField.clear();
        trainerPhoneNumTextField.clear();
        trainerStatusComboBox.setValue(null);
        trainerGenderComboBox.setValue(null);
        trainerTableView.getSelectionModel().clearSelection();
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
        addBtn.setDisable(false);
    }

    public void delete(){
        ButtonType option = confirmation("Trainer", "Delete trainer?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            Trainer selectedTrainer = trainerTableView.getSelectionModel().getSelectedItem();
            if (selectedTrainer != null) {
                DeleteTrainerThread deleteTrainerThread = new DeleteTrainerThread(selectedTrainer);
                executor.execute(deleteTrainerThread);

                Platform.runLater(this::refreshTrainersTable);
                updateBtn.setDisable(true);
                deleteBtn.setDisable(true);
                addBtn.setDisable(false);
                reset();
            }
        }
    }

    public void update(){
        ButtonType option = confirmation("Trainer", "Update information?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            Trainer selectedTrainer = trainerTableView.getSelectionModel().getSelectedItem();
            if (selectedTrainer != null) {

                try {
                    checkNameAndAddress();

                    Trainer newTrainer = new TrainerBuilder().setName(trainerNameTextField.getText()).setAddress(trainerAddressTextField.getText()).setGender(trainerGenderComboBox.getValue()).setPhoneNum(Integer.valueOf(trainerPhoneNumTextField.getText())).setStatus(trainerStatusComboBox.getValue()).createTrainer();

                    changeLog(selectedTrainer, newTrainer);

                    UpdateTrainerThread updateTrainerThread = new UpdateTrainerThread(newTrainer, selectedTrainer.getID());
                    executor.execute(updateTrainerThread);

                    updateBtn.setDisable(true);
                    deleteBtn.setDisable(true);
                    addBtn.setDisable(false);
                    reset();
                    Platform.runLater(this::refreshTrainersTable);
                } catch (NumberFormatException e){
                    showAlertError("Error!", "Wrong phone number input", "Wrong input");
                    logger.error(e.getMessage());
                } catch (WrongNameAddressException e){
                    showAlertError("Error", "Check Name and Address fields", "Wrong input");
                }
            }
        }
    }

    public void searchTrainers(){
        GetTrainersThread getTrainersThread = new GetTrainersThread();
        executor.execute(getTrainersThread);
        List<Trainer> trainerList = getTrainersThread.getTrainers();

        List<Trainer> filteredTrainerList = trainerList.stream().filter(customer -> customer.getName().contains(searchTrainersTextField.getText())).toList();
        ObservableList<Trainer> observableList = FXCollections.observableArrayList(filteredTrainerList);

        trainerTableView.setItems(observableList);
    }

    public void sortTrainers(){
        GetTrainersThread getTrainersThread = new GetTrainersThread();
        executor.execute(getTrainersThread);
        List<Trainer> trainerList = getTrainersThread.getTrainers();

        List<Trainer> sortedTrainerList = trainerList.stream().sorted(Comparator.comparing(Trainer::getName)).toList();
        ObservableList<Trainer> observableList = FXCollections.observableArrayList(sortedTrainerList);
        trainerTableView.setItems(observableList);
    }

    private void checkNameAndAddress() {
        if(trainerNameTextField.getText().isEmpty() || trainerAddressTextField.getText().isEmpty()){
            throw new WrongNameAddressException("Greska kod unosa imena");
        }
    }

    private static void changeLog(Trainer selectedTrainer, Trainer newTrainer) {
        Field[] fields = Trainer.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if(field.getName().equals("ID") || field.getName().equals("username") || field.getName().equals("password")){
                continue;
            }
            else{
                try {
                    Object originalValue = field.get(selectedTrainer);
                    Object updatedValue = field.get(newTrainer);

                    if (!Objects.equals(originalValue, updatedValue)) {
                        LogObject<?, ?> logObject = new LogObject<>(field.getName(), originalValue, updatedValue, MainApplication.user.getRole().toString(), selectedTrainer.getName());
                        LogObject.serializeChangeLogs(logObject);
                        LogObject.deserializeChangeLogs();
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Greska u logiranju promjena " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
