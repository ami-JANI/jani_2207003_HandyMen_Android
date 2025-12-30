package com.example.handymen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WorkerSignUpActivity extends AppCompatActivity {

    private EditText etWorkerName, etWorkerEmail, etWorkerPhone, etWorkerExperience,
            etWorkerRate, etWorkerAddress, etWorkerPassword, etWorkerConfirmPassword;
    private Spinner spinnerProfession;
    private CheckBox cbLength, cbUpperLower, cbNumber, cbSpecial;
    private Button btnWorkerSignUp;

    private FirebaseAuth mAuth;
    private DatabaseReference workersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_signup);

        mAuth = FirebaseAuth.getInstance();
        workersRef = FirebaseDatabase.getInstance().getReference("workers");

        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkerEmail = findViewById(R.id.etWorkerEmail);
        etWorkerPhone = findViewById(R.id.etWorkerPhone);
        etWorkerExperience = findViewById(R.id.etWorkerExperience);
        etWorkerRate = findViewById(R.id.etWorkerRate);
        etWorkerAddress = findViewById(R.id.etWorkerAddress);
        etWorkerPassword = findViewById(R.id.etWorkerPassword);
        etWorkerConfirmPassword = findViewById(R.id.etWorkerConfirmPassword);

        spinnerProfession = findViewById(R.id.spinnerProfession);

        cbLength = findViewById(R.id.cbLength);
        cbUpperLower = findViewById(R.id.cbUpperLower);
        cbNumber = findViewById(R.id.cbNumber);
        cbSpecial = findViewById(R.id.cbSpecial);

        btnWorkerSignUp = findViewById(R.id.btnWorkerSignUp);

        String[] professions = {"Electrician", "Painter", "Maid", "Plumber", "Mason", "Internet Provider"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, professions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfession.setAdapter(adapter);

        etWorkerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                cbLength.setChecked(password.length() >= 8);
                cbUpperLower.setChecked(password.matches(".*[a-z].*") && password.matches(".*[A-Z].*"));
                cbNumber.setChecked(password.matches(".*\\d.*"));
                cbSpecial.setChecked(password.matches(".*[@#$%^&+=!].*"));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnWorkerSignUp.setOnClickListener(v -> {
            if (validateWorker()) {
                signUpWorker();
            }
        });
    }

    private boolean validateWorker() {
        String password = etWorkerPassword.getText().toString();
        String confirm = etWorkerConfirmPassword.getText().toString();

        if (etWorkerName.getText().toString().isEmpty() ||
                etWorkerEmail.getText().toString().isEmpty() ||
                etWorkerPhone.getText().toString().isEmpty() ||
                etWorkerExperience.getText().toString().isEmpty() ||
                etWorkerRate.getText().toString().isEmpty() ||
                etWorkerAddress.getText().toString().isEmpty() ||
                password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(cbLength.isChecked() && cbUpperLower.isChecked() && cbNumber.isChecked() && cbSpecial.isChecked())) {
            Toast.makeText(this, "Password does not meet all requirements", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void signUpWorker() {
        String name = etWorkerName.getText().toString().trim();
        String email = etWorkerEmail.getText().toString().trim();
        String phone = etWorkerPhone.getText().toString().trim();
        String profession = spinnerProfession.getSelectedItem().toString();
        String experience = etWorkerExperience.getText().toString().trim();
        String rate = etWorkerRate.getText().toString().trim();
        String location = etWorkerAddress.getText().toString().trim();
        String password = etWorkerPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String workerId = email.replace(".", "_");

                            Worker worker = new Worker(
                                    name, email, phone, profession, experience, rate, location
                            );

                            workersRef.child(workerId).setValue(worker)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, WorkerLoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Database error: " + dbTask.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
