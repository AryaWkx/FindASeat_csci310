package com.example.findaseat_csci310;

public class BuildingActivity {
    // trying copilot
    void load_building() {
        setContentView(R.layout.building_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_login();
            }
        });
    }
}
