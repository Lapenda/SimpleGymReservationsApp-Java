package hr.java.application.exceptions.checked;

import static hr.java.application.MainApplication.logger;

public class StartEndDateException extends Exception {
    public StartEndDateException(String message) {
        super(message);
        logger.error(message);
    }
}
