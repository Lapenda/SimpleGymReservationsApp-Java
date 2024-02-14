package hr.java.application.threads;

import hr.java.application.model.User;

public class AddUserThread extends DatabaseThread implements Runnable {

    private final User<?> user;

    public AddUserThread(User<?> user){
        this.user = user;
    }

    @Override
    public void run() {
        super.addUser(user);
    }
}
