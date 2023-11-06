package com.example.findaseat_csci310;

import java.util.List;
import java.util.Vector;

public class Building {
    public double lat;
    public double lng;
    public String name;
    public Vector<Integer> indoor_avail = new Vector<Integer>(26);
    public Vector<Integer> outdoor_avail = new Vector<Integer>(26);

    Building(String name, double lat, double lng, Vector<Integer> indoor_avail, Vector<Integer> outdoor_avail) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.indoor_avail = indoor_avail;
        this.outdoor_avail = outdoor_avail;
    }

    public List<TimeSlot> getTimeSlots() {
        List<TimeSlot> timeSlots = new Vector<TimeSlot>(52);
        // add 26 indoor time slots to the list
        for (int i=0; i<26; i++){
            timeSlots.add(new TimeSlot(String.valueOf(i+8)+":00-"+String.valueOf(i+8)+":29", "Indoor", indoor_avail.get(i)));
        }
        // add 26 outdoor time slots to the list
        for (int i=0; i<26; i++){
            timeSlots.add(new TimeSlot(String.valueOf(i+8)+":30-"+String.valueOf(i+8)+":59", "Outdoor", outdoor_avail.get(i)));
        }
        return timeSlots;
    };
}
