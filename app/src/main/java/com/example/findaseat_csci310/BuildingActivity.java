package com.example.findaseat_csci310;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Vector;

public class BuildingActivity extends AppCompatActivity {

    public FirebaseDatabase root;
    public DatabaseReference reference;
    public String buildingName = "Doheny Memorial Library (DML)";
    public Vector<TimeSlot> timeSlots = new Vector<TimeSlot>();
    public Context context = this;
    public BottomNavigationView bottomNavigationView;
    public String usrID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        // get from intent
        Intent intent = getIntent();
        // get user id
        usrID = intent.getStringExtra("id");
        // get building name
        buildingName = intent.getStringExtra("building_name");
        Log.d("intent", "building name: " + buildingName);


        // load data from firebase database
//        load_time_slots();

        root = FirebaseDatabase.getInstance();
        reference = root.getReference();

        Log.d("Debug", "building name: " + buildingName);
        reference.child("Buildings").child(buildingName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    Building building = task.getResult().getValue(Building.class);

                    // setting building info
                    TextView buildingNameTextView = findViewById(R.id.building_name);
                    TextView buildingAbbreviationTextView = findViewById(R.id.building_name_short);
                    // parse building name into name and abbreviation, split by "("
                    String[] buildingNameSplit = building.name.split("\\(");
                    buildingNameTextView.setText("Building Name: " + buildingNameSplit[0]);
                    buildingAbbreviationTextView.setText("Building Name Abbreviation: " + buildingNameSplit[1].substring(0, buildingNameSplit[1].length()-1));
                    // set address as (lat, lng)
                    TextView buildingAddressTextView = findViewById(R.id.building_address);
                    buildingAddressTextView.setText("(" + building.lat + ", " + building.lng + ")");


                    // setting booking info
                    timeSlots = (Vector<TimeSlot>) building.getTimeSlots();
                    // create a recycler view
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewTimeSlots);
                    // set layout
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    // create a time slot adapter
                    TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(context, timeSlots);
                    // assign adapter to the recycler view
                    recyclerView.setAdapter(timeSlotAdapter);
                    // set onClickListener for each time slot item
                    timeSlotAdapter.setOnClickListener(new TimeSlotAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position, TimeSlot item) {
                            // Use getAdapterPosition to get the current position
                            int adapterPosition = position;
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                // TODO: select up to 4 consecutive time slots

                            }
                        }
                    });
                }
                else
                {
                    Log.e("Debug", "Error fetching data", task.getException());
                }

            }
        });


        // for navigation bar
        bottomNavigationView = findViewById(R.id.Navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_reservations);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_reservations) {
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                Intent intent1 = new Intent(getApplicationContext(), UserActivity.class);
                intent1.putExtra("id", usrID);
                Log.d("intent", "usrID given out: " + usrID);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_map) {
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                intent2.putExtra("id", usrID);
                intent2.putExtra("isLogin",true);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }

    void load_time_slots() {
        // read time slots from firebase database
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Buildings").child(buildingName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Building building = task.getResult().getValue(Building.class);
//                timeSlots = (Vector<TimeSlot>) building.getTimeSlots();

                // for debug purpose
//                Log.d("Debug", "hello there!" + timeSlots.size());
//                for (TimeSlot ts : timeSlots)
//                {
//                    Log.d("Debug", ts.getTime());
//                }
            }
        });
    }

}
