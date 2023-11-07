package com.example.findaseat_csci310;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Building {
    public double lat;
    public double lng;
    public String name;
    public ArrayList<Integer> indoor_avail = new ArrayList<Integer>(26);
    public ArrayList<Integer> outdoor_avail = new ArrayList<Integer>(26);
    public List<TimeSlot> timeSlots;

    Building(String name, double lat, double lng, ArrayList<Integer> indoor_avail, ArrayList<Integer> outdoor_avail) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.indoor_avail = indoor_avail;
        this.outdoor_avail = outdoor_avail;
    }

    Building() {
        this.lat = 0.0;
        this.lng = 0.0;
        this.name = "Doheny Memorial Library (DML)";
        for (int i=0; i<26; i++){
            this.indoor_avail.add(10);
            this.outdoor_avail.add(10);
        }
    }

    public List<TimeSlot> getTimeSlots() {
        List<TimeSlot> timeSlots = new Vector<TimeSlot>(52);
        // add 26 indoor time slots to the list
        for (int i=0; i<26; i++){
            timeSlots.add(new TimeSlot(String.valueOf(i+8)+":00-"+String.valueOf(i+8)+":29", "Indoor", indoor_avail.get(i), i));
        }
        // add 26 outdoor time slots to the list
        for (int i=0; i<26; i++){
            timeSlots.add(new TimeSlot(String.valueOf(i+8)+":30-"+String.valueOf(i+8)+":59", "Outdoor", outdoor_avail.get(i), i+26));
        }
        this.timeSlots = timeSlots;
        return timeSlots;
    };
}
