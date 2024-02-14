package hr.java.application.exceptions.checked;

import static hr.java.application.MainApplication.logger;

public class WrongCredentialsException extends Exception {
    public WrongCredentialsException(String message) {
        super(message);
        logger.error(message);
    }
}