package com.mawd.swiftnotify.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class NotificationInfo {
    private String username;
    private int age;
    private String userGender;
    private String userSection;
    private String account;
    private String timeStamp;

    public NotificationInfo() {
        //Empty Constructor
    }

    public NotificationInfo(String username, int age, String userGender, String userSection, String account, String timeStamp) {
        this.username = username;
        this.age = age;
        this.userGender = userGender;
        this.userSection = userSection;
        this.account = account;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUserSection() {
        return userSection;
    }

    public void setUserSection(String userSection) {
        this.userSection = userSection;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        account = account;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("fullName", username);
        result.put("age", age);
        result.put("userGender", userGender);
        result.put("userSection", userSection);
        result.put("email", account);
        result.put("timeStamp", timeStamp);
        return result;
    }

}
