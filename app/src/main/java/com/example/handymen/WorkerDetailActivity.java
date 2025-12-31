package com.example.handymen;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Calendar;
import java.util.HashMap;

public class WorkerDetailActivity extends AppCompatActivity {

    TextView tvName, tvProfession, tvLocation, tvSelectedDate;
    Button btnPickDate;
    GridLayout slotGrid;

    DatabaseReference workerRef, bookingRef;
    FirebaseUser currentUser;
    String workerId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        // 🔹 Init views
        tvName = findViewById(R.id.tvName);
        tvProfession = findViewById(R.id.tvProfession);
        tvLocation = findViewById(R.id.tvLocation);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        slotGrid = findViewById(R.id.slotGrid);

        // 🔹 Current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Get Firebase worker key
        workerId = getIntent().getStringExtra("workerId");
        if (workerId == null) {
            Toast.makeText(this, "Worker not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Firebase refs (SAME STYLE AS DASHBOARD)
        workerRef = FirebaseDatabase.getInstance()
                .getReference("workers")
                .child(workerId);

        bookingRef = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(workerId);

        loadWorkerDetails();

        btnPickDate.setOnClickListener(v -> showDatePicker());
    }

    // ======================================================
    // LOAD WORKER DETAILS
    // ======================================================
    private void loadWorkerDetails() {
        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(WorkerDetailActivity.this,
                            "Worker profile not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                Worker worker = snapshot.getValue(Worker.class);
                if (worker == null) return;

                tvName.setText(worker.name);
                tvProfession.setText(worker.profession);
                tvLocation.setText(worker.location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ======================================================
    // DATE PICKER
    // ======================================================
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dpd = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = day + "-" + (month + 1) + "-" + year;
                    tvSelectedDate.setText(date);
                    loadSlots(date);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();
    }

    // ======================================================
    // LOAD SLOTS
    // ======================================================
    private void loadSlots(String date) {

        slotGrid.removeAllViews();

        bookingRef.child(date)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap<String, String> booked = new HashMap<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                booked.put(ds.getKey(), ds.getValue(String.class));
                            }
                        }

                        // 8 slots: 9AM–4PM
                        for (int i = 0; i < 8; i++) {

                            int hour = 9 + i;
                            String slotKey = hour + ":00";

                            Button slot = new Button(WorkerDetailActivity.this);
                            slot.setText(slotKey);

                            GridLayout.LayoutParams params =
                                    new GridLayout.LayoutParams();
                            params.width = 0;
                            params.columnSpec = GridLayout.spec(
                                    GridLayout.UNDEFINED, 1f);
                            params.setMargins(8, 8, 8, 8);
                            slot.setLayoutParams(params);

                            String bookedBy = booked.get(slotKey);

                            if (bookedBy == null) {
                                // 🟢 AVAILABLE
                                slot.setBackgroundColor(Color.TRANSPARENT);
                                slot.setOnClickListener(v ->
                                        selectSlot(slot, date, slotKey));

                            } else if (bookedBy.equals(currentUser.getUid())) {
                                // ✅ BOOKED BY CURRENT USER
                                slot.setBackgroundColor(Color.parseColor("#2d9d48"));
                                slot.setEnabled(false);

                            } else {
                                // ❌ BOOKED BY OTHERS
                                slot.setBackgroundColor(Color.RED);
                                slot.setEnabled(false);
                            }

                            slotGrid.addView(slot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ======================================================
    // SLOT SELECTION
    // ======================================================
    private void selectSlot(Button slot, String date, String slotKey) {

        slot.setBackgroundColor(Color.parseColor("#FFA500")); // ORANGE
        slot.setEnabled(false);

        bookingRef.child(date)
                .child(slotKey)
                .setValue(currentUser.getUid())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this,
                                "Slot requested", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Booking failed", Toast.LENGTH_SHORT).show());
    }
}
