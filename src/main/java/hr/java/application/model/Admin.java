package hr.java.application.model;

public class Admin {

    String username, password;

    public Admin(String name, String password){
        this.username = name;
        this.password = password;
    }

    public Admin(String name){
        this.username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "username='" + username + '\'' +
                '}';
    }
}