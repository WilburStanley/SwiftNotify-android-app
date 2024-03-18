package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private AppCompatImageButton go_back_btn;
    private AppCompatButton login_btn, register_btn;
    private EditText logInEmail, logInPassword;

    //Firebase objects
    private FirebaseAuth auth;

    private void findReferences() {
        go_back_btn = findViewById(R.id.go_back_btn);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        logInEmail = findViewById(R.id.logInEmail);
        logInPassword = findViewById(R.id.logInPassword);
        auth = FirebaseAuth.getInstance();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        findReferences();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        go_back_btn.setOnClickListener(v -> {
            String previous_page = getIntent().getStringExtra("PREVIOUS_PAGE");

            if (Objects.equals(previous_page, "GET_STARTED_PAGE")) {
                startActivity(new Intent(this, GetStarted.class));
            } else if (Objects.equals(previous_page, "REGISTER_PAGE")) {
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
            String email = logInEmail.getText().toString();
            String password = logInPassword.getText().toString();
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Input all fields.", Toast.LENGTH_SHORT).show();
            }else if(!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            }else {
                signInUser(email, password);
            }
        });
    }

    private void signInUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(SignInActivity.this, StudentPage.class));
                            Toast.makeText(SignInActivity.this, "Welcome.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}