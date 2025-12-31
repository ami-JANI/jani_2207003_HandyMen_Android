package com.example.handymen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class UserDashboardActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton menuBtn;

    Button btnElectrician, btnPlumber, btnPainter, btnMason, btnMaid, btnInternetProvider;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, UserLoginActivity.class));
            finish();
            return;
        }

        // Views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuBtn = findViewById(R.id.menuBtn);

        btnElectrician = findViewById(R.id.btnElectrician);
        btnPlumber = findViewById(R.id.btnPlumber);
        btnPainter = findViewById(R.id.btnPainter);
        btnMason = findViewById(R.id.btnMason);
        btnMaid = findViewById(R.id.btnMaid);
        btnInternetProvider = findViewById(R.id.btnInternetProvider);

        // Menu button
        menuBtn.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END)
        );

        // Drawer menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menuProfile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }  else if(id == R.id.menuOngoing) {
                startActivity(new Intent(this, OngoingWorkActivity.class));
            }  else if(id == R.id.menuRequests) {
                startActivity(new Intent(this, MyRequestsActivity.class));
            }else if (id == R.id.menuLogout) {
                logout();
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // 🔹 Worker buttons → ONLY open activities
        btnElectrician.setOnClickListener(v ->
                startActivity(new Intent(this, ElectricianListActivity.class)));

        btnPlumber.setOnClickListener(v ->
                startActivity(new Intent(this, PlumberListActivity.class)));

        btnPainter.setOnClickListener(v ->
                startActivity(new Intent(this, PainterListActivity.class)));

        btnMason.setOnClickListener(v ->
                startActivity(new Intent(this, MasonListActivity.class)));

        btnMaid.setOnClickListener(v ->
                startActivity(new Intent(this, MaidListActivity.class)));

        btnInternetProvider.setOnClickListener(v ->
                startActivity(new Intent(this, InternetListActivity.class)));
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, UserLoginActivity.class));
        finish();
    }
}
