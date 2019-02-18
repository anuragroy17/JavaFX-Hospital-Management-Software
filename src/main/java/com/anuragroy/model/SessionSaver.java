package com.anuragroy.model;


//THIS CLASS SAVES CERTAIN DETAILS OF A LOGIN SESSION

public class SessionSaver {
    private static String username = "";
    private static String department = "";


    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        SessionSaver.username = username;
    }

    public static String getDepartment() {
        return department;
    }

    public static void setDepartment(String department) {
        SessionSaver.department = department;
    }
}
