package hr.java.application.threads;

import hr.java.application.model.Customer;

public class UpdateCustomerProfileThread extends DatabaseThread implements Runnable{

    private final Customer customer;
    private final Integer id;

    public UpdateCustomerProfileThread(Customer customer, Integer id){
        this.customer = customer;
        this.id = id;
    }

    @Override
    public void run() {
        super.updateCustomerProfile(customer, id);
    }
}
