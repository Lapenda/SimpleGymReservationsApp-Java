package hr.java.application.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Customer implements Comparable<Customer>{
    String username;
    String password;
    String name;
    String address;
    String gender;
    String schedule;
    String group;
    String status;
    Integer ID, phoneNum, trainerID;
    LocalDate startDate, endDate;
    Long price;

    public static Map<String, String> map = new HashMap<>();

    Integer hourlyRate = 10;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }


    public Customer(String username, String password, String name, String address, String gender, String schedule, String group, String status, Integer ID, Integer phoneNum, Integer trainerID, LocalDate startDate, LocalDate endDate, Long price) {

        this.username = username;
        this.password = password;
        this.name = Objects.requireNonNullElse(name, "Unknown");
        this.address = Objects.requireNonNullElse(address, "Unknown");
        this.gender = Objects.requireNonNullElse(gender, "OTHER");
        this.schedule = Objects.requireNonNullElse(schedule, "Unknown");
        this.group = Objects.requireNonNullElse(group, "false");
        this.status = Objects.requireNonNullElse(status, "UNPAID");
        this.ID = ID;
        this.phoneNum = Objects.requireNonNullElse(phoneNum, 0);
        this.trainerID = Objects.requireNonNullElse(trainerID, 100);
        this.startDate = Objects.requireNonNullElse(startDate, LocalDate.now());
        this.endDate = Objects.requireNonNullElse(endDate, LocalDate.now().plusDays(1));
        this.price = 2L * hourlyRate * (int) ChronoUnit.DAYS.between(this.startDate, this.endDate);
        map.put(username, password);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Integer getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(Integer trainerID) {
        this.trainerID = trainerID;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getID() { return ID; }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(Integer phoneNum) {
        this.phoneNum = phoneNum;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(Customer other) {
        return this.getName().compareTo(other.getName());
    }

    @Override
    public String toString() {
        return "username=" + username;
    }
}