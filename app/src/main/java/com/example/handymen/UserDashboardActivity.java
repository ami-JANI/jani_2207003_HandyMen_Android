package com.example.handymen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserDashboardActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        Button btnElectrician = findViewById(R.id.btnElectrician);
        Button btnPlumber = findViewById(R.id.btnPlumber);
        Button btnPainter = findViewById(R.id.btnPainter);
        Button btnMason = findViewById(R.id.btnMason);
        Button btnMaid = findViewById(R.id.btnMaid);
        Button btnInternet = findViewById(R.id.btnInternetProvider);

        btnElectrician.setOnClickListener(v -> openWorkerList("Electrician"));
        btnPlumber.setOnClickListener(v -> openWorkerList("Plumber"));
        btnPainter.setOnClickListener(v -> openWorkerList("Painter"));
        btnMason.setOnClickListener(v -> openWorkerList("Mason"));
        btnMaid.setOnClickListener(v -> openWorkerList("Maid"));
        btnInternet.setOnClickListener(v -> openWorkerList("Internet Provider"));

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        findViewById(R.id.menuBtn).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));

        navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.menuLogout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, UserLoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

    }

    private void openWorkerList(String profession) {
        Intent intent = new Intent(this, ElectricianActivity.class);
        intent.putExtra("profession", profession);
        startActivity(intent);
    }
}
