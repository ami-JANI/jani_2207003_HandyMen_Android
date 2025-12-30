package com.example.handymen;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
public class WorkerListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WorkerAdapter workerAdapter;
    List<Worker> workerList;

    DatabaseReference workersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);

        // 1️⃣ Initialize RecyclerView
        recyclerView = findViewById(R.id.rvWorkers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workerList = new ArrayList<>();
        workerAdapter = new WorkerAdapter(workerList);
        recyclerView.setAdapter(workerAdapter);

        // 2️⃣ Firebase reference
        workersRef = FirebaseDatabase.getInstance().getReference("workers");

        // 3️⃣ Fetch workers AFTER adapter is ready
        loadWorkers();
    }

    private void loadWorkers() {
        workersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workerList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Worker worker = ds.getValue(Worker.class);
                    if (worker != null) {
                        workerList.add(worker);
                    }
                }

                workerAdapter.notifyDataSetChanged();

                if (workerList.isEmpty()) {
                    Toast.makeText(WorkerListActivity.this,
                            "No workers found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorkerListActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
