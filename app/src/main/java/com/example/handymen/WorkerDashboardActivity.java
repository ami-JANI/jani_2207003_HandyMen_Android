package com.example.handymen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;

public class WorkerDashboardActivity extends AppCompatActivity {

    // ================= UI =================
    private EditText etName, etEmail, etPhone, etProfession, etExperience, etRate, etLocation;
    private DatePicker datePicker;
    private GridLayout slotGrid;
    private Button btnSave, btnSignOut, btnNotification;
    private TextView tvSelectedDate;

    // ================= FIREBASE =================
    private DatabaseReference workerRef, bookingRef;
    private FirebaseUser currentWorker;

    private String workerEmailKey;
    private String selectedDate;

    // ================= COLORS =================
    private final int COLOR_AVAILABLE = Color.LTGRAY;
    private final int COLOR_REQUESTED = Color.YELLOW;
    private final int COLOR_CONFIRMED = Color.parseColor("#2d9d48");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard); // ✅ ONLY ONCE

        initViews();

        // ================= AUTH =================
        currentWorker = FirebaseAuth.getInstance().getCurrentUser();
        if (currentWorker == null || currentWorker.getEmail() == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        workerEmailKey = currentWorker.getEmail().replace(".", "_");

        // ================= FIREBASE REFS =================
        workerRef = FirebaseDatabase.getInstance()
                .getReference("workers")
                .child(workerEmailKey);

        bookingRef = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(workerEmailKey);

        fetchWorkerData();
        initSlotButtons();
        setupDatePicker();
        setupButtons();
    }

    // ================= LOAD WORKER =================
    private void fetchWorkerData() {
        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(WorkerDashboardActivity.this,
                            "Worker profile not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Worker w = snapshot.getValue(Worker.class);
                if (w == null) return;

                etName.setText(w.name);
                etEmail.setText(w.email);
                etPhone.setText(w.phone);
                etProfession.setText(w.profession);
                etExperience.setText(w.experience);
                etRate.setText(w.rate);
                etLocation.setText(w.location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("WORKER", error.getMessage());
            }
        });
    }

    // ================= DATE PICKER =================
    private void setupDatePicker() {
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, y, m, d) -> {
                    selectedDate = d + "-" + (m + 1) + "-" + y;
                    tvSelectedDate.setText(selectedDate);
                    listenSlots(selectedDate);
                }
        );

        selectedDate = datePicker.getDayOfMonth() + "-"
                + (datePicker.getMonth() + 1) + "-"
                + datePicker.getYear();

        tvSelectedDate.setText(selectedDate);
        listenSlots(selectedDate);
    }

    // ================= INIT SLOTS =================
    private void initSlotButtons() {
        slotGrid.removeAllViews();
        slotGrid.setColumnCount(2);

        for (int i = 0; i < 8; i++) {
            Button btn = new Button(this);
            btn.setAllCaps(false);
            slotGrid.addView(btn);
        }
    }

    // ================= REALTIME LISTENER =================
    private void listenSlots(String date) {
        bookingRef.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateSlots(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("WORKER", error.getMessage());
            }
        });
    }

    // ================= UPDATE SLOTS =================
    private void updateSlots(DataSnapshot snapshot) {
        for (int i = 0; i < slotGrid.getChildCount(); i++) {

            Button slot = (Button) slotGrid.getChildAt(i);
            int hour = 9 + i;
            String slotKey = hour + ":00";

            slot.setText(slotKey);
            slot.setEnabled(true);
            slot.setOnClickListener(null);

            DataSnapshot slotSnap = snapshot.child(slotKey);

            if (!slotSnap.exists()) {
                slot.setBackgroundTintList(ColorStateList.valueOf(COLOR_AVAILABLE));
                continue;
            }

            String status = slotSnap.child("status").getValue(String.class);
            String userId = slotSnap.child("userId").getValue(String.class);

            if ("REQUESTED".equals(status)) {
                slot.setBackgroundTintList(ColorStateList.valueOf(COLOR_REQUESTED));
                slot.setOnClickListener(v -> confirmSlot(snapshot.getKey(), slotKey, userId));
            }
            else if ("CONFIRMED".equals(status)) {
                slot.setBackgroundTintList(ColorStateList.valueOf(COLOR_CONFIRMED));
                slot.setEnabled(false);
            }
        }
    }

    // ================= CONFIRM =================
    private void confirmSlot(String date, String slotKey, String userId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("status", "CONFIRMED");
        data.put("userId", userId);

        bookingRef.child(date).child(slotKey)
                .setValue(data)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Booking confirmed", Toast.LENGTH_SHORT).show()
                );
    }

    // ================= BUTTONS =================
    private void setupButtons() {

        btnSave.setOnClickListener(v -> saveProfile());

        btnNotification.setOnClickListener(v ->
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
        );

        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, WorkerLoginActivity.class));
            finish();
        });
    }

    // ================= SAVE =================
    private void saveProfile() {
        workerRef.child("name").setValue(etName.getText().toString());
        workerRef.child("email").setValue(etEmail.getText().toString());
        workerRef.child("phone").setValue(etPhone.getText().toString());
        workerRef.child("profession").setValue(etProfession.getText().toString());
        workerRef.child("experience").setValue(etExperience.getText().toString());
        workerRef.child("rate").setValue(etRate.getText().toString());
        workerRef.child("location").setValue(etLocation.getText().toString());

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }

    // ================= INIT VIEWS =================
    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etProfession = findViewById(R.id.etProfession);
        etExperience = findViewById(R.id.etExperience);
        etRate = findViewById(R.id.etRate);
        etLocation = findViewById(R.id.etLocation);

        datePicker = findViewById(R.id.datePicker);
        slotGrid = findViewById(R.id.slotGrid);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        btnSave = findViewById(R.id.btnSaveChanges);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnNotification = findViewById(R.id.btnNotification);
    }
}
