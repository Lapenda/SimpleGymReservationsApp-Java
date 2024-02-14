package hr.java.application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import static hr.java.application.MainApplication.logger;

public class ChangesController {

    @FXML
    private TextArea textArea;

    public void initialize() {

        try (BufferedReader reader = new BufferedReader(new FileReader("changeLogs/deserialized.txt"))) {
            String line;
            while(Optional.ofNullable(line = reader.readLine()).isPresent()) {
                textArea.appendText(line + "\n");
            }
        } catch (IOException e) {
            logger.error("Greska u citanju podataka" + e.getMessage());
            e.printStackTrace();
        }
    }
}
