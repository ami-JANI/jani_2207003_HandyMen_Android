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

public class ElectricianListActivity extends AppCompatActivity {

    RecyclerView rvWorkers;
    WorkerAdapter adapter;
    ArrayList<Worker> list;

    RadioButton rbAll, rbMyLocation;
    DatabaseReference workersRef;

    String myLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrician_list);

        rvWorkers = findViewById(R.id.rvWorkers);
        rbAll = findViewById(R.id.rbAll);
        rbMyLocation = findViewById(R.id.rbMyLocation);

        rvWorkers.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new WorkerAdapter(list);
        rvWorkers.setAdapter(adapter);

        // ✅ CLICK HANDLER (ONLY ONCE)
        adapter.setOnItemClickListener(worker -> {
            Intent intent = new Intent(this, WorkerDetailActivity.class);
            intent.putExtra("workerEmail", worker.email);

            startActivity(intent);
        });

        workersRef = FirebaseDatabase.getInstance()
                .getReference("workers");

        // 🔹 Get user location
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userKey = user.getEmail().replace(".", "_");
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userKey)
                    .child("location")
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(
                                        @NonNull DataSnapshot snapshot) {
                                    myLocation =
                                            snapshot.getValue(String.class);
                                    loadWorkers(false);
                                }

                                @Override
                                public void onCancelled(
                                        @NonNull DatabaseError error) {}
                            });
        }

        rbAll.setOnCheckedChangeListener(
                (b, checked) -> { if (checked) loadWorkers(false); });

        rbMyLocation.setOnCheckedChangeListener(
                (b, checked) -> { if (checked) loadWorkers(true); });
    }

    private void loadWorkers(boolean filterLocation) {
        workersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        list.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Worker w = ds.getValue(Worker.class);
                            if (w == null) continue;

                            if (!"Electrician"
                                    .equalsIgnoreCase(w.profession))
                                continue;

                            if (filterLocation &&
                                    myLocation != null &&
                                    w.location != null &&
                                    !w.location.equalsIgnoreCase(myLocation))
                                continue;

                            list.add(w);
                        }

                        adapter.notifyDataSetChanged();

                        if (list.isEmpty()) {
                            Toast.makeText(
                                    ElectricianListActivity.this,
                                    "No electricians found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {}
                });
    }
}
