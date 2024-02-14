package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.databaseUtils.Database;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Admin;
import hr.java.application.model.Customer;
import hr.java.application.model.User;
import hr.java.application.threads.GetCurrentUserThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuBarController implements NewScreen, AlertScreen {

    @FXML
    private Label currentUser;

    @FXML
    private Menu aboutYouMenu;

    @FXML
    private Menu staffMenu;

    @FXML
    private MenuItem profileMenuItem;

    @FXML
    private MenuItem dashboardMenuItem;

    Customer customer;

    public void initialize() {
        GetCurrentUserThread getCurrentUserThread = new GetCurrentUserThread(MainApplication.userString);
        Platform.runLater(getCurrentUserThread);
        customer = getCurrentUserThread.getCurrentUser(MainApplication.userString);

        if(MainApplication.user.getRole() instanceof Admin){
            currentUser.setText("Hello boss!");
            aboutYouMenu.setVisible(false);
            profileMenuItem.setVisible(false);
        }
        else{
            currentUser.setText("Hello: " + customer.getUsername() + " (" + customer.getStatus() + ")");
            staffMenu.setVisible(false);
            dashboardMenuItem.setVisible(false);
        }
    }

    public void signOutClick() {
        AlertScreen.showAlertInfo(":(", "Logout", "Are you sure you want to logout?", "main-view.fxml");
    }

    public void goToTrainerInfo() { NewScreen.showNewScreen("trainerInfo-view.fxml");}

    public void goToProfile() { NewScreen.showNewScreen("profile-view.fxml");}

    public void goToCustomerInfo() { NewScreen.showNewScreen("customerInfo-view.fxml");}

    public void goToDashboard() { NewScreen.showNewScreen("dashboard-view.fxml");}

    public void goToResSession() { NewScreen.showNewScreen("reserveSession-view.fxml");}

    public void goPay() { NewScreen.showNewScreen("pay-view.fxml");}

    public void showChanges() { NewScreen.showNewScreen("changes-view.fxml");}
}