package hr.java.application.model;

public class TrainerBuilder {
    private String name;
    private String address;
    private String status;
    private Integer phoneNum;
    private String gender;
    private Integer id;

    public TrainerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TrainerBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public TrainerBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public TrainerBuilder setPhoneNum(Integer phoneNum) {
        this.phoneNum = phoneNum;
        return this;
    }

    public TrainerBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public TrainerBuilder setID(Integer id) {
        this.id = id;
        return this;
    }

    public Trainer createTrainer() {
        return new Trainer(id, name, address, gender, phoneNum, status);
    }
}