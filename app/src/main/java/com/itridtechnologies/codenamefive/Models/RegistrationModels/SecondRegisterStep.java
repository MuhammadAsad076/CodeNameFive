package com.itridtechnologies.codenamefive.Models.RegistrationModels;

import org.jetbrains.annotations.NotNull;

public class SecondRegisterStep {

    private String dateOfBirth;
    private String address1;
    private String getAddress2;
    private String country;
    private String state;
    private String city;
    private String zipCode;

    public SecondRegisterStep(String dateOfBirth, String address1, String getAddress2, String country, String state, String city, String zipCode) {
        this.dateOfBirth = dateOfBirth;
        this.address1 = address1;
        this.getAddress2 = getAddress2;
        this.country = country;
        this.state = state;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress1() {
        return address1;
    }

    public String getGetAddress2() {
        return getAddress2;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    @NotNull
    @Override
    public String toString() {
        return "SecondRegisterStep{" +
                "dateOfBirth='" + dateOfBirth + '\'' +
                ", address1='" + address1 + '\'' +
                ", getAddress2='" + getAddress2 + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}//end class
