package com.example.handymen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WorkerLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.workerEmail);
        etPassword = findViewById(R.id.workerPassword);
        btnLogin = findViewById(R.id.btnWorkerLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(v -> loginWorker());

        setupSignUpLink();
    }

    private void loginWorker() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(
                                    WorkerLoginActivity.this,
                                    WorkerDashboardActivity.class
                            );
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(
                                WorkerLoginActivity.this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setupSignUpLink() {
        String text = "Are you a new user? Sign Up";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(
                        new Intent(
                                WorkerLoginActivity.this,
                                WorkerSignUpActivity.class
                        )
                );
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        int start = text.indexOf("Sign Up");
        int end = start + "Sign Up".length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignUp.setText(spannableString);
        tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
