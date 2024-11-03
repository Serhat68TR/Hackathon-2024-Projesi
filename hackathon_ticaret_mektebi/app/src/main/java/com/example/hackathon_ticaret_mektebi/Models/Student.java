package com.example.hackathon_ticaret_mektebi.Models;

public class Student {
    private String nameSurname;
    private String eMailAddress;
    private String eMailPassword;
    private String userDepartment;
    private String profilePictureURL;

    public Student() {
        // Default constructor
    }

    public Student(String nameSurname, String eMailAddress, String eMailPassword, String userDepartment, String profilePictureURL) {
        this.nameSurname = nameSurname;
        this.eMailAddress = eMailAddress;
        this.eMailPassword = eMailPassword;
        this.userDepartment = userDepartment;
        this.profilePictureURL = profilePictureURL;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String geteMailAddress() {
        return eMailAddress;
    }

    public void seteMailAddress(String eMailAddress) {
        this.eMailAddress = eMailAddress;
    }

    public String geteMailPassword() {
        return eMailPassword;
    }

    public void seteMailPassword(String eMailPassword) {
        this.eMailPassword = eMailPassword;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }
}
