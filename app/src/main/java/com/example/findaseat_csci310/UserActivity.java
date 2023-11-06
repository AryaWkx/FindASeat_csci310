package com.example.findaseat_csci310;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UserActivity extends AppCompatActivity {
    public User user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(user.usc_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                } else {
                    if (String.valueOf(task.getResult().getValue()) != null) {
//                        user.name = String.valueOf(task.getResult().child("name").getValue());
//                        user.usc_id = String.valueOf(task.getResult().child("usc_id").getValue());
//                        user.affiliation = String.valueOf(task.getResult().child("affiliation").getValue());
//                        user.email = String.valueOf(task.getResult().child("email").getValue());
//                        user.currentReservation.in_out = String.valueOf(task.getResult().child("currentReservation")
//                                .child("in_out").getValue());
//                        user.currentReservation.start_time = Integer.parseInt(String.valueOf(task.getResult().child("currentReservation")
//                                .child("start_time").getValue()));
//                        user.currentReservation.end_time = Integer.parseInt(String.valueOf(task.getResult().child("currentReservation")
//                                .child("end_time").getValue()));
//                        user.currentReservation.buiding_name = String.valueOf(task.getResult().child("currentReservation")
//                                .child("buiding_name").getValue());
                        user = task.getResult().getValue(User.class);

                    }
                }
            }
        });
        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars/[user_id].jpg");

// ImageView in your Activity
        ImageView imageView = findViewById(R.id.imageView);

// Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)
        Glide.with(context)
                .load(storageReference)
                .into(imageView);
    }


}

