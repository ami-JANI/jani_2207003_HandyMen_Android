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

public class UserSignUpActivity extends AppCompatActivity {

    private EditText etUserName, etUserEmail, etUserPhone, etUserAddress, etUserPassword, etUserConfirmPassword;
    private CheckBox cbUserLength, cbUserUpperLower, cbUserNumber, cbUserSpecial;
    private Button btnUserSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

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

        etUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                cbUserLength.setChecked(password.length() >= 8);
                cbUserUpperLower.setChecked(password.matches(".*[a-z].*") && password.matches(".*[A-Z].*"));
                cbUserNumber.setChecked(password.matches(".*\\d.*"));
                cbUserSpecial.setChecked(password.matches(".*[@#$%^&+=!].*"));
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });


        btnUserSignUp.setOnClickListener(v -> {
            if (validateUser()) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserSignUpActivity.this, UserLoginActivity.class));
                finish();
            }
        });
    }

    private boolean validateUser() {
        String password = etUserPassword.getText().toString();
        String confirm = etUserConfirmPassword.getText().toString();

        if (etUserName.getText().toString().isEmpty() ||
                etUserEmail.getText().toString().isEmpty() ||
                etUserPhone.getText().toString().isEmpty() ||
                etUserAddress.getText().toString().isEmpty() ||
                password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(cbUserLength.isChecked() && cbUserUpperLower.isChecked() && cbUserNumber.isChecked() && cbUserSpecial.isChecked())) {
            Toast.makeText(this, "Password does not meet all requirements", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
