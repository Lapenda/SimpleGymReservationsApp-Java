package hr.java.application.controllers;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.databaseUtils.Database;
import hr.java.application.interfaces.AlertScreen;
import hr.java.application.hash.PasswordHashing;
import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.Admin;
import hr.java.application.model.Customer;
import hr.java.application.model.CustomerBuilder;
import hr.java.application.model.User;
import hr.java.application.threads.AddUserThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public class RegisterController implements NewScreen, AlertScreen {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField firstPasswordField;
    @FXML
    PasswordField secondPasswordField;

    public void registerButton(){

        String username = usernameTextField.getText();
        String password = firstPasswordField.getText();
        String repeatedPassword = secondPasswordField.getText();

        String directoryPath = "usernames/";

        Path filePath = Paths.get(directoryPath, username);

        boolean fileExists = Files.exists(filePath);

        if (fileExists && !username.isEmpty()) {
            showAlertError("Error!", "Username already exists!", "This username already exists, we will transfer you to Log in screen");
            NewScreen.showNewScreen("login-view.fxml");
        } else {
            if (!password.isEmpty() && password.equals(repeatedPassword) && !username.isEmpty()) {
                String hashedPassword = PasswordHashing.hashPassword(repeatedPassword);
                createNewUser(username, hashedPassword);
                NewScreen.showNewScreen("firstTimeLoggingIn-view.fxml");
            } else {
                showAlertError("Error!", "Wrong input!", "Please enter a valid username and check if your passwords are the same!");
            }
        }
    }

    public void goToLogInButton(){
        NewScreen.showNewScreen("login-view.fxml");
    }

    private static void createNewUser(String username, String repeatedPassword) {
        String filePath = "usernames/" + username;

        try {
            FileWriter fileWriter = new FileWriter(filePath, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.write(repeatedPassword);
            bufferedWriter.close();

            MainApplication.userString = username;

            if (MainApplication.userString.equals("admin")) {
                MainApplication.user = new User<>(new Admin("admin", repeatedPassword));
            }
            else{
                MainApplication.user = new User<>(new CustomerBuilder().setUsername(username).setPassword(repeatedPassword).createCustomer());
            }

            AddUserThread addUserThread = new AddUserThread(MainApplication.user);
            Platform.runLater(addUserThread);

            AlertScreen.showAlertConfirmation("Wellcome!", "Hi " + username + "!", "You have to finish registering on the next screen if you want to be in our database");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            logger.error("Greska u stvaranju korisnika " + e.getMessage());
        }
    }
}
