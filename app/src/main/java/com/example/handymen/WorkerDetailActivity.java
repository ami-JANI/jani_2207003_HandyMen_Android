package com.example.handymen;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;

public class WorkerDetailActivity extends AppCompatActivity {

    TextView tvName, tvProfession, tvLocation, tvSelectedDate,
            tvPhone, tvExperience, tvRate, tvEmail;

    GridLayout slotGrid;
    DatePicker datePicker;

    DatabaseReference workerRef, bookingRef;
    FirebaseUser currentUser;
    String workerId;
    String selectedDate = "";

    // 🎨 COLORS (NO TINT — DIRECT COLOR)
    private final int COLOR_AVAILABLE     = Color.LTGRAY;
    private final int COLOR_REQUESTED     = Color.YELLOW;
    private final int COLOR_MY_BOOKED     = Color.parseColor("#2d9d48");
    private final int COLOR_OTHER_BOOKED  = Color.RED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        // Views
        tvName = findViewById(R.id.tvName);
        tvProfession = findViewById(R.id.tvProfession);
        tvLocation = findViewById(R.id.tvLocation);
        tvPhone = findViewById(R.id.tvPhone);
        tvExperience = findViewById(R.id.tvExperience);
        tvRate = findViewById(R.id.tvRate);
        tvEmail = findViewById(R.id.tvEmail);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        slotGrid = findViewById(R.id.slotGrid);
        datePicker = findViewById(R.id.datePicker);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        workerId = getIntent().getStringExtra("workerId");
        if (workerId == null) {
            Toast.makeText(this, "Worker not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        workerRef = FirebaseDatabase.getInstance()
                .getReference("workers")
                .child(workerId);

        bookingRef = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(workerId);

        loadWorkerDetails();
        initSlotButtons();

        // Date picker
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, year, month, day) -> {
                    selectedDate = day + "-" + (month + 1) + "-" + year;
                    tvSelectedDate.setText(selectedDate);
                    loadSlots(selectedDate);
                });

        selectedDate = datePicker.getDayOfMonth() + "-"
                + (datePicker.getMonth() + 1) + "-"
                + datePicker.getYear();

        tvSelectedDate.setText(selectedDate);
        loadSlots(selectedDate);
    }

    // ================= WORKER DETAILS =================
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

                Worker w = snapshot.getValue(Worker.class);
                if (w == null) return;

                tvName.setText(w.name);
                tvProfession.setText(w.profession);
                tvLocation.setText(w.location);
                tvExperience.setText(w.experience);
                tvPhone.setText(w.phone);
                tvRate.setText(w.rate);
                tvEmail.setText(w.email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ================= SLOT BUTTONS INIT =================
    private void initSlotButtons() {
        slotGrid.removeAllViews();
        slotGrid.setColumnCount(4);

        for (int i = 0; i < 8; i++) {
            Button btn = new Button(this);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(12, 12, 12, 12);

            btn.setLayoutParams(params);
            btn.setAllCaps(false);
            btn.setBackgroundColor(COLOR_AVAILABLE);

            slotGrid.addView(btn);
        }
    }

    // ================= LOAD SLOTS =================
    private void loadSlots(String date) {
        bookingRef.child(date).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (int i = 0; i < slotGrid.getChildCount(); i++) {
                            Button slot = (Button) slotGrid.getChildAt(i);
                            int hour = 9 + i;
                            String slotKey = hour + ":00";

                            slot.setText(slotKey);
                            slot.setEnabled(true);
                            slot.setBackgroundColor(COLOR_AVAILABLE);
                            slot.setOnClickListener(null);

                            DataSnapshot slotSnap = snapshot.child(slotKey);

                            // FREE
                            if (!slotSnap.exists()) {
                                slot.setBackgroundColor(COLOR_AVAILABLE);
                                slot.setOnClickListener(v ->
                                        requestSlot(slot, date, slotKey));
                            }
                            else {
                                String status = slotSnap.child("status")
                                        .getValue(String.class);
                                String userId = slotSnap.child("userId")
                                        .getValue(String.class);

                                if ("REQUESTED".equals(status)) {
                                    if (currentUser.getUid().equals(userId)) {
                                        slot.setBackgroundColor(COLOR_REQUESTED);
                                        slot.setOnClickListener(v ->
                                                cancelSlot(slot, date, slotKey));
                                    } else {
                                        slot.setBackgroundColor(COLOR_OTHER_BOOKED);
                                        slot.setEnabled(false);
                                    }
                                }
                                else if ("CONFIRMED".equals(status)) {
                                    if (currentUser.getUid().equals(userId)) {
                                        slot.setBackgroundColor(COLOR_MY_BOOKED);
                                    } else {
                                        slot.setBackgroundColor(COLOR_OTHER_BOOKED);
                                    }
                                    slot.setEnabled(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ================= REQUEST SLOT =================
    private void requestSlot(Button slot, String date, String slotKey) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("status", "REQUESTED");
        data.put("userId", currentUser.getUid());

        bookingRef.child(date).child(slotKey).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    slot.setBackgroundColor(COLOR_REQUESTED);
                    slot.setOnClickListener(v ->
                            cancelSlot(slot, date, slotKey));
                    Toast.makeText(this,
                            "Request sent", Toast.LENGTH_SHORT).show();
                });
    }

    // ================= CANCEL SLOT =================
    private void cancelSlot(Button slot, String date, String slotKey) {
        bookingRef.child(date).child(slotKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    slot.setBackgroundColor(COLOR_AVAILABLE);
                    slot.setOnClickListener(v ->
                            requestSlot(slot, date, slotKey));
                    Toast.makeText(this,
                            "Request canceled", Toast.LENGTH_SHORT).show();
                });
    }
}
