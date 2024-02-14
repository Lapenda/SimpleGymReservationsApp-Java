package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.enums.Gender;
import hr.java.application.exceptions.unchecked.WrongNameAddressException;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.ConfirmationWindow;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Customer;
import hr.java.application.model.CustomerBuilder;
import hr.java.application.model.User;
import hr.java.application.threads.GetCurrentUserThread;
import hr.java.application.threads.UpdateCustomerProfileThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public non-sealed class FirstTimeLoggingInController implements AlertScreen, ConfirmationWindow {

    @FXML
    private TextField customerAddressTextField;

    @FXML
    private ComboBox<String> customerGenderComboBox;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private TextField customerPhoneNumTextField;

    Customer customer;

    public void initialize(){
        customerGenderComboBox.getItems().setAll(Gender.MALE.toString(), Gender.FEMALE.toString(), Gender.OTHER.toString());

        GetCurrentUserThread getCurrentUserThread = new GetCurrentUserThread(MainApplication.userString);
        Platform.runLater(getCurrentUserThread);
        customer = getCurrentUserThread.getCurrentUser(MainApplication.userString);
    }

    @FXML
    void finish() {
        ButtonType option = confirmation("Finish", "Finish action?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            try {
                checkNameAndAddres();
                
                User<Customer> newCustomer = new User<>(new CustomerBuilder().withExistingUsPass((Customer) MainApplication.user.getRole())
                        .setAddress(customerAddressTextField.getText())
                        .setName(customerNameTextField.getText())
                        .setGender(customerGenderComboBox.getValue())
                        .setPhoneNum(Integer.valueOf(customerPhoneNumTextField.getText()))
                        .createCustomer());

                UpdateCustomerProfileThread updateCustomerProfileThread = new UpdateCustomerProfileThread(newCustomer.getRole(), customer.getID());
                Platform.runLater(updateCustomerProfileThread);

                NewScreen.showNewScreen("profile-view.fxml");
            } catch (WrongNameAddressException e) {
                logger.error("Sva polja moraju biti popunjena");
                showAlertError("Error", "Check inputs", "Input at least name, address and phone number");
            }
            catch (NumberFormatException e){
                logger.error("Krivi unos brojeva");
                showAlertError("Error", "Wrong input", "Only numbers can go in Phone Number text field!");
            }
        }
    }

    private void checkNameAndAddres() {
        if(customerNameTextField.getText().isEmpty() || customerAddressTextField.getText().isEmpty()){
            throw new WrongNameAddressException("Greska kod logiranja prvi put");
        }
    }
}
