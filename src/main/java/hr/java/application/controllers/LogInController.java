package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.exceptions.checked.WrongCredentialsException;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.hash.PasswordHashing;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Admin;
import hr.java.application.model.CustomerBuilder;
import hr.java.application.model.User;
import hr.java.application.records.UserCredentials;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class LogInController implements NewScreen, AlertScreen {
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordField;

    public void logInButton() {
        String username = usernameTextField.getText();

        String directoryPath = "usernames/";

        Path filePath = Paths.get(directoryPath, username);

        boolean fileExists = Files.exists(filePath);

        try {
            checkCredentials();
        }
        catch (WrongCredentialsException e){
            showAlertError("Error", ":(", "All fields must be filled");
        }

        if(fileExists){
            String password = passwordField.getText();

            String filePathString = "usernames/" + username;

            try (Scanner scanner = new Scanner(Paths.get(filePathString))) {
                for (int lineNumber = 1; scanner.hasNextLine(); lineNumber++) {
                    String line = scanner.nextLine();
                    if (lineNumber == 2) {
                        if (PasswordHashing.checkPassword(password, line)) {
                            AlertScreen.showAlertConfirmation("Wellcome!", "Hi " + username + "!", "Wellcome");
                            MainApplication.userString = username;

                            if (MainApplication.userString.equals("admin")) {
                                MainApplication.user = new User<>(new Admin("admin"));
                                NewScreen.showNewScreen("dashboard-view.fxml");
                                UserCredentials userCredentials = new UserCredentials("admin");
                                System.out.println("Current user: " + userCredentials.username());
                            }
                            else{
                                MainApplication.user = new User<>(new CustomerBuilder().setUsername(username).createCustomer());
                                NewScreen.showNewScreen("profile-view.fxml");
                                UserCredentials userCredentials = new UserCredentials(username);
                                System.out.println("Current user: " + userCredentials.username());
                            }
                        } else {
                            if(!password.isEmpty()){
                                showAlertError("Error!", "Wrong password!", "Your password is incorrect!");
                            }
                        }
                    }
                }
            }
            catch (IOException e) {
                logger.error("Greska u loginu " + e.getMessage());
            }
        }
        else{
             if(!usernameTextField.getText().isEmpty() && !passwordField.getText().isEmpty()){
                 showAlertError("Error!", "Wrong username!", "Your username doesn't exist!");
             }
        }
    }

    private void checkCredentials() throws WrongCredentialsException {
        if(usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty()){
            throw new WrongCredentialsException("Nisu uneseni korisnicko ime ili lozinka");
        }
    }

    public void goToRegisterButton(){
        NewScreen.showNewScreen("register-view.fxml");
    }
}
