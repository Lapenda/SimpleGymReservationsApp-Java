package hr.java.application.exceptions.unchecked;

import static hr.java.application.MainApplication.logger;

public class NotEnoughMoneyException extends RuntimeException{
    public NotEnoughMoneyException(String message) {
        super(message);
        logger.error(message);
    }
}
