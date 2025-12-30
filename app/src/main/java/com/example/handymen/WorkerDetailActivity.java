package com.example.handymen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class WorkerDetailActivity extends AppCompatActivity {

    TextView tvName, tvPhone, tvLocation, tvEmail;
    DatabaseReference workersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvLocation = findViewById(R.id.tvLocation);
        tvEmail = findViewById(R.id.tvEmail);

        String email = getIntent().getStringExtra("workerEmail");

        if (email == null) {
            Toast.makeText(this,
                    "Worker not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        workersRef = FirebaseDatabase.getInstance()
                .getReference("workers");

        // 🔥 SEARCH BY EMAIL
        workersRef.orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    Toast.makeText(
                                            WorkerDetailActivity.this,
                                            "Worker not found",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    Worker w =
                                            ds.getValue(Worker.class);

                                    if (w == null) continue;

                                    tvName.setText(w.name);
                                    tvPhone.setText(w.phone);
                                    tvLocation.setText(w.location);
                                    tvEmail.setText(w.email);
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {}
                        });
    }
}
