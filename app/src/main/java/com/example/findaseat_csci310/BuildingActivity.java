package com.example.findaseat_csci310;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Vector;

public class BuildingActivity extends AppCompatActivity {

    public FirebaseDatabase root;
    public DatabaseReference reference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTimeSlots); // create a recycler view
        Vector<TimeSlot> timeSlots = new Vector<TimeSlot>();

        // some dummy inputs
        timeSlots.add(new TimeSlot("9:00 AM", "In", 10));
        timeSlots.add(new TimeSlot("10:00 AM", "In", 10));
        timeSlots.add(new TimeSlot("11:00 AM", "In", 10));
        timeSlots.add(new TimeSlot("12:00 AM", "In", 10));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // create a time slot adapter
        TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(this, timeSlots);
        recyclerView.setAdapter(timeSlotAdapter);
    }

    void load_time_slots() {
        // read time slots from firebase database

    }

}
