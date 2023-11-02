package com.example.findaseat_csci310;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupTabFragment extends Fragment {

    public Uri imageUri;
    Uri defaultUri = Uri.parse("android.resource://com.example.findaseat_csci310;/" + R.drawable.avatar);
    public FirebaseStorage storage;
    public StorageReference storageRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        Button signupButton = view.findViewById(R.id.signup_button);
        Button avatar = view.findViewById(R.id.upload_avatar);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = view.findViewById(R.id.signup_email);
                EditText uscID = view.findViewById(R.id.uscid);
                EditText name = view.findViewById(R.id.signup_name);
                EditText affliation = view.findViewById(R.id.signup_affiliation);
                EditText pwd = view.findViewById(R.id.signup_password);
                ImageView imageView = view.findViewById(R.id.avatar);
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
                    uploadAvatar(imageView, uscID.getText().toString());
                    pwd.setText("");
                    email.setText("");
                    uscID.setText("");
                    name.setText("");
                    affliation.setText("");
                    imageView.setImageURI(defaultUri);
                }
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);

                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReference();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ImageView avatar = getView().findViewById(R.id.avatar);
            avatar.setImageURI(imageUri);
        }
    }

    public void uploadAvatar(ImageView imageView, String id) {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image...");
        pd.show();

        // Create a storage reference from our app
        String path = "avatars/" + id + ".jpg";
        StorageReference avatarRef = storageRef.child(path);
        if (imageUri == null) {
            imageUri = defaultUri;
        }
        avatarRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(
                            UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(imageView, "Uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getContext(),
                                        "Upload failed: " + e.getMessage(),
                                        Toast.LENGTH_LONG);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });

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