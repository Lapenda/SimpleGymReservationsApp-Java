package hr.java.application.exceptions.unchecked;

import static hr.java.application.MainApplication.logger;

public class WrongNameAddressException extends RuntimeException {
    public WrongNameAddressException(String message){
        super(message);
        logger.error(message);
    }
}
