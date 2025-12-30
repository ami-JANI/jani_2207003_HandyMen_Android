package com.example.handymen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

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
public class ElectricianListActivity extends AppCompatActivity {

    RecyclerView rv;
    WorkerAdapter adapter;
    ArrayList<Worker> list;

    RadioButton rbAll, rbMyLocation;
    DatabaseReference workersRef;

    String myLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrician_list);

        rv = findViewById(R.id.rvWorkers);
        rbAll = findViewById(R.id.rbAll);
        rbMyLocation = findViewById(R.id.rbMyLocation);

        rv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new WorkerAdapter(list);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(worker -> {
            Intent intent = new Intent(ElectricianListActivity.this, WorkerDetailActivity.class);
            // Replace "." with "_" for Firebase-safe key
            String emailKey = worker.email.replace(".", "_");
            intent.putExtra("workerEmailKey", emailKey);
            startActivity(intent);
        });
        workersRef = FirebaseDatabase.getInstance().getReference("workers");

        // Get user location
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String key = user.getEmail().replace(".", "_");
            FirebaseDatabase.getInstance().getReference("users")
                    .child(key)
                    .child("location")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            myLocation = s.getValue(String.class);
                            loadWorkers(false);
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {}
                    });
        }

        rbAll.setOnCheckedChangeListener((b, checked) -> {
            if (checked) loadWorkers(false);
        });

        rbMyLocation.setOnCheckedChangeListener((b, checked) -> {
            if (checked) loadWorkers(true);
        });
    }

    private void loadWorkers(boolean filterLocation) {
        workersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                list.clear();

                for (DataSnapshot ds : snap.getChildren()) {
                    Worker w = ds.getValue(Worker.class);
                    if (w == null) continue;

                    if (!"Electrician".equalsIgnoreCase(w.profession)) continue;

                    if (filterLocation && !String.valueOf(w.location).equalsIgnoreCase(myLocation))
                        continue;


                    list.add(w);
                }

                adapter.notifyDataSetChanged();

                if (list.isEmpty()) {
                    Toast.makeText(ElectricianListActivity.this,
                            "No electricians found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
