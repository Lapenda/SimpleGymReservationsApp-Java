package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.enums.TrainerStatus;
import hr.java.application.exceptions.checked.StartEndDateException;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.ConfirmationWindow;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Customer;
import hr.java.application.model.CustomerBuilder;
import hr.java.application.model.Trainer;
import hr.java.application.threads.GetCurrentUserThread;
import hr.java.application.threads.GetTrainersThread;
import hr.java.application.threads.UpdateCustomerThread;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public non-sealed class ReserveASessionController implements ConfirmationWindow, AlertScreen {

    private final String[] schedule = {"6AM - 8AM", "8AM - 10AM", "10AM - 12PM", "1PM - 3PM", "3PM - 5PM", "5PM - 7PM", "7PM - 9PM"};

    @FXML
    private DatePicker customerEndDatePicker;

    @FXML
    private ComboBox<String> customerScheduleComboBox;

    @FXML
    private DatePicker customerStartDatePicker;

    @FXML
    private ComboBox<Trainer> customerTrainerComboBox;

    @FXML
    private RadioButton radioBtnYes;

    Customer customer;

    public void initialize(){
        GetCurrentUserThread getCurrentUserThread = new GetCurrentUserThread(MainApplication.userString);
        Platform.runLater(getCurrentUserThread);
        customer = getCurrentUserThread.getCurrentUser(MainApplication.userString);

        GetTrainersThread getTrainersThread = new GetTrainersThread();
        Platform.runLater(getTrainersThread);
        List<Trainer> trainerList = getTrainersThread.getTrainers();

        List<Trainer> activeTrainerList = trainerList.stream().filter(trainer -> trainer.getStatus().equals(TrainerStatus.ACTIVE.toString())).toList();
        ObservableList<Trainer> observableActiveTrainerList = FXCollections.observableArrayList(activeTrainerList);
        customerTrainerComboBox.setItems(observableActiveTrainerList);

        List<String> scheduleList = new ArrayList<>(Arrays.asList(schedule));
        ObservableList<String> observableScheduleList = FXCollections.observableArrayList(scheduleList);
        customerScheduleComboBox.setItems(observableScheduleList);

        customerScheduleComboBox.setValue(customer.getSchedule());
        customerTrainerComboBox.setValue(trainerList.stream().filter(trainer -> trainer.getID().equals(customer.getTrainerID())).findFirst().get());
        radioBtnYes.setSelected(Boolean.parseBoolean(customer.getGroup()));
        customerEndDatePicker.setValue(customer.getEndDate());
        customerStartDatePicker.setValue(customer.getStartDate());

    }

    @FXML
    void reserveASession() {
        ButtonType option = confirmation("Reserve", "Reserve a session?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            try {
                LocalDate startDate = customerStartDatePicker.getValue();
                LocalDate endDate = customerEndDatePicker.getValue();

                checkDate(startDate, endDate);

                Customer newCustomer = new CustomerBuilder()
                        .setName(customer.getName())
                        .setPhoneNum(customer.getPhoneNum())
                        .setGender(customer.getGender())
                        .setAddress(customer.getAddress())
                        .setTrainerID(customerTrainerComboBox.getValue().getID())
                        .setSchedule(customerScheduleComboBox.getValue())
                        .setStartDate(customerStartDatePicker.getValue())
                        .setEndDate(customerEndDatePicker.getValue())
                        .setGroup(String.valueOf(radioBtnYes.isSelected()))
                        .createCustomer();

                changeLog(newCustomer);

                UpdateCustomerThread updateCustomerThread = new UpdateCustomerThread(newCustomer, customer.getID());
                Platform.runLater(updateCustomerThread);

                NewScreen.showNewScreen("profile-view.fxml");

                AlertScreen.showAlertInfo("Reservation", ":)", "Press OK to pay now or CANCEL to pay later (your schedule will be saved)", "pay-view.fxml");
            } catch (StartEndDateException e) {
                logger.error("Greska u rezervieanju termina" + e.getMessage());
                System.out.println(e.getMessage());
                showAlertError("Error", "Check all fields", "Check all fields and Date inputs!");
            }
        }
    }

    private static void checkDate(LocalDate startDate, LocalDate endDate) throws StartEndDateException {
        if (startDate == null || endDate == null || startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now()) || startDate.equals(endDate)) {
            throw new StartEndDateException("Krivi unos datuma");
        }
    }

    private void changeLog(Customer newCustomer) {
        Field[] fields = Customer.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if(field.getName().equals("ID") || field.getName().equals("username") || field.getName().equals("password")){
                continue;
            }
            else{
                try {
                    Object originalValue = field.get(customer);
                    Object updatedValue = field.get(newCustomer);

                    if (!Objects.equals(originalValue, updatedValue)) {
                        LogObject<?, ?> logObject = new LogObject<>(field.getName(), originalValue, updatedValue, customer.toString(), customer.toString());
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
