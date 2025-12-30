package com.example.handymen;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerDetailActivity extends AppCompatActivity {

    GridLayout slotGrid;
    String workerEmailKey; // email used as key
    FirebaseUser currentUser;
    DatabaseReference slotsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        slotGrid = findViewById(R.id.slotGrid);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get worker email key from intent
        workerEmailKey = getIntent().getStringExtra("workerEmail");
        if (workerEmailKey == null) {
            Toast.makeText(this, "Worker not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        slotsRef = FirebaseDatabase.getInstance()
                .getReference("workers")
                .child(workerEmailKey)
                .child("slots");

        loadSlots();
    }

    private void loadSlots() {
        slotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slotGrid.removeAllViews();

                for (DataSnapshot slotSnap : snapshot.getChildren()) {
                    String slotKey = slotSnap.getKey();
                    if (slotKey == null) continue;

                    String status = slotSnap.child("status").getValue(String.class);
                    if (status == null) status = "available";

                    String bookedUserId = slotSnap.child("userId").getValue(String.class);
                    if (bookedUserId == null) bookedUserId = "";

                    Button btn = new Button(WorkerDetailActivity.this);
                    btn.setText(slotKey.replace("_", "-")); // display like "9-10"
                    btn.setTag(slotKey);

                    // Default color
                    btn.setBackgroundColor(Color.TRANSPARENT);

                    // Coloring logic
                    switch (status) {
                        case "booked":
                            if (currentUser.getUid().equals(bookedUserId)) {
                                btn.setBackgroundColor(0xFFFFA500); // orange for user booked
                            } else {
                                btn.setBackgroundColor(0xFFFF0000); // red for others
                                btn.setEnabled(false);
                            }
                            break;
                        case "confirmed":
                            if (currentUser.getUid().equals(bookedUserId)) {
                                btn.setBackgroundColor(0xFF00FF00); // green for user confirmed
                            } else {
                                btn.setBackgroundColor(0xFFFF0000); // red for others
                                btn.setEnabled(false);
                            }
                            break;
                        default:
                            btn.setBackgroundColor(Color.TRANSPARENT); // available
                    }

                    btn.setOnClickListener(v -> handleSlotClick(slotKey, btn));

                    slotGrid.addView(btn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorkerDetailActivity.this, "Failed to load slots.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSlotClick(String slotKey, Button btn) {
        slotsRef.child(slotKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot slotSnap) {
                String status = slotSnap.child("status").getValue(String.class);
                if (status == null) status = "available";

                String bookedUserId = slotSnap.child("userId").getValue(String.class);
                if (bookedUserId == null) bookedUserId = "";

                if ("available".equals(status)) {
                    // User books the slot (orange)
                    slotsRef.child(slotKey).child("status").setValue("booked");
                    slotsRef.child(slotKey).child("userId").setValue(currentUser.getUid());
                    btn.setBackgroundColor(0xFFFFA500);
                } else if ("booked".equals(status) && currentUser.getUid().equals(bookedUserId)) {
                    // Worker confirms the slot (green)
                    slotsRef.child(slotKey).child("status").setValue("confirmed");
                    btn.setBackgroundColor(0xFF00FF00);
                } else {
                    // Do nothing if another user booked or confirmed
                    Toast.makeText(WorkerDetailActivity.this, "This slot is not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorkerDetailActivity.this, "Failed to update slot", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
