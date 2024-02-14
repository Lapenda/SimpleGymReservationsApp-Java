package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.enums.Gender;
import hr.java.application.enums.PaidStatus;
import hr.java.application.enums.TrainerStatus;
import hr.java.application.exceptions.checked.StartEndDateException;
import hr.java.application.exceptions.unchecked.WrongNameAddressException;
import hr.java.application.hash.PasswordHashing;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.ConfirmationWindow;
import hr.java.application.model.*;
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
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public non-sealed class CustomerInfoController implements AlertScreen, ConfirmationWindow {

    private final String[] schedule = {"6AM - 8AM", "8AM - 10AM", "10AM - 12PM", "1PM - 3PM", "3PM - 5PM", "5PM - 7PM", "7PM - 9PM"};

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerAddress;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerEndDate;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerGender;

    @FXML
    private TableColumn<Customer, Integer> TableColumnCustomerID;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerName;

    @FXML
    private TableColumn<Customer, Integer> TableColumnCustomerPhone;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerSchedule;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerStartDate;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerTrainer;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerGroup;

    @FXML
    private TableColumn<Customer, String> TableColumnCustomerStatus;

    @FXML
    private TextField customerAddressTextField;

    @FXML
    private DatePicker customerEndDatePicker;

    @FXML
    private ComboBox<String> customerGenderComboBox;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private TextField customerPhoneNumTextField;

    @FXML
    private ComboBox<String> customerScheduleComboBox;

    @FXML
    private DatePicker customerStartDatePicker;

    @FXML
    private ComboBox<Trainer> customerTrainerComboBox;

    @FXML
    private RadioButton radioBtnYes;

    @FXML
    private TableView<Customer> tableViewCustomer;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private PasswordField firstPasswordField;

    @FXML
    private AnchorPane firstScreen;

    @FXML
    private PasswordField secondPasswordField;

    @FXML
    private GridPane secondScreen;

    @FXML
    private TextField usernameTextField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TextField filterTextField;

    Customer newCustomer;

    List<Trainer> trainerList;

    public void initialize(){

        GetTrainersThread getTrainersThread = new GetTrainersThread();
        Platform.runLater(getTrainersThread);

        trainerList = getTrainersThread.getTrainers();
        //Database.getTrainers();

        List<Trainer> activeTrainerList = trainerList.stream().filter(trainer -> trainer.getStatus().equals(TrainerStatus.ACTIVE.toString())).toList();
        ObservableList<Trainer> observableActiveTrainerList = FXCollections.observableArrayList(activeTrainerList);
        customerTrainerComboBox.setItems(observableActiveTrainerList);

        customerGenderComboBox.getItems().setAll(Gender.MALE.toString(), Gender.FEMALE.toString(), Gender.OTHER.toString());
        statusComboBox.getItems().setAll(PaidStatus.PAID.toString(), PaidStatus.UNPAID.toString());

        List<String> scheduleList = new ArrayList<>(Arrays.asList(schedule));
        ObservableList<String> observableScheduleList = FXCollections.observableArrayList(scheduleList);
        customerScheduleComboBox.setItems(observableScheduleList);

        TableColumnCustomerName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        TableColumnCustomerAddress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress());
            }
        });

        TableColumnCustomerGender.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getGender());
            }
        });

        TableColumnCustomerSchedule.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getSchedule());
            }
        });

        TableColumnCustomerTrainer.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getTrainerID().toString());
            }
        });

        TableColumnCustomerGroup.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getGroup());
            }
        });

        TableColumnCustomerPhone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Customer, Integer> param) {
                return new ObservableValueBase<Integer>() {
                    @Override
                    public Integer getValue() {
                        return param.getValue().getPhoneNum();
                    }
                };
            }
        });

        TableColumnCustomerID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Customer, Integer> param) {
                return new ObservableValueBase<Integer>() {
                    @Override
                    public Integer getValue() {
                        return param.getValue().getID();
                    }
                };
            }
        });

        TableColumnCustomerStartDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStartDate().toString());
            }
        });

        TableColumnCustomerEndDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getEndDate().toString());
            }
        });

        TableColumnCustomerStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStatus());
            }
        });

        tableViewCustomer.setOnMouseClicked(event -> handleTableViewClick(tableViewCustomer));

        Platform.runLater(this::refreshCustomersTable);
    }

    private void handleTableViewClick(TableView<Customer> tableView) {
        Customer selectedCustomer = tableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            customerNameTextField.setText(selectedCustomer.getName());
            customerGenderComboBox.setValue(selectedCustomer.getGender());
            Integer trainerID = selectedCustomer.getTrainerID();
            Optional<Trainer> getTrainer = trainerList.stream().filter(trainer -> trainerID.equals(trainer.getID())).findFirst();
            customerTrainerComboBox.setValue(getTrainer.get());
            customerPhoneNumTextField.setText(selectedCustomer.getPhoneNum().toString());
            customerAddressTextField.setText(selectedCustomer.getAddress());
            customerScheduleComboBox.setValue(selectedCustomer.getSchedule());
            customerStartDatePicker.setValue(selectedCustomer.getStartDate());
            customerEndDatePicker.setValue(selectedCustomer.getEndDate());
            radioBtnYes.setSelected(selectedCustomer.getGroup().equals("true"));
            statusComboBox.setValue(selectedCustomer.getStatus());
            updateBtn.setDisable(false);
            deleteBtn.setDisable(false);
            addBtn.setDisable(true);
        }
    }


    @FXML
    void deleteCustomer() {
        ButtonType option = confirmation("Customer", "Delete customer?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            Customer selectedCustomer = tableViewCustomer.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                DeleteCustomerThread deleteCustomerThread = new DeleteCustomerThread(selectedCustomer);
                Platform.runLater(deleteCustomerThread);

                Platform.runLater(this::refreshCustomersTable);
                updateBtn.setDisable(true);
                deleteBtn.setDisable(true);
                addBtn.setDisable(false);
                reset();
            }
        }
    }

    @FXML
    void reset() {
        customerNameTextField.clear();
        customerAddressTextField.clear();
        customerPhoneNumTextField.clear();
        customerStartDatePicker.setValue(null);
        customerEndDatePicker.setValue(null);
        customerScheduleComboBox.getSelectionModel().clearSelection();
        customerGenderComboBox.getSelectionModel().clearSelection();
        customerTrainerComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        radioBtnYes.setSelected(false);
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
        addBtn.setDisable(false);
    }

    @FXML
    void updateCustomer() {
        ButtonType option = confirmation("Customer", "Update customer?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            Customer selectedCustomer = tableViewCustomer.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {

                try {
                    checkDate();
                    checkNameAndAddress();

                    Customer newCustomer = new CustomerBuilder()
                            .setName(customerNameTextField.getText())
                            .setAddress(customerAddressTextField.getText())
                            .setGender(customerGenderComboBox.getValue())
                            .setPhoneNum(Integer.valueOf(customerPhoneNumTextField.getText()))
                            .setTrainerID(customerTrainerComboBox.getValue().getID())
                            .setSchedule(customerScheduleComboBox.getValue())
                            .setStartDate(customerStartDatePicker.getValue())
                            .setEndDate(customerEndDatePicker.getValue())
                            .setGroup(String.valueOf(radioBtnYes.isSelected()))
                            .setStatus(statusComboBox.getValue())
                            .createCustomer();

                    changeLog(selectedCustomer, newCustomer);

                    UpdateCustomerThread updateCustomerThread = new UpdateCustomerThread(newCustomer, selectedCustomer.getID());
                    Platform.runLater(updateCustomerThread);
                    updateBtn.setDisable(true);
                    deleteBtn.setDisable(true);
                    addBtn.setDisable(false);
                    reset();
                    Platform.runLater(this::refreshCustomersTable);

                } catch (StartEndDateException e){
                    showAlertError("Error", "Check date fields", "Wrong date input");
                }
                catch (NumberFormatException e){
                    showAlertError("Error", "Check Phone Number field", "Wrong phone number input");
                }
                catch (WrongNameAddressException e){
                    showAlertError("Error", "Check Name and Address fields", "Wrong input");
                }
            }
        }
    }

    private void refreshCustomersTable() {
        GetCustomersThread getCustomersThread = new GetCustomersThread();
        Platform.runLater(getCustomersThread);
        List<Customer> customersList = getCustomersThread.getCustomers();
        ObservableList<Customer> observableCustomersList = FXCollections.observableArrayList(customersList);
        tableViewCustomer.setItems(observableCustomersList);
    }

    @FXML
    void cancel() {
        ButtonType option = confirmation("Cancel", "Cancel action?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            usernameTextField.clear();
            firstPasswordField.clear();
            secondPasswordField.clear();
            secondScreen.setVisible(false);
            firstScreen.setVisible(true);
            Platform.runLater(this::refreshCustomersTable);
        }
    }

    @FXML
    void finish() {
        ButtonType option = confirmation("Finish", "Finish action?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {

            String username = usernameTextField.getText();
            String password = firstPasswordField.getText();
            String repeatedPassword = secondPasswordField.getText();

            String directoryPath = "usernames/";

            Path filePath = Paths.get(directoryPath, username);

            boolean fileExists = Files.exists(filePath);

            boolean flag;

            if (fileExists && !username.isEmpty()) {
                showAlertError("Error!", "Username already exists!", "This username already exists!");
                flag = true;
            } else {
                if (!password.isEmpty() && password.equals(repeatedPassword) && !username.isEmpty()) {
                    String hashedPassword = PasswordHashing.hashPassword(repeatedPassword);
                    createNewCustomer(newCustomer, username, hashedPassword);
                    flag = false;
                } else {
                    showAlertError("Error!", "Wrong input!", "Please enter a valid username and check if your passwords are the same!");
                    flag = true;
                }
            }

            usernameTextField.clear();
            firstPasswordField.clear();
            secondPasswordField.clear();

            reset();

            if (!flag) {
                secondScreen.setVisible(false);
                firstScreen.setVisible(true);
            }

            Platform.runLater(this::refreshCustomersTable);
        }
    }

    @FXML
    void addCustomer() {
        ButtonType option = confirmation("Customer", "Add customer?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            try {
                String name = customerNameTextField.getText();
                String address = customerAddressTextField.getText();
                String gender = customerGenderComboBox.getValue();
                Integer phoneNum = Integer.valueOf(customerPhoneNumTextField.getText());
                String schedule = customerScheduleComboBox.getValue();
                LocalDate startDate = customerStartDatePicker.getValue();
                LocalDate endDate = customerEndDatePicker.getValue();
                Optional<Trainer> trainer = Optional.ofNullable(customerTrainerComboBox.getValue());
                String status = statusComboBox.getValue();
                String radioBtn = String.valueOf(radioBtnYes.isSelected());

                Optional<Integer> trainerID = trainerList.stream()
                        .map(Trainer::getID)
                        .findFirst();

                if(trainer.isPresent()){
                    trainerID = Optional.of(trainer.get().getID());
                }

                checkDate();
                checkNameAndAddress();

                newCustomer = new CustomerBuilder()
                        .setName(name)
                        .setAddress(address)
                        .setGender(gender)
                        .setPhoneNum(phoneNum)
                        .setTrainerID(trainerID.get())
                        .setSchedule(schedule)
                        .setStartDate(startDate)
                        .setEndDate(endDate)
                        .setGroup(radioBtn)
                        .setStatus(status)
                        .createCustomer();

                firstScreen.setVisible(false);
                secondScreen.setVisible(true);
            }
            catch (StartEndDateException e){
                showAlertError("Error", "Check date fields", "Wrong date input");
            }
            catch (NumberFormatException e){
                showAlertError("Error", "Check Phone Number field", "Wrong phone number input");
            }
            catch (WrongNameAddressException e){
                showAlertError("Error", "Check Name and Address fields", "Wrong input");
            }
        }
    }

    static void createNewCustomer(Customer newCustomer, String username, String repeatedPassword) {
        String filePath = "usernames/" + username;

        try {
            FileWriter fileWriter = new FileWriter(filePath, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.write(repeatedPassword);
            bufferedWriter.close();

            AddUserThread addUserThread = new AddUserThread(new User<>(new CustomerBuilder().withExistingValues(newCustomer).setUsername(username).setPassword(repeatedPassword).createCustomer()));
            Platform.runLater(addUserThread);

            AlertScreen.showAlertConfirmation("Well done!", "Success!", "Successfully created a new customer: " + username + "!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            logger.error("Greska u stvaranju korisnika" + e.getMessage());
        }
    }

    @FXML
    void searchCustomers(){
        GetCustomersThread getCustomersThread = new GetCustomersThread();
        Platform.runLater(getCustomersThread);
        List<Customer> customerList = getCustomersThread.getCustomers();

        List<Customer> filteredCustomerList = customerList.stream().filter(customer -> customer.getName().contains(filterTextField.getText())).toList();
        ObservableList<Customer> observableList = FXCollections.observableArrayList(filteredCustomerList);

        tableViewCustomer.setItems(observableList);
    }

    @FXML
    void sortCustomers(){
        GetCustomersThread getCustomersThread = new GetCustomersThread();
        Platform.runLater(getCustomersThread);
        List<Customer> customerList = getCustomersThread.getCustomers();

        List<Customer> sortedCustomerList = customerList.stream().sorted(Comparator.comparing(Customer::getName)).toList();
        ObservableList<Customer> observableList = FXCollections.observableArrayList(sortedCustomerList);
        tableViewCustomer.setItems(observableList);
    }

    private void checkDate() throws StartEndDateException {
        if (customerStartDatePicker.getValue() == null || customerEndDatePicker.getValue() == null || customerStartDatePicker.getValue().isAfter(customerEndDatePicker.getValue()) || customerStartDatePicker.getValue().isBefore(LocalDate.now()) || customerEndDatePicker.getValue().equals(customerStartDatePicker.getValue())) {
            throw new StartEndDateException("Krivi unos datuma");
        }
    }

    private void checkNameAndAddress() {
        if(customerNameTextField.getText().isEmpty() || customerAddressTextField.getText().isEmpty()){
            throw new WrongNameAddressException("Greska kod unosa imena");
        }
    }

    static void changeLog(Customer selectedCustomer, Customer newCustomer) {
        Field[] fields = Customer.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if(field.getName().equals("ID") || field.getName().equals("username") || field.getName().equals("password")){
                continue;
            }
            else{
                try {
                    Object originalValue = field.get(selectedCustomer);
                    Object updatedValue = field.get(newCustomer);

                    if (!Objects.equals(originalValue, updatedValue)) {
                        LogObject<?, ?> logObject = new LogObject<>(field.getName(), originalValue, updatedValue, MainApplication.user.getRole().toString(), selectedCustomer.toString());
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
