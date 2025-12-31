package com.example.handymen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MaidListActivity extends AppCompatActivity {

    RecyclerView rvWorkers;
    WorkerAdapter adapter;
    ArrayList<Worker> workerList;

    RadioButton rbAll, rbMyLocation;
    DatabaseReference workersRef;

    String myLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maid_list);

        // 🔹 Views
        rvWorkers = findViewById(R.id.rvWorkers);
        rbAll = findViewById(R.id.rbAll);
        rbMyLocation = findViewById(R.id.rbMyLocation);

        rvWorkers.setLayoutManager(new LinearLayoutManager(this));

        workerList = new ArrayList<>();
        adapter = new WorkerAdapter(workerList);
        rvWorkers.setAdapter(adapter);

        // 🔹 Click → Worker Details (ADDED)
        adapter.setOnItemClickListener(worker -> {

            if (worker.email == null) {
                Toast.makeText(this, "Worker email missing", Toast.LENGTH_SHORT).show();
                return;
            }

            String workerId = worker.email.replace(".", "_");

            Intent intent = new Intent(
                    MaidListActivity.this,
                    WorkerDetailActivity.class
            );
            intent.putExtra("workerId", workerId);
            startActivity(intent);
        });

        workersRef = FirebaseDatabase.getInstance()
                .getReference("workers");

        // 🔹 Get user location (IMPROVED SAFETY)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {

            String userKey = user.getEmail().replace(".", "_");

            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userKey)
                    .child("location")
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(
                                        @NonNull DataSnapshot snapshot) {
                                    myLocation = snapshot.getValue(String.class);
                                    loadWorkers(false);
                                }

                                @Override
                                public void onCancelled(
                                        @NonNull DatabaseError error) {}
                            });
        } else {
            loadWorkers(false);
        }

        rbAll.setOnCheckedChangeListener(
                (b, checked) -> {
                    if (checked) loadWorkers(false);
                });

        rbMyLocation.setOnCheckedChangeListener(
                (b, checked) -> {
                    if (checked) loadWorkers(true);
                });
    }
    private void loadWorkers(boolean filterLocation) {

        workersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        workerList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Worker worker = ds.getValue(Worker.class);
                            if (worker == null) continue;

                            if (!"Maid"
                                    .equalsIgnoreCase(worker.profession))
                                continue;
                            if (filterLocation &&
                                    myLocation != null &&
                                    worker.location != null &&
                                    !worker.location.equalsIgnoreCase(myLocation))
                                continue;

                            workerList.add(worker);
                        }

                        adapter.notifyDataSetChanged();

                        if (workerList.isEmpty()) {
                            Toast.makeText(
                                    MaidListActivity.this,
                                    "No maids found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {}
                });
    }
}
