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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserLoginActivity extends AppCompatActivity {

    TextView tvSignUp;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        tvSignUp = findViewById(R.id.tvUserSignUp);
        btnLogin = findViewById(R.id.btnUserLogin);

        String text = "Are you a new user? Sign Up";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(
                        UserLoginActivity.this,
                        UserSignUpActivity.class
                ));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        int start = text.indexOf("Sign Up");
        int end = start + "Sign Up".length();
        spannableString.setSpan(
                clickableSpan,
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvSignUp.setText(spannableString);
        tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    UserLoginActivity.this,
                    UserDashboardActivity.class
            );
            startActivity(intent);
            finish();
        });
    }
}
