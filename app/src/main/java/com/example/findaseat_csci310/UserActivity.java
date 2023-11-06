package com.example.findaseat_csci310;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UserActivity extends AppCompatActivity {
    public User user;

    public String usr_id;
    public Uri downloadUri;
    public FirebaseDatabase root;
    public DatabaseReference reference;
    private BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        usr_id = intent.getStringExtra("id");
        Log.d("intent", "usrID received: " + usr_id);
        setContentView(R.layout.user_profile);

        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Users").child(usr_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                } else {
                    if (String.valueOf(task.getResult().getValue()) != null) {
                        user = task.getResult().getValue(User.class);
                        load_info();
                    }
                }
            }
        });
        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars/"+usr_id+".jpg");
        // Get the download URL
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<android.net.Uri>() {
            @Override
            public void onComplete(@NonNull Task<android.net.Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    ImageView imageView = (ImageView) findViewById(R.id.avatar);
                    imageView.setImageURI(downloadUri);
                } else {
                    // Handle failures
                    Log.d("firebase", "Error getting avatar", task.getException());
                }
            }
        });

        // navigation bar selector
        bottomNavigationView = findViewById(R.id.Navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_profile) {
                return true;
            } else if (item.getItemId() == R.id.bottom_map) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                intent1.putExtra("id", usr_id);
                intent1.putExtra("isLogin", true);
                intent1.putExtra("username", user.getName());
                Log.d("intent", "usrID given out: " + usr_id);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_reservations) {
                Intent intent2 = new Intent(getApplicationContext(), BuildingActivity.class);
                intent2.putExtra("id", usr_id);
                intent2.putExtra("building_name", "Doheny Memorial Library (DML)");
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });


    }

    // TODO: cancel current reservation and put it into history
    public void onClickCancel(View view) {
        // Add current Reservation to the first element of history (need to shift previous reservations
        // to the right and set history[0] = currentReservation)

        // Set currentReservation = null
    }

    // TODO: change reservation time
    public void onClickManage(View view) {
        // create popup window

        // read availability from database (can just retrieve an Arraylist of indoor(outdorr)_avail)

        // display availability in the popup window

        // user can select a time slot and click "confirm" to change reservation time

        // if user clicks "cancel", close the popup window (can be written in a separate function)

        // if user clicks "confirm", check if the selected time slot is valid (can be written in a separate function)

        // if valid, write changes to database (profile and building), change display, and close the popup window

        // if not valid, display error message and do nothing

    }
    public void load_info() {
        // display user info card
        TextView name = (TextView) findViewById(R.id.name);
        name.setText("Hello, "+user.getName());
        TextView email = (TextView) findViewById(R.id.email);
        email.setText("Email: "+user.getEmail());
        TextView affiliation = (TextView) findViewById(R.id.affiliation);
        affiliation.setText("Affiliation: "+user.getAffiliation());
        TextView uscid = (TextView) findViewById(R.id.usc_id);
        uscid.setText("USC ID: "+usr_id);

        // display current reservation card
        if (user.currentReservation != null) {
            TextView current_reservation = (TextView) findViewById(R.id.activated_reserved_buildingname);
            current_reservation.setText(user.currentReservation.getBuilding());
            TextView current_reservation_time = (TextView) findViewById(R.id.activated_reserved_time);
            current_reservation_time.setText(user.currentReservation.getTime());
        } else {
            TextView current_reservation = (TextView) findViewById(R.id.activated_reserved_buildingname);
            current_reservation.setText("No Current Reservation");
            TextView current_reservation_time = (TextView) findViewById(R.id.activated_reserved_time);
            current_reservation_time.setText("");
        }

        // display past reservation card
        int history_size = 0;
        int cnt = 0;
        if (user.history != null) {
            history_size = user.history.size();
        }
        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name1);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time1);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name1);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time1);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name2);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time2);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name2);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time2);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name3);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time3);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name3);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time3);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name4);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time4);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name4);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time4);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name5);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time5);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name5);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time5);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name6);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time6);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name6);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time6);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name7);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time7);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name7);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time7);
            timeView.setText("");
        }
        cnt++;

        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name8);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time8);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name8);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time8);
            timeView.setText("");
        }
        cnt++;
        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name9);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time9);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name9);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time9);
            timeView.setText("");
        }
        cnt++;
        if (cnt < history_size) {
            TextView nameView = (TextView) findViewById(R.id.building_name10);
            nameView.setText(user.history.get(cnt).getBuilding());
            TextView timeView = (TextView) findViewById(R.id.reserved_time10);
            timeView.setText(user.history.get(cnt).getTime());
        } else {
            TextView nameView = (TextView) findViewById(R.id.building_name10);
            nameView.setText("");
            TextView timeView = (TextView) findViewById(R.id.reserved_time10);
            timeView.setText("");
        }


    }
}

