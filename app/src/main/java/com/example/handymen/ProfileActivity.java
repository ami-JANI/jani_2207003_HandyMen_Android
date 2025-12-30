package com.example.handymen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etPhone, etLocation;
    TextView tvEmail;
    Button btnSave;

    DatabaseReference usersRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etProfileName);
        etPhone = findViewById(R.id.etProfilePhone);
        etLocation = findViewById(R.id.etProfileLocation);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnSave = findViewById(R.id.btnSaveProfile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        String emailKey = currentUser.getEmail().replace(".", "_");
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(emailKey);

        // Load current user data
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);

                    etName.setText(name != null ? name : "");
                    etPhone.setText(phone != null ? phone : "");
                    etLocation.setText(location != null ? location : "");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Save changes button
        btnSave.setOnClickListener(v -> saveProfileChanges());
    }

    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        usersRef.child("name").setValue(name);
        usersRef.child("phone").setValue(phone);
        usersRef.child("location").setValue(location)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
