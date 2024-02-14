package hr.java.application.threads;

import hr.java.application.model.Customer;

public class DeleteCustomerThread extends DatabaseThread implements Runnable{

    private final Customer customer;

    public DeleteCustomerThread(Customer customer){ this.customer = customer; }

    @Override
    public void run() {
        super.deleteCustomer(customer);
    }
}
