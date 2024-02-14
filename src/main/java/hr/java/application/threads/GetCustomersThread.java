package hr.java.application.threads;

public class GetCustomersThread extends DatabaseThread implements Runnable{

    @Override
    public void run() {
        super.getCustomers();
    }
}
