package com.example.findaseat_csci310;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.Locale;
import java.util.Vector;
import java.util.Calendar;

public class BuildingActivity extends Activity {

    public FirebaseDatabase root;
    public DatabaseReference reference;
    public String buildingName = "Doheny Memorial Library (DML)";
    public Vector<TimeSlot> timeSlots = new Vector<TimeSlot>();
    public Context context = this;
    public BottomNavigationView bottomNavigationView;
    public String usrID;
    // list of selected time slots
    public Vector<TimeSlot> selectedTimeSlots = new Vector<TimeSlot>();
    // list of all itemViews
    public Vector<View> itemViews = new Vector<View>();
    // to check if there's active reservation
    public boolean flag = true;
    // user object
    public User user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        // get from intent
        Intent intent = getIntent();
        // get user id
        usrID = intent.getStringExtra("id");

        // for blackbox testing
        if (usrID == null) {
            usrID = "2000000001";
        }

        // get building name
        buildingName = intent.getStringExtra("building_name");
        Log.d("intent", "building name: " + buildingName);

        // for blackbox testing
        if (buildingName == null) {
            buildingName = "Kaprielian Hall (KAP)";
        }

        // load data from firebase database
//        load_time_slots();

        root = FirebaseDatabase.getInstance();
        reference = root.getReference();

        // check for active reservation
        reference.child("Users").child(usrID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    user = task.getResult().getValue(User.class);
                    if (user.currentReservation != null) {
                        Log.d("Debug", "currentReservation: " + user.currentReservation);
                        flag = false;
                    }
                }
            }
        });

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


                    // store all the itemViews in a vector
                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        itemViews.add(recyclerView.getChildAt(i));
                    }

                    Log.d("Debug", "recyclerView size: " + recyclerView.getChildCount());
                    Log.d("Debug", "itemViews size: " + itemViews.size());


                    // set onClickListener for each time slot item
                    timeSlotAdapter.setOnClickListener(new TimeSlotAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position, TimeSlot item) {
                            // Use getAdapterPosition to get the current position
                            int adapterPosition = position;
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                TimeSlot timeSlot = timeSlotAdapter.timeSlotList.get(adapterPosition);
                                // track if it is selected
                                boolean isSelected = selectedTimeSlots.contains(timeSlot);
                                // get the view of the item --> for changing background color
                                View itemView = null;
                                if (adapterPosition < itemViews.size())
                                {
                                    itemView = itemViews.get(adapterPosition);
                                }
                                // if the time slot is selected, remove it from the list and reset background color
                                if (isSelected) {
                                    selectedTimeSlots.remove(timeSlot);
                                    // TODO: reset background color to default
                                    if (itemView != null) {
                                        // set background color to default
                                        itemView.setBackgroundColor(getResources().getColor(R.color.white));
                                    }
                                    // for debug purpose
                                    Log.d("Debug", "time slot deselected: " + timeSlot.getTime());
                                } else {
                                    selectedTimeSlots.add(timeSlot);
                                    // TODO: set background color to blue
                                    if (itemView != null) {
                                        // set background color to lavender
                                        itemView.setBackgroundColor(getResources().getColor(R.color.lavender));
                                    }
                                    // for debug purpose
                                    Log.d("Debug", "time slot selected: " + timeSlot.getTime());
                                }
                                timeSlotAdapter.notifyItemChanged(adapterPosition);
                            }
                        }
                    });

                    // set onClickListener for the "Book" button
                    findViewById(R.id.book_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // check if the selected time slots are valid
                            if (isValid() == 0) {
                                Log.d("Debug", "Reservation Valid!");
                                // if valid, modify the building object and push to database
                                for (int i = 0; i < selectedTimeSlots.size(); i++) {
                                    TimeSlot timeSlot = selectedTimeSlots.get(i);
                                    // update the available seats
                                    timeSlot.setAvailableSeats(timeSlot.getAvailableSeats()-1);
                                    // update the building object accordingly
                                    if (timeSlot.getType().equals("Indoor")) {
                                        building.indoor_avail.set(timeSlot.getIndex(), timeSlot.getAvailableSeats());
                                    } else { // if (timeSlot.getType().equals("Outdoor"))
                                        building.outdoor_avail.set(timeSlot.getIndex()-26, timeSlot.getAvailableSeats());
                                    }
                                }

                                // push the modified building object to the database
                                reference.child("Buildings").child(buildingName).setValue(building);

                                // update user
                                selectedTimeSlots.sort((o1, o2) -> o1.getIndex() - o2.getIndex());
                                int start_time = selectedTimeSlots.get(0).getIndex();
                                int end_time = selectedTimeSlots.get(selectedTimeSlots.size()-1).getIndex();
                                String in_out = selectedTimeSlots.get(0).getType().toLowerCase(Locale.ROOT);
                                // update user's current reservation
                                Reservation reservation = new Reservation(buildingName, start_time, end_time, in_out);
                                user.currentReservation = reservation;
                                reference.child("Users").child(usrID).setValue(user);

                                // after update, go to the next page
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                // pass isLogin and id to the map view (hopefully)
                                intent.putExtra("isLogin", true);
                                intent.putExtra("id", usrID);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                Log.d("Debug", "Reservation Invalid!");
                                // if not valid, show error message
                                if (isValid() == 1) {
                                    Toast.makeText(getApplicationContext(), "No Time Slot Selected!", Toast.LENGTH_SHORT).show();
                                } else if (isValid() == 2) {
                                    Toast.makeText(getApplicationContext(), "No Available Seats!", Toast.LENGTH_SHORT).show();
                                } else if (isValid() == 3) {
                                    Toast.makeText(getApplicationContext(), "Time Slots Not Consecutive!", Toast.LENGTH_SHORT).show();
                                } else if (isValid() == 4) {
                                    Toast.makeText(getApplicationContext(), "Time Slots Not of the Same Type!", Toast.LENGTH_SHORT).show();
                                } else if (isValid() == 5) {
                                    Toast.makeText(getApplicationContext(), "Too Many Time Slots Selected!", Toast.LENGTH_SHORT).show();
                                } else if (isValid() == 6) {
                                    Toast.makeText(getApplicationContext(), "You Have an Active Reservation!", Toast.LENGTH_SHORT).show();
                                } else if (isValid() == 7) {
                                    Toast.makeText(getApplicationContext(), "Selected Time Slots Are in the Past!", Toast.LENGTH_SHORT).show();
                                }
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
            } else if (item.getItemId() == R.id.bottom_logout) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to log out?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, id1) -> {
                            dialog.dismiss();
                            Intent intent3 = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent3);
                        })
                        .setNegativeButton("No", (dialog, id1) -> {
                            dialog.dismiss();
                        });
                AlertDialog alert = builder.create();
                alert.show();
//                finish();
                return true;
            }
            return false;
        });
    }

//    void load_time_slots() {
//        // read time slots from firebase database
//        root = FirebaseDatabase.getInstance();
//        reference = root.getReference();
//        reference.child("Buildings").child(buildingName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                Building building = task.getResult().getValue(Building.class);
//                timeSlots = (Vector<TimeSlot>) building.getTimeSlots();
//            }
//        });
//    }

    public int isValid() {
        // if array empty, return false
        if(isSelectedEmpty()) {
            return 1;
        }

        if (!isSelectedAvailable()) {
            return 2;
        }

        if (!isSelectedConsecutive()) {
            return 3;
        }

        if (!isSelectedSameType()) {
            return 4;
        }

        if (!isSelectedLessThan4()) {
            return 5;
        }

        // if active reservation, return false
        if (!flag) {
            return 6;
        }

        // if current time is earlier than selected time, return false
        if (!isCurrentTimeEarlier()) {
            return 7;
        }

        // otherwise valid
        return 0;
    }

    // check if no time slots are selected
    public boolean isSelectedEmpty() {
        return selectedTimeSlots.isEmpty();
    }

    // check if selected time slots are available
    public boolean isSelectedAvailable() {
        for (int i = 0; i < selectedTimeSlots.size(); i++) {
            TimeSlot timeSlot = selectedTimeSlots.get(i);
            if (timeSlot.getAvailableSeats() <= 0) {
                return false;
            }
        }
        return true;
    }

    // check if selected time slots are consecutive
    public boolean isSelectedConsecutive() {
        // sort the selected time slots by index
        selectedTimeSlots.sort((o1, o2) -> o1.getIndex() - o2.getIndex());

        // check if the time slots are consecutive
        for (int i = 0; i < selectedTimeSlots.size(); i++) {
            TimeSlot timeSlot = selectedTimeSlots.get(i);
            if (i != 0) {
                if (timeSlot.getIndex() != selectedTimeSlots.get(i-1).getIndex()+1) {
                    return false;
                }
//                if (timeSlot.getIndex() == 26) {
//                    return false;
//                }
            }
        }
        return true;
    }

    // check if selected time slots are of the same type
    public boolean isSelectedSameType() {
        // track types
        boolean indoor = false;
        boolean outdoor = false;

        for (int i = 0; i < selectedTimeSlots.size(); i++) {
            TimeSlot timeSlot = selectedTimeSlots.get(i);
            if (timeSlot.getType().equals("Indoor")) {
                indoor = true;
            } else {
                outdoor = true;
            }
        }

        // if both types present, return false
        if (indoor && outdoor) {
            return false;
        }

        return true;
    }

    // check if selected time slots are less than 4
    public boolean isSelectedLessThan4() {
        return selectedTimeSlots.size() <= 4;
    }

    public boolean isActiveReservation() {
        return user.currentReservation != null;
    }

    public boolean isCurrentTimeEarlier() {
        // Get the current hour and minute
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);

        // Convert current time to 30-min time slot
        int current_slot = (hour-8)*2;
        if (minute >= 30) {
            current_slot += 1;
        }

        // Check if any slot before current_slot is chosen
        for (TimeSlot slot : selectedTimeSlots) {
            if (slot.getIndex() < current_slot) {
                return false;
            }
        }

        return true;
    }

}
