package com.example.handymen;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class WorkerDashboardActivity extends AppCompatActivity {


    private EditText etName, etEmail, etPhone, etProfession, etExperience, etRate, etLocation;
    private DatePicker datePicker;
    private GridLayout slotGrid;
    private Button btnSave, btnSignOut, btnNotification;


    private DatabaseReference workerRef;


    enum SlotStatus { FREE, REQUESTED, BOOKED }
    private SlotStatus[] slots = new SlotStatus[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_worker_dashboard);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Layout load error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        initViews();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String email = user.getEmail();
        if (email == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String workerId = email.replace(".", "_");
        workerRef = FirebaseDatabase.getInstance()
                .getReference("workers")
                .child(workerId);

        fetchWorkerData(workerId);

        initSlots();
        setupButtons();
    }

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

        btnSave = findViewById(R.id.btnSaveChanges);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnNotification = findViewById(R.id.btnNotification);
    }

    private void fetchWorkerData(String workerId) {
        if (workerRef == null) return;

        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(WorkerDashboardActivity.this,
                            "Worker profile not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Worker worker = snapshot.getValue(Worker.class);
                if (worker == null) {
                    Toast.makeText(WorkerDashboardActivity.this,
                            "Worker data is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etName != null) etName.setText(worker.name);
                if (etEmail != null) etEmail.setText(worker.email);
                if (etPhone != null) etPhone.setText(worker.phone);
                if (etProfession != null) etProfession.setText(worker.profession);
                if (etExperience != null) etExperience.setText(worker.experience);
                if (etRate != null) etRate.setText(worker.rate);
                if (etLocation != null) etLocation.setText(worker.location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("WorkerDashboard", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void initSlots() {
        if (slotGrid == null) return;

        for (int i = 0; i < slotGrid.getChildCount() && i < slots.length; i++) {
            View v = slotGrid.getChildAt(i);
            if (!(v instanceof Button)) continue;

            Button btn = (Button) v;
            int index = i;
            slots[i] = SlotStatus.FREE;
            updateSlotUI(btn, SlotStatus.FREE);

            btn.setOnClickListener(view -> handleSlotClick(index, btn));
        }
    }

    private void handleSlotClick(int index, Button btn) {
        if (index >= slots.length) return;

        if (slots[index] == SlotStatus.FREE) return;

        slots[index] = (slots[index] == SlotStatus.REQUESTED) ? SlotStatus.BOOKED : SlotStatus.FREE;
        updateSlotUI(btn, slots[index]);
    }

    private void updateSlotUI(Button btn, SlotStatus status) {
        if (btn == null) return;

        switch (status) {
            case FREE:
                btn.setBackgroundColor(Color.LTGRAY);
                btn.setTextColor(Color.BLACK);
                break;
            case REQUESTED:
                btn.setBackgroundColor(Color.YELLOW);
                btn.setTextColor(Color.BLACK);
                break;
            case BOOKED:
                btn.setBackgroundColor(Color.parseColor("#2d9d48"));
                btn.setTextColor(Color.WHITE);
                break;
        }
    }

    private void setupButtons() {
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProfile());
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v ->
                    Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        }

        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> finish());
        }
    }

    private void saveProfile() {
        if (workerRef == null) return;

        if (etName != null) workerRef.child("name").setValue(etName.getText().toString());
        if (etEmail != null) workerRef.child("email").setValue(etEmail.getText().toString());
        if (etPhone != null) workerRef.child("phone").setValue(etPhone.getText().toString());
        if (etProfession != null) workerRef.child("profession").setValue(etProfession.getText().toString());
        if (etExperience != null) workerRef.child("experience").setValue(etExperience.getText().toString());
        if (etRate != null) workerRef.child("rate").setValue(etRate.getText().toString());
        if (etLocation != null) workerRef.child("location").setValue(etLocation.getText().toString());

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
