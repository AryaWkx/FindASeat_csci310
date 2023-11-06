package com.example.findaseat_csci310;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseDatabase root;
    DatabaseReference reference;

    private GoogleMap myMap;
    private String building_name;
    private boolean isLogin = false;
    private RelativeLayout layout;
    private String usrID;
    private String username;

    private BottomNavigationView bottomNavigationView;

    private boolean mode = false; // 0 for info, 1 for reserve

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get intent from login page
        Intent intent = getIntent();
        isLogin = intent.getBooleanExtra("isLogin", false);
        usrID = intent.getStringExtra("id");
        username = intent.getStringExtra("username");
        // log user ID
        if (isLogin) {
            Toast.makeText(getApplicationContext(), "Logged in as " + username, Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_main);
        load_mapview();

//        addDummyBuidings();
//        addDummyUser();

        // navigation bar selector
        bottomNavigationView = findViewById(R.id.Navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_map);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_map) {
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                if (isLogin) {
                    Intent intent1 = new Intent(getApplicationContext(), UserActivity.class);
                    intent1.putExtra("id", usrID);
                    Log.d("intent", "usrID given out: " + usrID);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    promptLogin();
                }
                return true;
            } else if (item.getItemId() == R.id.bottom_reservations) {
                if (isLogin) {
                    Intent intent2 = new Intent(getApplicationContext(), BuildingActivity.class);
                    intent2.putExtra("id", usrID);
                    intent2.putExtra("building_name", building_name);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    promptLogin();
                }
                return true;
            }
            return false;
        });
    }

    void load_mapview() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton menufab = findViewById(R.id.floatingActionButton1);
        FloatingActionButton reservefab = findViewById(R.id.floatingActionButton2);
        menufab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = true;
                menufab.setVisibility(View.INVISIBLE);
                reservefab.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Switched to reserve action mode", Toast.LENGTH_SHORT).show();
            }
        });
        reservefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = false;
                menufab.setVisibility(View.VISIBLE);
                reservefab.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Switched to info display mode", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // initialize zoom level
        myMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        // set max zoom level
        myMap.setMaxZoomPreference(20);
        // set min zoom level
        myMap.setMinZoomPreference(15);
        myMap.setPadding(0, 0, 0, 240);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng usc = new LatLng(34.0222316, -118.2845691);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(usc));
        // create clickable markers
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // return the info of the marker
                if (!mode) {
                    marker.showInfoWindow();
                } else {
                    // jump to reserve page if logged in, else jump to login page
                    reserveHandler(marker.getTitle());
                }
                return true;
            }
        });

        // add markers for the buildings in database
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Buildings").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Error getting data", Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> data = (Map<String, Object>) task.getResult().getValue();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    Map singleBuilding = (Map) entry.getValue();
                    LatLng building = new LatLng((double) singleBuilding.get("lat"), (double) singleBuilding.get("lng"));
                    MarkerOptions building_marker = new MarkerOptions().position(building).title(entry.getKey());
                    myMap.addMarker(building_marker);
                }
            }
        });
    }
    private void reserveHandler(String name) {
        if (isLogin) {
            building_name = name;
            layout = findViewById(R.id.main_layout);
            createPopupWindow(name);
        } else {
            promptLogin();
        }
    }
    private void addDummyBuidings() {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();

        Vector<Integer> indoor = new Vector<Integer>(26);
        Vector<Integer> outdoor = new Vector<Integer>(26);
        for (int i=0; i<26; i++){
            indoor.add(10);
            outdoor.add(2);
        }
        // Taper Hall
        Building thh = new Building("Taper Hall (THH)", 34.0222316, -118.2845691, indoor, outdoor);
        reference.child("Buildings").child("Taper Hall (THH)").setValue(thh);
        // Leavey Library
        Building lvl = new Building("Leavey Library (LVL)", 34.0217200, -118.2828376, indoor, outdoor);
        reference.child("Buildings").child("Leavey Library (LVL)").setValue(lvl);
        // Doheny Memorial Library
        Building dml = new Building("Doheny Memorial Library (DML)", 34.0201440, -118.2837366, indoor, outdoor);
        reference.child("Buildings").child("Doheny Memorial Library (DML)").setValue(dml);
        // Science & Engineering Library
        Building sel = new Building("Science & Engineering Library (SEL)", 34.0196113, -118.2887994, indoor, outdoor);
        reference.child("Buildings").child("Science & Engineering Library (SEL)").setValue(sel);
        // Kaprielian Hall
        Building kap = new Building("Kaprielian Hall (KAP)", 34.0224902, -118.2910134, indoor, outdoor);
        reference.child("Buildings").child("Kaprielian Hall (KAP)").setValue(kap);
        // Fertitta Hall
        Building jff = new Building("Fertitta Hall (JFF)", 34.0187263, -118.2824098, indoor, outdoor);
        reference.child("Buildings").child("Fertitta Hall (JFF)").setValue(jff);
        // Hoffman Hall
        Building hoh = new Building("Hoffman Hall (HOH)", 34.0187226, -118.2852340, indoor, outdoor);
        reference.child("Buildings").child("Hoffman Hall (HOH)").setValue(hoh);
        // USC Village
        Building usc = new Building("USC Village", 34.0252800, -118.2849921, indoor, outdoor);
        reference.child("Buildings").child("USC Village").setValue(usc);
        // Grace Ford Salvatori Hall
        Building gfs = new Building("Grace Ford Salvatori Hall (GFS)", 34.0213012, -118.2880528, indoor, outdoor);
        reference.child("Buildings").child("Grace Ford Salvatori Hall (GFS)").setValue(gfs);
        // Parkside Residential Area
        Building prk = new Building("Parkside Residential Area", 34.0188311, -118.2899874, indoor, outdoor);
        reference.child("Buildings").child("Parkside Residential Area").setValue(prk);
    }

    private void createPopupWindow(String name) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.reserve_popup, null);
        TextView buildingName = popupView.findViewById(R.id.reserved_name_field);
        buildingName.setText(name);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean foucsable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, foucsable);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });

    }

    private void promptLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please login first")
                .setCancelable(true)
                .setPositiveButton("Login", (dialog, id1) -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, id1) -> {
                    dialog.dismiss();
                    bottomNavigationView.setSelectedItemId(R.id.bottom_map);
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addDummyUser() {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        User u = new User("email@gmail.com", "0000000001", "test", "Student", "12345678");
        Reservation r1 = new Reservation("Taper Hall (THH)", 4, 5, "indoor");
        Reservation r2 = new Reservation("Taper Hall (THH)", 11, 13, "outdoor");
        u.history.add(r1);
        u.history.add(r2);
        reference.child("Users").child("0000000001").setValue(u);
    }
}