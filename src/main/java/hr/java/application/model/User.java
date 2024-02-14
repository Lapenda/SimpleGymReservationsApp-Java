package hr.java.application.model;

public class User<T> {

    private final T role;

    public User(T role) {
        this.role = role;
    }

    public T getRole() {
        return role;
    }

}