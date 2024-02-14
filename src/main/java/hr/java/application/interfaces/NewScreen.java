package hr.java.application.interfaces;

import hr.java.application.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface NewScreen {
    Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @FXML
    static void showNewScreen(String fxmlName){
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxmlName));
        try{
            Scene scene = new Scene(fxmlLoader.load(), 1000, 620);
            MainApplication.getMainStage().setTitle("Laps Gym");
            MainApplication.getMainStage().setScene(scene);
            MainApplication.getMainStage().show();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
        }
    }
}
