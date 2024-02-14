package hr.java.application.threads;

public class GetCurrentUserThread extends DatabaseThread implements Runnable{

    private final String username;

    public GetCurrentUserThread(String username){ this.username = username; }

    @Override
    public void run() {
        super.getCurrentUser(username);
    }

}
