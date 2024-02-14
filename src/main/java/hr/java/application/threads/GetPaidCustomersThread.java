package hr.java.application.threads;

public class GetPaidCustomersThread extends DatabaseThread implements Runnable{

    @Override
    public void run() {
        super.getPaidCustomers();
    }
}
