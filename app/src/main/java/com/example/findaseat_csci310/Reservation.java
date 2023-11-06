package com.example.findaseat_csci310;

import java.util.Vector;

public class Reservation {
    public String building_name;
    public int start_time;
    public int end_time;
    public String in_out;

    Reservation() {
        building_name = null;
        start_time = 0;
        end_time = 0;
        in_out = null;
    }
    Reservation(String b, int s, int e, String io) {
        building_name = b;
        start_time = s;
        end_time = e;
        in_out = io;
    }

    public String getTime() {
        String start = Integer.toString(start_time/2+8);
        String end = Integer.toString(end_time/2+8);
        if (start_time%2 == 1) {
            start += ":30";
        } else {
            start += ":00";
        }

        if (end_time%2 == 1) {
            end += ":59";
        } else {
            end += ":29";
        }
        return start + " - " + end;
    }

    public String getBuilding() {
        return building_name;
    }
    public String getInOut() {
        return in_out;
    }

}
