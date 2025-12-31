package com.example.handymen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class OngoingWorkActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<BookingItem> list = new ArrayList<>();
    BookingAdapter adapter;

    FirebaseUser user;
    DatabaseReference bookingsRef, workersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        recyclerView = findViewById(R.id.recyclerRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(this, list);
        recyclerView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        workersRef = FirebaseDatabase.getInstance().getReference("workers");
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        loadRequests();
    }

    private void loadRequests() {
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                list.clear();

                for (DataSnapshot workerSnap : snap.getChildren()) {
                    for (DataSnapshot dateSnap : workerSnap.getChildren()) {
                        for (DataSnapshot slotSnap : dateSnap.getChildren()) {

                            String status = slotSnap.child("status").getValue(String.class);
                            String userId = slotSnap.child("userId").getValue(String.class);

                            if ("CONFIRMED".equals(status) && user.getUid().equals(userId))
                            {

                                String slot = dateSnap.getKey() + " | " + slotSnap.getKey();
                                String workerKey = workerSnap.getKey();

                                workersRef.child(workerKey)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot w) {
                                                BookingItem item = new BookingItem(
                                                        w.child("profession").getValue(String.class),
                                                        w.child("name").getValue(String.class),
                                                        w.child("email").getValue(String.class),
                                                        w.child("phone").getValue(String.class),
                                                        slot
                                                );
                                                list.add(item);
                                                adapter.notifyDataSetChanged();
                                            }
                                            @Override public void onCancelled(@NonNull DatabaseError e) {}
                                        });
                            }
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
