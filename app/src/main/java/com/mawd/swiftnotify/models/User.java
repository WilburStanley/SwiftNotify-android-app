package com.mawd.swiftnotify.models;

public class User {
    private String fullName;
    private int age;
    private String userGender;
    private String userStatus;
    private String userEmail;
    private String deviceToken;

    private String personalQrCodeImage;
    private boolean teacherAvailable;
    private boolean verified;
    private int simNumber;

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

    public User(String fullName, int age, String userGender, String userStatus, String email, boolean teacherAvailable, int simNumber) {
        this.fullName = fullName;
        this.age = age;
        this.userGender = userGender;
        this.userStatus = userStatus;
        this.userEmail = email;
        this.teacherAvailable = teacherAvailable;
        this.simNumber = simNumber;
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

    public boolean isTeacherAvailable() {
        return teacherAvailable;
    }
    public void setIsTeacherAvailable(boolean teacherAvailable) {
        this.teacherAvailable = teacherAvailable;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getPersonalQrCodeImage() {
        return personalQrCodeImage;
    }

    public void setPersonalQrCodeImage(String personalQrCodeImage) {
        this.personalQrCodeImage = personalQrCodeImage;
    }
    public int getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(int simNumber) {
        this.simNumber = simNumber;
    }
}
