package hr.java.application.databaseUtils;

import hr.java.application.MainApplication;
import hr.java.application.changes.LogObject;
import hr.java.application.enums.PaidStatus;
import hr.java.application.model.*;
import hr.java.application.threads.GetTrainersThread;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    private static final String DATABASE_URL = "conf/database.properties";

    private static synchronized Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_URL));
        String url = properties.getProperty("databaseURL");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(url, username, password);
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void addUser(User<?> user) {

        try (Connection connection = connectToDatabase()) {

            if(user.getRole() instanceof Admin){
                String sqlQuery = "INSERT INTO ADMIN (USERNAME, PASSWORD) VALUES (?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, ((Admin) user.getRole()).getUsername());
                preparedStatement.setString(2, ((Admin) user.getRole()).getPassword());
                preparedStatement.execute();
                preparedStatement.close();
            }
            else if(user.getRole() instanceof Trainer){
                String sqlQuery = "INSERT INTO TRAINERS (NAME, ADDRESS, GENDER, PHONE_NUMBER, STATUS) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, ((Trainer) user.getRole()).getName());
                preparedStatement.setString(2, ((Trainer) user.getRole()).getAddress());
                preparedStatement.setString(3, ((Trainer) user.getRole()).getGender());
                preparedStatement.setInt(4, ((Trainer) user.getRole()).getPhoneNum());
                preparedStatement.setString(5, ((Trainer) user.getRole()).getStatus());

                preparedStatement.execute();
                preparedStatement.close();

                System.out.println("New Trainer added successfully.");
            }
            else{
                String sqlQuery = "INSERT INTO CUSTOMERS (USERNAME, PASSWORD, NAME, ADDRESS, GENDER, SCHEDULE, TRAINER_ID, GROUP_NAME, PHONE_NUM, START_DATE, END_DATE, PRICE, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, ((Customer) user.getRole()).getUsername());
                preparedStatement.setString(2, ((Customer) user.getRole()).getPassword());
                preparedStatement.setString(3, ((Customer) user.getRole()).getName());
                preparedStatement.setString(4, ((Customer) user.getRole()).getAddress());
                preparedStatement.setString(5, ((Customer) user.getRole()).getGender());
                preparedStatement.setString(6, ((Customer) user.getRole()).getSchedule());
                preparedStatement.setInt(7, ((Customer) user.getRole()).getTrainerID());
                preparedStatement.setString(8, ((Customer) user.getRole()).getGroup());
                preparedStatement.setInt(9, ((Customer) user.getRole()).getPhoneNum());
                preparedStatement.setDate(10, Date.valueOf(((Customer) user.getRole()).getStartDate()));
                preparedStatement.setDate(11, Date.valueOf(((Customer) user.getRole()).getEndDate()));
                preparedStatement.setLong(12, ((Customer) user.getRole()).getPrice());
                preparedStatement.setString(13, ((Customer) user.getRole()).getStatus());

                preparedStatement.execute();
                preparedStatement.close();

                System.out.println("New Customer added successfully.");
            }

        } catch (SQLException | IOException e) {
            logger.error("Greska u dodavanju trenera u bazu podataka!");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static List<Trainer> getTrainers() {
        List<Trainer> trainerList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {

            String sqlQuery = "SELECT * FROM trainers";
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                Trainer trainer = new TrainerBuilder().setID(rs.getInt("ID"))
                        .setName(rs.getString("NAME"))
                        .setAddress(rs.getString("ADDRESS"))
                        .setGender(rs.getString("GENDER"))
                        .setPhoneNum(rs.getInt("PHONE_NUMBER"))
                        .setStatus(rs.getString("STATUS"))
                        .createTrainer();
                trainerList.add(trainer);
            }
            statement.close();
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
            logger.error("Greska u dohvacanju trenera iz baze podataka!");
            e.printStackTrace();
        }

        return trainerList;
    }

    public static void deleteTrainer(Trainer trainer) {
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "DELETE FROM trainers WHERE ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, trainer.getID());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    Trainer.ids.remove(trainer.getID());
                    System.out.println("Trainer with ID " + trainer.getID() + " deleted successfully.");
                    String customerSqlQuery = "UPDATE CUSTOMERS SET TRAINER_ID = ? WHERE TRAINER_ID = ?";
                    try(PreparedStatement ps = connection.prepareStatement(customerSqlQuery)){
                        List<Trainer> trainerList = Database.getTrainers();
                        ps.setInt(1, trainerList.stream().map(Trainer::getID).findAny().get());
                        ps.setInt(2, trainer.getID());
                        ps.execute();
                    }
                } else {
                    System.out.println("Trainer with ID " + trainer.getID() + " not found.");
                }
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateTrainer(Trainer selectedTrainer, Integer id) {
        try(Connection connection = connectToDatabase()){
            String sqlQuery = "UPDATE TRAINERS SET NAME = ?, ADDRESS = ?, GENDER = ?, PHONE_NUMBER = ?, STATUS = ? WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, selectedTrainer.getName());
            preparedStatement.setString(2, selectedTrainer.getAddress());
            preparedStatement.setString(3, selectedTrainer.getGender());
            preparedStatement.setInt(4, selectedTrainer.getPhoneNum());
            preparedStatement.setString(5, selectedTrainer.getStatus());
            preparedStatement.setInt(6, id);

            preparedStatement.executeUpdate();
        }
        catch (SQLException | IOException e){
            logger.error("Greska u azuriranju podataka o treneru u bazi!");
            System.out.println(e.getMessage());
        }
    }

    public static void deleteCustomer(Customer selectedCustomer) {
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "DELETE FROM CUSTOMERS WHERE ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, selectedCustomer.getID());

                int affectedRows = preparedStatement.executeUpdate();

                String filePathString = "usernames/" + selectedCustomer.getUsername();
                Path filePath = Paths.get(filePathString);

                try {
                    Files.delete(filePath);
                    System.out.println("File deleted successfully");
                } catch (IOException e) {
                    logger.error("Unable to delete the file: " + e.getMessage());
                    System.err.println("Unable to delete the file: " + e.getMessage());
                }

                if (affectedRows > 0) {
                    System.out.println("Customer with ID " + selectedCustomer.getID() + " deleted successfully.");
                } else {
                    System.out.println("Customer with ID " + selectedCustomer.getID() + " not found.");
                }
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateCustomer(Customer selectedCustomer, Integer id) {
        try(Connection connection = connectToDatabase()){
            String sqlQuery = "UPDATE CUSTOMERS SET NAME = ?, ADDRESS = ?, GENDER = ?, SCHEDULE = ?, TRAINER_ID = ?," +
                    "GROUP_NAME = ?, PHONE_NUM = ?, START_DATE = ?, END_DATE = ?, PRICE = ?, STATUS = ? WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, selectedCustomer.getName());
            preparedStatement.setString(2, selectedCustomer.getAddress());
            preparedStatement.setString(3, selectedCustomer.getGender());
            preparedStatement.setString(4, selectedCustomer.getSchedule());
            preparedStatement.setInt(5, selectedCustomer.getTrainerID());
            preparedStatement.setString(6, selectedCustomer.getGroup());
            preparedStatement.setInt(7, selectedCustomer.getPhoneNum());
            preparedStatement.setDate(8,  java.sql.Date.valueOf(selectedCustomer.getStartDate()));
            preparedStatement.setDate(9, java.sql.Date.valueOf(selectedCustomer.getEndDate()));
            preparedStatement.setLong(10, selectedCustomer.getPrice());
            preparedStatement.setString(11, selectedCustomer.getStatus());
            preparedStatement.setInt(12, id);

            preparedStatement.executeUpdate();
        }
        catch (SQLException | IOException e){
            logger.error("Greska u azuriranju podataka o korisniku u bazi!");
            System.out.println(e.getMessage());
        }
    }

    public static List<Customer> getCustomers() {
        List<Customer> customersList = new ArrayList<>();

        try(Connection connection = connectToDatabase()){

            String sqlQuery = "SELECT * FROM CUSTOMERS";
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                Customer newCustomer = customer(rs);
                customersList.add(newCustomer);
            }
            statement.close();

        }catch (SQLException | IOException e){
            logger.error("Greska u dohvacanju korisnika iz baze podataka");
            System.out.println(e.getMessage());
        }

        return customersList;
    }

    public static Customer getCurrentUser(String username){

        try(Connection connection = connectToDatabase()) {

            String sqlQuery = "SELECT * FROM CUSTOMERS WHERE USERNAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Customer customer = customer(rs);
                    return customer;
                }
            }
            preparedStatement.close();
        }
        catch (IOException | SQLException e){
            logger.error("Greska u dohvacanju trenutnog korisnika iz baze");
        }
        return null;
    }

    public static void updateCustomerStatus(Customer customer, String status) {
        try (Connection connection = connectToDatabase()){

            String sqlQuery = "UPDATE CUSTOMERS SET STATUS = ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

                preparedStatement.setString(1, status);
                preparedStatement.setInt(2, customer.getID());

                preparedStatement.executeUpdate();

                System.out.println("User updated successfully.");
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateCustomerProfile(Customer updatedCustomer, Integer id) {
        try(Connection connection = connectToDatabase()){
            String sqlQuery = "UPDATE CUSTOMERS SET NAME = ?, ADDRESS = ?, GENDER = ?, PHONE_NUM = ? WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, updatedCustomer.getName());
            preparedStatement.setString(2, updatedCustomer.getAddress());
            preparedStatement.setString(3, updatedCustomer.getGender());
            preparedStatement.setInt(4, updatedCustomer.getPhoneNum());
            preparedStatement.setInt(5, id);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException | IOException e){
            logger.error("Greska u azuriranju podataka o korisniku u bazi!");
            System.out.println(e.getMessage());
        }
    }

    public static List<Customer> getPaidCustomers(){
        List<Customer> customerList = new ArrayList<>();
        try(Connection connection = connectToDatabase()) {

            String sqlQuery = "SELECT * FROM CUSTOMERS WHERE STATUS = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, PaidStatus.PAID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Customer customer = customer(resultSet);
                    customerList.add(customer);
                }
            }
        }
        catch (IOException | SQLException e){
            System.out.println(e.getMessage());
            logger.error("Greska u dohvacanju trenutnog korisnika iz baze");
        }
        return customerList;
    }

    public static void displayChart(AreaChart<String, Integer> chart){
        chart.getData().clear();
        String sqlQuery = "SELECT START_DATE, SUM(PRICE) FROM CUSTOMERS WHERE STATUS = 'PAID' GROUP BY START_DATE ORDER BY START_DATE ASC LIMIT 10";

        try(Connection connection = connectToDatabase()){
            XYChart.Series<String, Integer> chart2 = new XYChart.Series<>();
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                try(ResultSet rs = preparedStatement.executeQuery()){

                    while(rs.next()){
                        chart2.getData().add(new XYChart.Data<>(rs.getString(1), rs.getInt(2)));
                    }
                    chart.getData().add(chart2);
                    }
                catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

            }catch (Exception e){
                System.out.println(e.getMessage());
                logger.error(e.getMessage());
            }
        }catch(SQLException | IOException e){
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private static Customer customer(ResultSet rs) throws SQLException {
        return new CustomerBuilder()
                .setID(rs.getInt("ID"))
                .setUsername(rs.getString("USERNAME"))
                .setPassword(rs.getString("PASSWORD"))
                .setName(rs.getString("NAME"))
                .setAddress(rs.getString("ADDRESS"))
                .setGender(rs.getString("GENDER"))
                .setSchedule(rs.getString("SCHEDULE"))
                .setTrainerID(rs.getInt("TRAINER_ID"))
                .setGroup(rs.getString("GROUP_NAME"))
                .setPhoneNum(rs.getInt("PHONE_NUM"))
                .setStartDate(rs.getDate("START_DATE").toLocalDate())
                .setEndDate((rs.getDate("END_DATE")).toLocalDate())
                .setPrice(rs.getLong("PRICE"))
                .setStatus(rs.getString("STATUS"))
                .createCustomer();
    }
}
