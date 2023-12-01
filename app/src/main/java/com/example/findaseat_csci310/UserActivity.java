package com.example.findaseat_csci310;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;


public class UserActivity extends Activity {
    public User user;
    private ArrayList<TextView> cell_tvs;
    public String usr_id;
    public FirebaseDatabase root;
    public DatabaseReference reference;

    public int new_start;
    public int new_end;
    private BottomNavigationView bottomNavigationView;
    private String in_out;
    public Building building;

    private PopupWindow popupWindow;
    private RelativeLayout layout;
    private ArrayList<Boolean> selected_slots;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        usr_id = intent.getStringExtra("id");
        if (usr_id== null) {
            usr_id = "1000000001";
        }
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

        selected_slots = new ArrayList<>(Collections.nCopies(26, false));


        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars/"+usr_id+".jpg");
        // Get the download URL
        ImageView imageView = findViewById(R.id.imageView);
        storageReference.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d("firebase", "onSuccess: Got profile image");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageView.setImageBitmap(bitmap);
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
            else if (item.getItemId() == R.id.bottom_logout) {
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

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancel(v);
                Log.d("debug", "cancel button clicked");
            }
        });

    }

    public void onClickCancel(View view) {
        if (user.currentReservation == null) {
            Toast.makeText(getApplicationContext(), "No current reservation", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("debug", "onClickCancel called");
        // pop up an alert dialog to confirm cancellation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to cancel your reservation?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, id1) -> { cancelReserve(); dialog.dismiss(); });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void cancelReserve() {
        // Add current Reservation to the first element of history (need to shift previous reservations
        // to the right and set history[0] = currentReservation)
        cancel();
        load_info();

        // update building database
        String building_name = user.history.get(0).getBuilding();
        in_out = user.history.get(0).getInOut();
        int start = user.history.get(0).start_time;
        int end = user.history.get(0).end_time;
        // read building data into building
        reference.child("Buildings").child(building_name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (String.valueOf(task.getResult().getValue()) != null) {
                        building = task.getResult().getValue(Building.class);

                        // update building object
                        if (in_out.equals("indoor")) {
                            for (int i=start; i<=end; i++) {
                                building.indoor_avail.set(i, building.indoor_avail.get(i)+1);
                            }
                        } else {
                            for (int i=start; i<=end; i++) {
                                building.outdoor_avail.set(i, building.outdoor_avail.get(i)+1);
                            }
                        }

                        // push building object to database
                        reference.child("Buildings").child(building_name).setValue(building);
                    }
                }
            }
        });


        update_database();
    }


    public void onClickManage(View view) {
        if (user.currentReservation == null) {
            Toast.makeText(getApplicationContext(), "No current reservation", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize selected_slots
        selected_slots = new ArrayList<>(Collections.nCopies(26, false));

        // create popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.manage_popup, null);
        TextView buildingName = popupView.findViewById(R.id.reserved_name_field);
        buildingName.setText(building.name);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean foucsable = true;
        popupWindow = new PopupWindow(popupView, width, height, foucsable);
        layout = (RelativeLayout) findViewById(R.id.profile_layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });

        // display availability in the popup window
        ArrayList<Integer> avail;
        if (user.currentReservation.getInOut().equals("indoor")) {
            // display indoor availability
            avail = building.indoor_avail;
        } else {
            // display outdoor availability
            avail = building.outdoor_avail;
        }
        cell_tvs = new ArrayList<TextView>();
        GridLayout grid = (GridLayout) popupView.findViewById(R.id.gridLayout01);
        Log.d("debug", "grid: "+grid.toString());

        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<13; i++) {
            for (int j=0; j<2; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                if (avail.get(i*2+j) >0) {
                    tv.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
                    tv.setClickable(true);
                }
                else if (avail.get(i*2+j) == 0) {
                    tv.setBackground(getResources().getDrawable(R.drawable.edittext_error_bkg));
                    tv.setClickable(false);
                }

                if (j == 0) {
                    String time = String.valueOf(i+8)+":00-"+String.valueOf(i+8)+":29";
                    tv.setText(time);
                } else {
                    String time = String.valueOf(i+8)+":30-"+String.valueOf(i+8)+":59";
                    tv.setText(time);
                }
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);
                cell_tvs.add(tv);
            }
        }

        // if user clicks "cancel", close the popup window
        Button backButton = popupView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickConfirm(View view) {
        // check if the selected time slot is valid
        boolean isValid = checkConsecutiveReservation(selected_slots) && checkTotalReservation(selected_slots)
                && checkCurrentTime(selected_slots);

        if (!isValid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Invalid Timeslot Selected")
                    .setCancelable(true)
                    .setPositiveButton("OK", (dialog, id1) -> { dialog.dismiss(); });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            addOldAvail();
            deleteNewAvail();

            // update building object
            reference.child("Buildings").child(building.name).setValue(building);
            // update user object
            user.currentReservation.start_time = new_start;
            user.currentReservation.end_time = new_end;
            reference.child("Users").child(usr_id).setValue(user);
            load_info();
            popupWindow.dismiss();
        }
    }

    private void onClickTV(View view) {
        TextView tv = (TextView) view;
        int idx = findIndexOfCellTextView(tv);
        if (!selected_slots.get(idx)) { // slot is not selected
            tv.setBackground(getResources().getDrawable(R.drawable.edittext_selected_bkg));
            selected_slots.set(idx, true);
        } else { // slot is selected
            tv.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
            selected_slots.set(idx, false);
        }
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
            TextView in_out = (TextView) findViewById(R.id.activated_reserved_indoor);
            in_out.setText(user.currentReservation.getInOut());
        } else {
            TextView current_reservation = (TextView) findViewById(R.id.activated_reserved_buildingname);
            current_reservation.setText("No Current Reservation");
            TextView current_reservation_time = (TextView) findViewById(R.id.activated_reserved_time);
            current_reservation_time.setText("");
            TextView in_out = (TextView) findViewById(R.id.activated_reserved_indoor);
            in_out.setText("");
        }
        if (user.currentReservation != null) {
            reference.child("Buildings").child(user.currentReservation.getBuilding()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());

                    } else {
                        if (String.valueOf(task.getResult().getValue()) != null) {
                            building = task.getResult().getValue(Building.class);
                        }
                    }
                }
            });
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

    public void update_database() {
        // update user to database
        reference.child("Users").child(usr_id).setValue(user);
    }

    public boolean checkConsecutiveReservation(ArrayList<Boolean> slots) {
        boolean isValid = true;
        int consecutiveCnt = 0;
        boolean blockFound = false;
        new_start = 0;
        new_end = 0;
        for (int i=0; i<slots.size(); i++) {
            if (slots.get(i)) {
                consecutiveCnt++;
                if (consecutiveCnt == 1) {
                    new_start = i;
                }
                if (consecutiveCnt > 4) {
                    isValid = false;
                }
                new_end = i;
            } else {
                if (consecutiveCnt > 0 && blockFound) {
                    isValid = false;
                } else if (consecutiveCnt >0) {
                    blockFound = true;
                    consecutiveCnt = 0;
                }
            }
        }
        return isValid;
    }

    public boolean checkTotalReservation(ArrayList<Boolean> slots) {
        boolean isValid = true;
        int totalCnt = 0;
        for (int i=0; i<slots.size(); i++) {
            if (slots.get(i)) {
                totalCnt++;
                if (totalCnt >= 5) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    public boolean checkCurrentTime(ArrayList<Boolean> slots) {

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
        for (int i=0; i<current_slot; i++) {
            if (slots.get(i)) {
                return false;
            }
        }

        return true;
    }
    public void cancel() {
        int size = user.history.size();
        if (size != 0) {
            user.history.add(user.history.get(size-1));
        }
        for (int i=size-1; i>0; i--) {
            user.history.set(i, user.history.get(i-1));
        }
        if (size != 0) {
            user.history.set(0, user.currentReservation);
        }
        else {
            user.history.add(user.currentReservation);
        }
        // Set currentReservation = null
        user.currentReservation = null;
    }

    public void addOldAvail() {
        ArrayList<Integer> avail;
        String in_out = user.currentReservation.in_out;
        if (in_out.equals("indoor")) {
            // display indoor availability
            avail = building.indoor_avail;
        } else {
            // display outdoor availability
            avail = building.outdoor_avail;
        }

        for (int i=user.currentReservation.start_time; i<=user.currentReservation.end_time; i++) {
            avail.set(i, avail.get(i)+1);
        }

        if (in_out.equals("indoor")) {
            // display indoor availability
            building.indoor_avail = avail;
        } else {
            // display outdoor availability
            building.outdoor_avail = avail;
        }
    }

    public void deleteNewAvail() {
        ArrayList<Integer> avail;
        String in_out = user.currentReservation.in_out;
        if (in_out.equals("indoor")) {
            // display indoor availability
            avail = building.indoor_avail;
        } else {
            // display outdoor availability
            avail = building.outdoor_avail;
        }

        for (int i = new_start; i <= new_end; i++) {
            avail.set(i, avail.get(i) - 1);
        }

        if (in_out.equals("indoor")) {
            // display indoor availability
            building.indoor_avail = avail;
        } else {
            // display outdoor availability
            building.outdoor_avail = avail;
        }
    }
}

