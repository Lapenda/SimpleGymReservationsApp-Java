package hr.java.application.controllers;

import hr.java.application.databaseUtils.Database;
import hr.java.application.enums.PaidStatus;
import hr.java.application.enums.TrainerStatus;
import hr.java.application.model.Customer;
import hr.java.application.model.Trainer;
import hr.java.application.threads.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DashboardViewController {

    @FXML
    private AreaChart<String, Integer> chart;

    @FXML
    private Label noMembers;

    @FXML
    private Label noTrainers;

    @FXML
    private Label revenue;

    public void initialize(){

        GetTrainersThread getTrainersThread = new GetTrainersThread();
        Platform.runLater(getTrainersThread);
        List<Trainer> trainerList = getTrainersThread.getTrainers();


        GetCustomersThread getCustomersThread = new GetCustomersThread();
        Platform.runLater(getCustomersThread);
        List<Customer> customerList = getCustomersThread.getCustomers();

        long numMembers = customerList.stream().filter(customer -> customer.getStatus().equals(PaidStatus.PAID.toString())).count();
        long numTrainers = trainerList.stream().filter(trainer -> trainer.getStatus().equals(TrainerStatus.ACTIVE.toString())).count();

        GetPaidCustomersThread getPaidCustomersThread = new GetPaidCustomersThread();
        Platform.runLater(getPaidCustomersThread);
        List<Customer> paidCustomerList = getPaidCustomersThread.getPaidCustomers();
        Long currentRevenue = paidCustomerList.stream().map(Customer::getPrice).reduce(0L, Long::sum);

        revenue.setText(currentRevenue.toString());
        noMembers.setText(Long.toString(numMembers));
        noTrainers.setText(Long.toString(numTrainers));

        //DisplayChartThread displayChartThread = new DisplayChartThread(chart);
        //Platform.runLater(displayChartThread);
        Database.displayChart(chart);
    }
}