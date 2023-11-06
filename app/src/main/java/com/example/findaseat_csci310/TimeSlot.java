package com.example.findaseat_csci310;

import java.sql.Time;

public class TimeSlot {
    public String time;
    public String type; // Indoor or Outdoor
    public int availableSeats;

    public TimeSlot(String time, String type, int availableSeats) {
        this.time = time;
        this.type = type;
        this.availableSeats = availableSeats;
    }

    public TimeSlot() {
        this.time = null;
        this.type = null;
        this.availableSeats = 0;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
