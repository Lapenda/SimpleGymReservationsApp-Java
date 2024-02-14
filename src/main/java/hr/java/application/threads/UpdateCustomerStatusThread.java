package hr.java.application.threads;

import hr.java.application.model.Customer;

public class UpdateCustomerStatusThread extends DatabaseThread implements Runnable{

    private final Customer customer;
    private final String status;

    public UpdateCustomerStatusThread(Customer customer, String status){
        this.customer = customer;
        this.status = status;
    }

    @Override
    public void run() {
        super.updateCustomerStatus(customer, status);
    }
}
