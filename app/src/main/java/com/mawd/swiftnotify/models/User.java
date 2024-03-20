package com.mawd.swiftnotify.models;

public class User {
    private String fullName;
    private int age;
    private String userGender;
    private String userStatus;
    private String userEmail;
    private boolean isAvailable;

    public User() {
        //Default constructor
    }

    public User(String fullName, int age, String userGender, String userStatus, String userEmail) {
        this.fullName = fullName;
        this.age = age;
        this.userGender = userGender;
        this.userStatus = userStatus;
        this.userEmail = userEmail;
    }

    public User(String fullName, int age, String userGender, String userStatus, String userEmail, boolean isAvailable) {
        this.fullName = fullName;
        this.age = age;
        this.userGender = userGender;
        this.userStatus = userStatus;
        this.userEmail = userEmail;
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}
