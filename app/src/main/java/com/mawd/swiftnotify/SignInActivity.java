package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    AppCompatImageButton go_back_btn;
    AppCompatButton login_btn, register_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        go_back_btn = findViewById(R.id.go_back_btn);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);

        go_back_btn.setOnClickListener(v -> {
            String previous_page = getIntent().getStringExtra("PREVIOUS_PAGE");

            if (Objects.equals(previous_page, "GET_STARTED_PAGE")){
                startActivity(new Intent(this, GetStarted.class));
            } else if (Objects.equals(previous_page, "REGISTER_PAGE")){
                startActivity(new Intent(this, RegisterActivity.class));
            } else {
                startActivity(new Intent(this, GetStarted.class));
            }
            finish();
        });

        register_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class).putExtra("PREVIOUS_PAGE", "SIGN_IN_PAGE"));
            finish();
        });

        login_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentPage.class));
            finish();
        });
    }
}