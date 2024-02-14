module hr.java.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires jbcrypt;
    requires java.sql;
    requires com.h2database;

    exports hr.java.application.controllers;
    opens hr.java.application.controllers to javafx.fxml;

    exports hr.java.application;
    opens hr.java.application to javafx.fxml;
    exports hr.java.application.enums;
    opens hr.java.application.enums to javafx.fxml;
}