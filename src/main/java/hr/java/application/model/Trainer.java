package hr.java.application.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Trainer  implements Comparable<Trainer> {

    String name, address, status, gender;
    Integer phoneNum, ID;

    public static Set<Integer> ids = new HashSet<>();

    public Trainer(Integer ID, String name, String address, String gender, Integer phoneNum, String status) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.gender = Objects.requireNonNullElse(gender, "OTHER");;
        this.phoneNum = phoneNum;
        this.status =  Objects.requireNonNullElse(status, "INACTIVE");
        ids.add(ID);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(Integer phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    @Override
    public String toString(){
        return getName() + " (ID: " + getID() + ")";
    }

    @Override
    public int compareTo(Trainer other) {
        return this.getName().compareTo(other.getName());
    }
}
