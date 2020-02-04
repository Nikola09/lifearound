package com.example.lifearound.data.model;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

public class Notification {
    public String message;
    public LocalTime time;

    public Notification(){}

    public Notification(String msg)
    {
        message=msg;
        time=LocalTime.now();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public LocalTime getTime() {
        return time;
    }
    public String getTimeStr() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }
}
