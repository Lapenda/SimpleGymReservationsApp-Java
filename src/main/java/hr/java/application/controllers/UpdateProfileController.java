package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.enums.Gender;
import hr.java.application.exceptions.unchecked.WrongNameAddressException;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.ConfirmationWindow;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Customer;
import hr.java.application.model.CustomerBuilder;
import hr.java.application.threads.GetCurrentUserThread;
import hr.java.application.threads.UpdateCustomerProfileThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.util.Objects;

public non-sealed class UpdateProfileController implements ConfirmationWindow, AlertScreen {

    @FXML
    private TextField customerAddressTextField;

    @FXML
    private ComboBox<String> customerGenderComboBox;

    @FXML
    private TextField customerIDTextField;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private TextField customerPhoneNumTextField;

    Customer currentUser;

    public void initialize(){
        GetCurrentUserThread getCurrentUserThread = new GetCurrentUserThread(MainApplication.userString);
        Platform.runLater(getCurrentUserThread);
        currentUser = getCurrentUserThread.getCurrentUser(MainApplication.userString);

        customerGenderComboBox.getItems().setAll(Gender.MALE.toString(), Gender.FEMALE.toString(), Gender.OTHER.toString());
        customerIDTextField.setText(currentUser.getID().toString());

        customerPhoneNumTextField.setText(currentUser.getPhoneNum().toString());
        customerNameTextField.setText(currentUser.getName());
        customerGenderComboBox.setValue(currentUser.getGender());
        customerAddressTextField.setText(currentUser.getAddress());
    }

    @FXML
    void update() {
        ButtonType option = confirmation("Update", "Update information?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            try {
                checkProfileInfo();

                Customer updatedCustomer = new CustomerBuilder()
                        .setName(customerNameTextField.getText())
                        .setAddress(customerAddressTextField.getText())
                        .setGender(customerGenderComboBox.getValue())
                        .setPhoneNum(Integer.valueOf(customerPhoneNumTextField.getText()))
                        .createCustomer();

                changeLog(updatedCustomer);

                UpdateCustomerProfileThread updateCustomerProfileThread = new UpdateCustomerProfileThread(updatedCustomer, currentUser.getID());
                Platform.runLater(updateCustomerProfileThread);
                AlertScreen.showAlertConfirmation("Updated", "Well done", "Successfully updated profile info!");
                NewScreen.showNewScreen("profile-view.fxml");

            } catch (WrongNameAddressException e) {
                logger.error("Greska u azuriranju informacija o korisniku " + e.getMessage());
                showAlertError("Error", "Wrong input", "Name, address and phone number can't be empty");
            }
            catch (NumberFormatException e){
                logger.error("Nije unesen broj");
                showAlertError("Error", "Wrong input", "Input only numbers for phone number");
            }
        }
    }

    private void checkProfileInfo() {
        if(customerNameTextField.getText().isEmpty() || customerAddressTextField.getText().isEmpty()){
            throw new WrongNameAddressException("Nije uneseno ime ili adresa");
        }
    }

    private void changeLog(Customer updatedCustomer) {
        Field[] fields = Customer.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if(field.getName().equals("ID") || field.getName().equals("username") || field.getName().equals("password")
            || field.getName().equals("schedule") || field.getName().equals("group") || field.getName().equals("status")
                    || field.getName().equals("trainerID") || field.getName().equals("startDate") || field.getName().equals("endDate") || field.getName().equals("price"))
            {
                continue;
            }
            else{
                try {
                    Object originalValue = field.get(currentUser);
                    Object updatedValue = field.get(updatedCustomer);

                    if (!Objects.equals(originalValue, updatedValue)) {
                        LogObject<?, ?> logObject = new LogObject<>(field.getName(), originalValue, updatedValue, currentUser.toString(), currentUser.toString());
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
