package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mawd.swiftnotify.models.User;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private AppCompatImageButton go_back_btn;
    private AppCompatButton sign_in_btn, registerAccount;
    private Spinner genderSpinner, statusSpinner;
    private EditText registerFullName, registerAge, registerEmail, registerPassword, retypePassword;
    private String selectedGender, selectedStatus;

    //Firebase object
    private FirebaseAuth auth;
    private DatabaseReference db;

    private void findReferences() {
        go_back_btn = findViewById(R.id.go_back_btn);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        registerAccount = findViewById(R.id.registerAccount);
        genderSpinner = findViewById(R.id.gender_spinner);
        statusSpinner = findViewById(R.id.status_spinner);
        registerFullName = findViewById(R.id.registerFullName);
        registerAge = findViewById(R.id.registerAge);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        retypePassword = findViewById(R.id.retypePassword);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void clearInputs() {
        registerFullName.setText("");
        registerAge.setText("");
        registerEmail.setText("");
        registerPassword.setText("");
        retypePassword.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        findReferences();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        go_back_btn.setOnClickListener(v -> {
            String previous_page = getIntent().getStringExtra("PREVIOUS_PAGE");

            if (Objects.equals(previous_page, "SIGN_IN_PAGE")) {
                startActivity(new Intent(this, SignInActivity.class));
            } else {
                startActivity(new Intent(this, GetStarted.class));
            }
            finish();
        });


        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_options, R.layout.custom_snipper_item);
        genderAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
                Toast.makeText(RegisterActivity.this, "Selected Gender: " + selectedGender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // DO NOTHING (FOR NOW)
            }
        });


        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_options, R.layout.custom_snipper_item);
        statusAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = parent.getItemAtPosition(position).toString();
                Toast.makeText(RegisterActivity.this, "Selected Status: " + selectedStatus, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // DO NOTHING (FOR NOW)
            }
        });

        sign_in_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class).putExtra("PREVIOUS_PAGE", "REGISTER_PAGE"));
            finish();
        });

        registerAccount.setOnClickListener(v -> {
            String fullName = registerFullName.getText().toString().trim();
            String age = registerAge.getText().toString().trim();
            String email = registerEmail.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();
            String retypePass = retypePassword.getText().toString().trim();

            if (fullName.isEmpty() || age.isEmpty() || selectedGender.isEmpty() || selectedStatus.isEmpty() || email.isEmpty() || password.isEmpty() || retypePass.isEmpty()) {
                Toast.makeText(this, "Input all fields.", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "Password should be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(retypePass)) {
                Toast.makeText(this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
            } else {
                int convertedAge = Integer.parseInt(age);
                User user = new User(fullName, convertedAge, selectedGender, selectedStatus, email);
                registerUser(user, password);
                clearInputs();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            //if may current user
            startActivity(new Intent(this, MainPage.class));
        }
    }

    private void registerUser(User user, String password) {
        auth.createUserWithEmailAndPassword(user.getUserEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                            FirebaseUser currentUser = auth.getCurrentUser();
                            assert currentUser != null;
                            writeUser(currentUser.getUid(), user);

                            startActivity(new Intent(RegisterActivity.this, MainPage.class));
                            Toast.makeText(RegisterActivity.this, "Welcome.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeUser(String userId, User user) {
        if (user.getUserStatus() != null && user.getUserStatus().equalsIgnoreCase("Teacher")) {
            user.setIsTeacherAvailable(true);
        }

        db.child("users").child(userId).setValue(user);
    }
}