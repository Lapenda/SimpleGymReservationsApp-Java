package hr.java.application.threads;

import hr.java.application.MainApplication;
import hr.java.application.databaseUtils.Database;
import hr.java.application.model.*;
import javafx.application.Platform;
import javafx.scene.chart.AreaChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public abstract class DatabaseThread {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static boolean activeConnectionWithDatabase = false;

    public synchronized void addUser(User<?> user) {

            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.addUser(user);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized List<Trainer> getTrainers() {

            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            List<Trainer> trainers = Database.getTrainers();
            activeConnectionWithDatabase = false;
            notifyAll();
            return trainers;
    }

    public synchronized void deleteTrainer(Trainer trainer) {

            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.deleteTrainer(trainer);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized void updateTrainer(Trainer selectedTrainer, Integer id) {
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.updateTrainer(selectedTrainer, id);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized void deleteCustomer(Customer selectedCustomer) {
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.deleteCustomer(selectedCustomer);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized void updateCustomer(Customer selectedCustomer, Integer id) {

            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.updateCustomer(selectedCustomer, id);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized List<Customer> getCustomers() {

            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            List<Customer> list = Database.getCustomers();
            activeConnectionWithDatabase = false;
            notifyAll();
            return list;
    }

    public synchronized Customer getCurrentUser(String username){
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Customer user = Database.getCurrentUser(username);
            activeConnectionWithDatabase = false;
            notifyAll();
            return user;
    }

    public synchronized void updateCustomerStatus(Customer customer, String status) {
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.updateCustomerStatus(customer, status);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized void updateCustomerProfile(Customer updatedCustomer, Integer id) {
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Database.updateCustomerProfile(updatedCustomer, id);
            activeConnectionWithDatabase = false;
            notifyAll();
    }

    public synchronized List<Customer> getPaidCustomers(){
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            List<Customer> list = Database.getPaidCustomers();
            activeConnectionWithDatabase = false;
            notifyAll();
            return list;
    }

    public synchronized void displayChart(AreaChart<String, Integer> chart){
            while (activeConnectionWithDatabase) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Error while waiting for connection with database to be free: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithDatabase = true;
            Platform.runLater(() -> {
            Database.displayChart(chart);
            });
            //Database.displayChart(chart);
            activeConnectionWithDatabase = false;
            notifyAll();
    }
}
