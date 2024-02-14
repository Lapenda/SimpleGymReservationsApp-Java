package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.enums.PaidStatus;
import hr.java.application.exceptions.unchecked.NotEnoughMoneyException;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.ConfirmationWindow;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Customer;
import hr.java.application.threads.DeleteCustomerThread;
import hr.java.application.threads.GetCurrentUserThread;
import hr.java.application.threads.UpdateCustomerStatusThread;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public non-sealed class PayController implements AlertScreen, ConfirmationWindow {

    @FXML
    private TextField amount;

    @FXML
    private Label change;

    @FXML
    private Label money;

    @FXML
    private TableColumn<Customer, String> endDateTableColumn;

    @FXML
    private TableView<Customer> paymentTableView;

    @FXML
    private TableColumn<Customer, String> startDateTableColumn;

    @FXML
    private TableColumn<Customer, String> statusTableColumn;

    @FXML
    private Button payBtn;

    Customer customer;

    private long varChange = 0L;

    public void initialize() {
        GetCurrentUserThread getCurrentUserThread = new GetCurrentUserThread(MainApplication.userString);
        Platform.runLater(getCurrentUserThread);
        customer = getCurrentUserThread.getCurrentUser(MainApplication.userString);

        money.setText(customer.getPrice().toString());
        change.setText(String.valueOf(varChange));


        startDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStartDate().toString());
            }
        });

        endDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getEndDate().toString());
            }
        });

        statusTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStatus());
            }
        });

        if (customer.getStatus().equals("PAID")) {
            payBtn.setDisable(true);
        }

        Platform.runLater(this::refreshPaymentTable);
    }

    @FXML
    void cancelMembership() {
        ButtonType option = confirmation("Delete", "Cancel membership?", "Are you sure?");
        if(option.equals(ButtonType.OK)){
            DeleteCustomerThread deleteCustomerThread = new DeleteCustomerThread(customer);
            Platform.runLater(deleteCustomerThread);
            NewScreen.showNewScreen("main-view.fxml");
        }
    }

    @FXML
    void displayChange() {
        try {
            if (!amount.getText().isEmpty()) {
                varChange = Long.parseLong(amount.getText()) - Long.parseLong(money.getText());
                change.setText(String.valueOf(varChange));
            }
        }catch (NumberFormatException e){
            logger.error("Nije unesen broj za iznos " + e.getMessage());
            showAlertError("Error", "Wrong input", "Please input just numbers in the field!");
        }
    }

    @FXML
    void pay() {
        ButtonType option = confirmation("Pay", "Pay membership?", "Are you sure?");
        if(option.equals(ButtonType.OK)) {
            try {
                checkMoney();

                UpdateCustomerStatusThread updateCustomerStatusThread = new UpdateCustomerStatusThread(customer, "PAID");
                Platform.runLater(updateCustomerStatusThread);
                LogObject<?, ?> logObject = new LogObject<>("status", PaidStatus.UNPAID.toString(), PaidStatus.PAID.toString(), customer.toString(), customer.toString());
                LogObject.serializeChangeLogs(logObject);
                LogObject.deserializeChangeLogs();

                AlertScreen.showAlertConfirmation("Hooray", "Well done!", "Payment was successful!");
                NewScreen.showNewScreen("profile-view.fxml");

            } catch (NotEnoughMoneyException e){
                logger.error("Nedovoljno novca " + e.getMessage());
                showAlertError("Oops", "Money", "Not enough money!");
            }
        }
    }

    private void checkMoney(){
        if(varChange < 0 || amount.getText().isEmpty()){
            throw new NotEnoughMoneyException("Nema dovoljno novca");
        }
    }

    private void refreshPaymentTable() {
        List<Customer> customersList = new ArrayList<>();
        customersList.add(customer);
        ObservableList<Customer> observableCustomersList = FXCollections.observableArrayList(customersList);
        paymentTableView.setItems(observableCustomersList);
    }
}