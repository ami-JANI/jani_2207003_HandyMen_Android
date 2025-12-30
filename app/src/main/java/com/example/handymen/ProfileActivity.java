package com.example.handymen;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvLocation;

    DatabaseReference usersRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvPhone = findViewById(R.id.tvProfilePhone);
        tvLocation = findViewById(R.id.tvProfileLocation);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);

                    tvName.setText(name != null ? name : "");
                    tvPhone.setText(phone != null ? phone : "");
                    tvLocation.setText(location != null ? location : "");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
