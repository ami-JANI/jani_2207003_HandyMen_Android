package com.example.handymen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkerDetailsActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvExperience, tvRate, tvLocation, tvProfession;
    Button btnCall, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        tvName = findViewById(R.id.tvName);
        tvProfession = findViewById(R.id.tvProfession);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvExperience = findViewById(R.id.tvExperience);
        tvRate = findViewById(R.id.tvRate);
        tvLocation = findViewById(R.id.tvLocation);

        btnCall = findViewById(R.id.btnCall);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String experience = intent.getStringExtra("experience");
        String rate = intent.getStringExtra("rate");
        String location = intent.getStringExtra("location");
        String profession = intent.getStringExtra("profession");

        tvName.setText(name);
        tvProfession.setText("Profession: " + profession);
        tvEmail.setText("Email: " + email);
        tvPhone.setText("Phone: " + phone);
        tvExperience.setText("Experience: " + experience + " years");
        tvRate.setText("Rate: " + rate);
        tvLocation.setText("Location: " + location);

        btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
