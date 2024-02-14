package hr.java.application;

import hr.java.application.interfaces.NewScreen;
import hr.java.application.model.User;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApplication extends Application implements NewScreen {

    public static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    private static Stage mainStage;

    public static String userString;

    public static User<?> user;

    @Override
    public void start(Stage stage){
        mainStage = stage;
        NewScreen.showNewScreen("main-view.fxml");
    }

    public static Stage getMainStage()
    {
        return mainStage;
    }

    public static void main(String[] args) {
        logger.info("Program je pokrenut!");
        launch();
    }
}