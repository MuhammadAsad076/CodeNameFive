package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import org.jetbrains.annotations.NotNull;

public class FirstRegisterStep {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String vehicleNum;
    private String vehicleId;
    private String imagePath;

    public FirstRegisterStep(String firstName, String lastName, String email, String password,
                             String phone, String vehicleNum, String vehicleId, String imagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.vehicleNum = vehicleNum;
        this.vehicleId = vehicleId;
        this.imagePath = imagePath;
    }

    public FirstRegisterStep(String firstName, String lastName, String email, String password,
                             String phone, String vehicleId, String imagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.vehicleId = vehicleId;
        this.imagePath = imagePath;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getVehicleNum() {
        return vehicleNum;
    }

    public String getImagePath() {
        return imagePath;
    }

    private String getVehicleId() {
        return vehicleId;
    }

    @NotNull
    @Override
    public String toString() {
        return "FirstRegisterStep{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", vehicleNum='" + vehicleNum + '\'' +
                ", vehicleType='" + vehicleId + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}//end class
