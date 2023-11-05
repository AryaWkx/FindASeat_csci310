package com.example.findaseat_csci310;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.view.Gravity;
import android.widget.PopupWindow;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseDatabase root;
    DatabaseReference reference;

    private GoogleMap myMap;
    private String building_name;
    private boolean isLogin = false;
    private ConstraintLayout layout;
    private String usrID;
    private String username;

    private boolean mode = false; // 0 for info, 1 for reserve

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        isLogin = intent.getBooleanExtra("isLogin", false);
        usrID = intent.getStringExtra("usrID");
        username = intent.getStringExtra("username");
        // log user ID
        if (isLogin) {
            Toast.makeText(getApplicationContext(), "Logged in as " + username, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_main);
        load_mapview();
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
                building_name = marker.getTitle();
                if (!mode) {
                    marker.showInfoWindow();
                } else {
                    // TODO: jump to reserve page if logged in, else jump to login page
                    if (isLogin) {
                        layout = findViewById(R.id.constraintLayout);
                        createPopupWindow();
                    } else {
                        promptLogin();
                    }

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

    private void addDummyBuidings() {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        // Taper Hall
        reference.child("Buildings").child("Taper Hall (THH)").child("lat").setValue(34.0222316);
        reference.child("Buildings").child("Taper Hall (THH)").child("lng").setValue(-118.2845691);
        // Leavey Library
        reference.child("Buildings").child("Leavey Library (LVL)").child("lat").setValue(34.0217200);
        reference.child("Buildings").child("Leavey Library (LVL)").child("lng").setValue(-118.2828376);
        // Doheny Memorial Library
        reference.child("Buildings").child("Doheny Memorial Library (DML)").child("lat").setValue(34.0201440);
        reference.child("Buildings").child("Doheny Memorial Library (DML)").child("lng").setValue(-118.2837366);
        // Science & Engineering Library
        reference.child("Buildings").child("Science & Engineering Library (SEL)").child("lat").setValue(34.0196113);
        reference.child("Buildings").child("Science & Engineering Library (SEL)").child("lng").setValue(-118.2887994);
        // Kaprielian Hall
        reference.child("Buildings").child("Kaprielian Hall (KAP)").child("lat").setValue(34.0224902);
        reference.child("Buildings").child("Kaprielian Hall (KAP)").child("lng").setValue(-118.2910134);
        // Fertitta Hall
        reference.child("Buildings").child("Fertitta Hall (JFF)").child("lat").setValue(34.0187263);
        reference.child("Buildings").child("Fertitta Hall (JFF)").child("lng").setValue(-118.2824098);
        // Hoffman Hall
        reference.child("Buildings").child("Hoffman Hall (HOH)").child("lat").setValue(34.0187226);
        reference.child("Buildings").child("Hoffman Hall (HOH)").child("lng").setValue(-118.2852340);
        // USC Village
        reference.child("Buildings").child("USC Village").child("lat").setValue(34.0252800);
        reference.child("Buildings").child("USC Village").child("lng").setValue(-118.2849921);
        // Grace Ford Salvatori Hall
        reference.child("Buildings").child("Grace Ford Salvatori Hall (GFS)").child("lat").setValue(34.0213012);
        reference.child("Buildings").child("Grace Ford Salvatori Hall (GFS)").child("lng").setValue(-118.2880528);
        // Parkside Residential Area
        reference.child("Buildings").child("Parkside Residential Area").child("lat").setValue(34.0188311);
        reference.child("Buildings").child("Parkside Residential Area").child("lng").setValue(-118.2899874);
    }

    private void createPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.reserve_popup, null);
        TextView buildingName = popupView.findViewById(R.id.building_name_field);
        buildingName.setText(building_name);

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
                .setNegativeButton("Cancel", (dialog, id1) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}