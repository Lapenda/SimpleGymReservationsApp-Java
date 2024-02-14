package hr.java.application.controllers;

import hr.java.application.interfaces.NewScreen;

public class MainController implements NewScreen {

    public void registerButtonClick() {
        NewScreen.showNewScreen("register-view.fxml");
    }

    public void logInButtonClick() {
        NewScreen.showNewScreen("login-view.fxml");
    }
}