package com.itridtechnologies.codenamefive.Models;

public class FirstRegisterStep {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String vehicleNum;
    private String vehicleType;
    private String imagePath;

    public FirstRegisterStep(String firstName, String lastName, String email, String password,
                             String phone, String vehicleNum, String vehicleType, String imagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.vehicleNum = vehicleNum;
        this.vehicleType = vehicleType;
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

    @Override
    public String toString() {
        return "FirstRegisterStep{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", vehicleNum='" + vehicleNum + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}//end class
