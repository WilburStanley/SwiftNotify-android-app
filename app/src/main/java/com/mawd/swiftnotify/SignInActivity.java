package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private AppCompatImageButton go_back_btn;
    private AppCompatButton login_btn, register_btn;
    private EditText logInEmail, logInPassword;

    //Firebase objects
    private FirebaseAuth auth;
    private DatabaseReference reference;

    private void findReferences() {
        go_back_btn = findViewById(R.id.go_back_btn);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        logInEmail = findViewById(R.id.logInEmail);
        logInPassword = findViewById(R.id.logInPassword);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
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
            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Input all fields.");
            } else if (!isValidEmail(email)) {
                showMessage("Invalid email format.");
            } else {
                signInUser(email, password);
            }
        });
    }

    private void signInUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            addTokenToDatabase();
                            startActivity(new Intent(SignInActivity.this, MainPage.class));
                            addVerify(user, true);
                            showMessage("Welcome.");
                        } else {
                            sendEmailVerification();
                            assert user != null;
                            addVerify(user, false);
                            showMessage("Verify your email first.");
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        showMessage("Authentication failed.");
                    }
                });
    }

    public void addTokenToDatabase() {
        FirebaseUser user = auth.getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                showMessage("Error");
                return;
            }
            String token = task.getResult();
            checkExistingToken(token, reference, user);
        });
    }

    private void sendEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showMessage("We sent you an email, please check.");
                        } else {
                            Log.e("Error", "Error sending the email.");
                        }
                    });
        }
    }

    private void addVerify(FirebaseUser user, boolean verified) {
        HashMap<String, Object> verifyMap = new HashMap<>();
        verifyMap.put("verified", verified);
        reference.child(user.getUid()).updateChildren(verifyMap);
    }

    private void showMessage(String msg) {
        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkExistingToken(String token, DatabaseReference ref, FirebaseUser user) {
        String path = "deviceToken";
        ref.orderByChild(path).equalTo(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    HashMap<String, Object> tokenMap = new HashMap<>();
                    tokenMap.put(path, token);
                    reference.child(user.getUid()).updateChildren(tokenMap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Success", "Token added successfully.");
                                } else {
                                    Log.d("Failed", "Failed to add token: " + task.getException().getMessage());
                                }
                            });
                } else {
                    Log.d("Info", "Token already exists in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", "Database query cancelled");
            }
        });
    }
}