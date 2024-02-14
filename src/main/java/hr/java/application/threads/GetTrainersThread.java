package hr.java.application.threads;

public class GetTrainersThread extends DatabaseThread implements Runnable{

    @Override
    public void run() {
        super.getTrainers();
    }
}
