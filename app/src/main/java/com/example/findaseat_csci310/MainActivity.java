package com.example.findaseat_csci310;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;


    private GoogleMap myMap;
    private String building_name;
    private boolean isLogin = false;

    private boolean mode = false; // 0 for info, 1 for reserve

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        load_login();

        addDummyBuidings();

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

    void load_login() {
        setContentView(R.layout.login_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng usc = new LatLng(34.0222316, -118.2845691);
        MarkerOptions usc_marker = new MarkerOptions().position(usc).title("Taper Hall");
        usc_marker.snippet("USC");
        myMap.addMarker(usc_marker);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(usc));

        // initialize zoom level
        myMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        // set max zoom level
        myMap.setMaxZoomPreference(20);
        // set min zoom level
        myMap.setMinZoomPreference(15);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
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
                }
                return true;
            }
        });
    }

    private void addDummyBuidings() {

    }
}