package hr.java.application.model;

import java.time.LocalDate;

public class CustomerBuilder {
    private String username;
    private String password;
    private String name;
    private String address;
    private String gender;
    private String schedule;
    private String group;
    private String status;
    private Integer id;
    private Integer phoneNum;
    private Integer trainerID;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;

    public CustomerBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public CustomerBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public CustomerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public CustomerBuilder setSchedule(String schedule) {
        this.schedule = schedule;
        return this;
    }

    public CustomerBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public CustomerBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public CustomerBuilder setID(Integer id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder setPhoneNum(Integer phoneNum) {
        this.phoneNum = phoneNum;
        return this;
    }

    public CustomerBuilder setTrainerID(Integer trainerID) {
        this.trainerID = trainerID;
        return this;
    }

    public CustomerBuilder setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public CustomerBuilder setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public CustomerBuilder setPrice(Long price) {
        this.price = price;
        return this;
    }

    public CustomerBuilder withExistingValues(Customer existingCustomer) {
        this.name = existingCustomer.getName();
        this.address = existingCustomer.getAddress();
        this.gender = existingCustomer.getGender();
        this.phoneNum = existingCustomer.getPhoneNum();
        this.trainerID = existingCustomer.getTrainerID();
        this.schedule = existingCustomer.getSchedule();
        this.startDate = existingCustomer.getStartDate();
        this.endDate = existingCustomer.getEndDate();
        this.group = existingCustomer.getGroup();
        this.status = existingCustomer.getStatus();
        return this;
    }

    public CustomerBuilder withExistingUsPass(Customer existingCustomer) {
        this.username = existingCustomer.getUsername();
        this.password = existingCustomer.getPassword();
        return this;
    }

    public Customer createCustomer() {
        return new Customer(username, password, name, address, gender, schedule, group, status, id, phoneNum, trainerID, startDate, endDate, price);
    }
}