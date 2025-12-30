package com.example.handymen;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ElectricianActivity extends AppCompatActivity {

    Spinner filterSpinner;
    RecyclerView recyclerView;
    WorkerAdapter adapter;
    ArrayList<Worker> list = new ArrayList<>();

    DatabaseReference workersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrician);

        filterSpinner = findViewById(R.id.filterSpinner);
        recyclerView = findViewById(R.id.recyclerWorkers);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkerAdapter(list);
        recyclerView.setAdapter(adapter);

        workersRef = FirebaseDatabase.getInstance()
                .getReference("workers");


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"My Location", "All Workers"}
        );
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        loadWorkers(position == 0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );
    }

    private void loadWorkers(boolean onlyMyLocation) {

        list.clear();

        workersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Worker w = ds.getValue(Worker.class);

                            if (w == null) continue;

                            // Profession filter
                            if (!"Electrician".equalsIgnoreCase(w.profession))
                                continue;

                            // Location filter
                            if (onlyMyLocation) {
                                if (!w.location.equalsIgnoreCase(
                                        UserSession.getUserLocation()))
                                    continue;
                            }

                            list.add(w);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                }
        );
    }
}
