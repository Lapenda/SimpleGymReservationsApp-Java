package hr.java.application.changes;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static hr.java.application.MainApplication.logger;

public class LogObject<T, X> implements Serializable {

    private LocalDateTime timestamp;
    private String changedData;
    private T oldValue;
    private X newValue;
    private String role;
    private String username;

    public LogObject(String changedData, T oldValue, X newValue, String role, String username) {
        this.timestamp = LocalDateTime.now();
        this.changedData = changedData;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.role = role;
        this.username = username;
    }

    private static final String FILE_PATH = "changeLogs/changelog.bin";

    public static void serializeChangeLogs(LogObject<?, ?> changeLogObject) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
                oos.writeObject(changeLogObject);
        } catch (IOException e) {
            logger.error("Greska kod serijalizacije");
            e.printStackTrace();
        }
    }

    public static void deserializeChangeLogs() {

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            while (true) {
                try{
                    LogObject<?, ?> changeLogObject = (LogObject<?, ?>) ois.readObject();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("changeLogs/deserialized.txt", true))) {
                        writer.write(changeLogObject.toString() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (EOFException e) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getChangedData() {
        return changedData;
    }

    public void setChangedData(String changedData) {
        this.changedData = changedData;
    }

    public T getOldValue() {
        return oldValue;
    }

    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    public X getNewValue() {
        return newValue;
    }

    public void setNewValue(X newValue) {
        this.newValue = newValue;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("[%s] Changed data -> %s: %s -> %s (Changed by: %s to user: %s)", timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), changedData, oldValue, newValue, role, username);
    }
}
