package com.mawd.swiftnotify.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationInfoExtractor {

    public static NotificationInfo extractInfoFromQR(String qrResult) {
        String[] lines = qrResult.split("\n");
        String name = "";
        int age = 0;
        String gender = "";
        String section = "";
        String account = "";

        for (String line : lines) {
            if (line.startsWith("Name:")) {
                name = line.substring(6).trim();
            } else if (line.startsWith("Age:")) {
                age = Integer.parseInt(line.substring(5).trim());
            } else if (line.startsWith("Gender:")) {
                gender = line.substring(8).trim();
            } else if (line.startsWith("Section:")) {
                section = line.substring(9).trim();
            } else if (line.startsWith("Account:")) {
                account = line.substring(9).trim();
            }
        }
        String timeStamp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
            LocalDateTime now = LocalDateTime.now();
            timeStamp = dtf.format(now);
        }

        return new NotificationInfo(name, age, gender, section, account, timeStamp);
    }
}
