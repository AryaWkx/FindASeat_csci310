package com.example.findaseat_csci310;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupTabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        Button signupButton = view.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = view.findViewById(R.id.signup_email);
                EditText uscID = view.findViewById(R.id.uscid);
                EditText name = view.findViewById(R.id.signup_name);
                EditText affliation = view.findViewById(R.id.signup_affiliation);
                EditText pwd = view.findViewById(R.id.signup_password);
                boolean valid = true;
                if (!isEmailValid(email.getText().toString())) {
                    valid = false;
                    email.setBackground(getResources().getDrawable(R.drawable.edittext_error_bkg));
                    email.setHint("Invalid Email");
                    email.setText("");
                } else {
                    email.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
                }
                if (!isIDValid(uscID.getText().toString())) {
                    valid = false;
                    uscID.setBackground(getResources().getDrawable(R.drawable.edittext_error_bkg));
                    uscID.setHint("Invalid USC ID");
                    uscID.setText("");
                } else {
                    uscID.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
                }

                if (name.getText().toString().equals("")) {
                    valid = false;
                    name.setBackground(getResources().getDrawable(R.drawable.edittext_error_bkg));
                    name.setHint("Name cannot be empty");
                    name.setText("");
                } else {
                    name.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
                }

                if (!isAffValid(affliation.getText().toString())) {
                    valid = false;
                    affliation.setBackground(getResources().getDrawable(R.drawable.edittext_error_bkg));
                    affliation.setHint("Type in Student or Faculty");
                    affliation.setText("");
                } else {
                    affliation.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
                }

                if (!isPwdValid(pwd.getText().toString())) {
                    valid = false;
                    pwd.setBackground(getResources().getDrawable(R.drawable.edittext_error_bkg));
                    pwd.setHint("At least 8 characters");
                    pwd.setText("");
                } else {
                    pwd.setBackground(getResources().getDrawable(R.drawable.edittext_bkg));
                }

                if (valid) {
                    signup(email.getText().toString(), uscID.getText().toString(),
                            name.getText().toString(), affliation.getText().toString(),
                            pwd.getText().toString());
                    pwd.setText("");
                    email.setText("");
                    uscID.setText("");
                    name.setText("");
                    affliation.setText("");
                }
            }
        });

        return view;
    }

    public boolean isEmailValid(String email) {

        String email_pattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(email_pattern);
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    public boolean isIDValid(String id) {

        if (id == null) {
            return false;
        }
        if (id.length() != 10) {
            return false;
        }
        return true;
    }

    public boolean isAffValid(String aff) {
        if (aff.equals("Student") || aff.equals("Faculty")) {
            Log.i("MyTag", "This is an info log message inside if:"+aff);
            return true;
        }
        Log.i("MyTag", "This is an info log message:"+aff);
        return false;
    }

    public boolean isPwdValid(String p) {
        if (p.length() < 8) {
            return false;
        }
        return true;
    }

    public void signup(String email, String id, String name, String aff, String pwd) {
        User user = new User(email, id, name, aff, pwd);
        FirebaseDatabase root;
        DatabaseReference reference;

        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference.child("Users").child(id).setValue(user);

        Button signupButton = getView().findViewById(R.id.signup_button);
        signupButton.setBackgroundColor(getResources().getColor(R.color.yellow));
        signupButton.setText("You've been signed up!");
    }
}