package com.example.handymen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText etUserName, etUserEmail, etUserPhone, etUserAddress,
            etUserPassword, etUserConfirmPassword;
    private CheckBox cbUserLength, cbUserUpperLower, cbUserNumber, cbUserSpecial;
    private Button btnUserSignUp;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Bind views
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserPhone = findViewById(R.id.etUserPhone);
        etUserAddress = findViewById(R.id.etUserAddress);
        etUserPassword = findViewById(R.id.etUserPassword);
        etUserConfirmPassword = findViewById(R.id.etUserConfirmPassword);

        cbUserLength = findViewById(R.id.cbUserLength);
        cbUserUpperLower = findViewById(R.id.cbUserUpperLower);
        cbUserNumber = findViewById(R.id.cbUserNumber);
        cbUserSpecial = findViewById(R.id.cbUserSpecial);

        btnUserSignUp = findViewById(R.id.btnUserSignUp);

        // Password validation
        etUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                cbUserLength.setChecked(password.length() >= 8);
                cbUserUpperLower.setChecked(password.matches(".*[a-z].*") && password.matches(".*[A-Z].*"));
                cbUserNumber.setChecked(password.matches(".*\\d.*"));
                cbUserSpecial.setChecked(password.matches(".*[@#$%^&+=!].*"));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sign up click
        btnUserSignUp.setOnClickListener(v -> {
            if (validateUser()) {
                signUpUser();
            }
        });
    }

    // Validate all fields
    private boolean validateUser() {
        String password = etUserPassword.getText().toString().trim();
        String confirm = etUserConfirmPassword.getText().toString().trim();

        if (etUserName.getText().toString().trim().isEmpty() ||
                etUserEmail.getText().toString().trim().isEmpty() ||
                etUserPhone.getText().toString().trim().isEmpty() ||
                etUserAddress.getText().toString().trim().isEmpty() ||
                password.isEmpty() || confirm.isEmpty()) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(cbUserLength.isChecked() && cbUserUpperLower.isChecked()
                && cbUserNumber.isChecked() && cbUserSpecial.isChecked())) {
            Toast.makeText(this, "Password does not meet requirements", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Sign up user with Firebase Auth and save in Realtime Database using email as key
    private void signUpUser() {
        String name = etUserName.getText().toString().trim();
        String email = etUserEmail.getText().toString().trim();
        String phone = etUserPhone.getText().toString().trim();
        String address = etUserAddress.getText().toString().trim();
        String password = etUserPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {

                            // Replace "." with "_" for Firebase keys
                            String emailKey = email.replace(".", "_");

                            // Create user object
                            User user = new User(name, email, phone, address);

                            // Save in database under "users/emailKey"
                            usersRef.child(emailKey).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(UserSignUpActivity.this,
                                                    "User registered successfully",
                                                    Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(UserSignUpActivity.this, UserLoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(UserSignUpActivity.this,
                                                    "Database error: " + dbTask.getException(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    } else {
                        Toast.makeText(UserSignUpActivity.this,
                                "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
