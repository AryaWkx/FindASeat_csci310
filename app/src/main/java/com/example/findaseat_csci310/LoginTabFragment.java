 package com.example.findaseat_csci310;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

 public class LoginTabFragment extends Fragment {

     public FirebaseDatabase root;
     public DatabaseReference reference;
     public String correctPassword = null;
     public boolean registered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        // TODO: Add login functionality
        // read input in edit text fields
        EditText ID = view.findViewById(R.id.login_uscid);
        EditText pwd = view.findViewById(R.id.login_password);
        Button loginButton = view.findViewById(R.id.login_button);
        // check if input is valid when login button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginValidation(ID, pwd);
            }
        });
        return view;
    }

    // check if input is valid
     public void loginValidation(EditText ID, EditText pwd) {
         // check if ID is valid
         String id = ID.getText().toString();
         String password = pwd.getText().toString();

         root = FirebaseDatabase.getInstance();
         reference = root.getReference();
         reference.child("Users").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DataSnapshot> task) {
                 if (!task.isSuccessful()) {
                     Log.e("firebase", "Error getting data", task.getException());
                 }
                 else {
                     Log.d("firebase", "Success reading: "+String.valueOf(task.getResult().getValue()));
                     if (String.valueOf(task.getResult().getValue()) != null) {
                         registered = true;
                         correctPassword = String.valueOf(task.getResult().child("password").getValue());
                     }
                 }
             }
         });
         if (!registered) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Invalid ID. Please try again or sign up.")
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, id1) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                alert.show();
         } else if (!password.equals(correctPassword)) {
             AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
             builder.setMessage("Incorrect password. Please try again.")
                     .setCancelable(true)
                     .setPositiveButton("OK", (dialog, id1) -> dialog.dismiss());
             AlertDialog alert = builder.create();
             alert.show();
         } else {//TODO: go to main page
         }
     }
}