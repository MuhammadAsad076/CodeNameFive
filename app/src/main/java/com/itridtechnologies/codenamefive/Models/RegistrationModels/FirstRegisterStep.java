package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import android.net.Uri;

public final class FirstRegisterStep {

    private static String firstName;
    private static String lastName;
    private static String email;
    private static String password;
    private static String phone;
    private static String vehicleNum;
    private static String vehicleId;
    private static Uri imageUri;

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        FirstRegisterStep.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        FirstRegisterStep.lastName = lastName;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        FirstRegisterStep.email = email;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        FirstRegisterStep.password = password;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        FirstRegisterStep.phone = phone;
    }

    public static String getVehicleNum() {
        return vehicleNum;
    }

    public static void setVehicleNum(String vehicleNum) {
        if (!vehicleNum.isEmpty()) {
            FirstRegisterStep.vehicleNum = vehicleNum;
        } else {
            FirstRegisterStep.vehicleNum = "null";
        }
    }

    public static Uri getImageUri() {
        return imageUri;
    }

    public static void setImageUri(Uri imageUri) {
        FirstRegisterStep.imageUri = imageUri;
    }

    public static String getVehicleId() {
        return vehicleId;
    }

    public static void setVehicleId(String vehicleId) {
        FirstRegisterStep.vehicleId = vehicleId;
    }

}//end class
