package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.mawd.swiftnotify.models.FcmNotificationsSender;

public class ContentPage extends AppCompatActivity {
    AppCompatImageButton go_back_btn;
    TextView userName, teacherAvailability, studentAgeValue, studentGenderValue, studentSectionValue, studentAccountValue;
    AppCompatButton beepBtn;
    ActivityResultLauncher<ScanOptions> barLauncher;
    LinearLayout availabilityContainer, beeperContainer, studentCredentialsUi;
    private boolean beepBtnAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_content_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        go_back_btn = findViewById(R.id.go_back_btn);

        userName = findViewById(R.id.userName);
        teacherAvailability = findViewById(R.id.availabilityStatus);

        availabilityContainer = findViewById(R.id.availabilityContainer);
        beeperContainer = findViewById(R.id.beeperContainer);
        studentCredentialsUi = findViewById(R.id.studentCredentialsUi);

        beepBtn = findViewById(R.id.beepBtn);

        // History fragment values
        studentAgeValue = findViewById(R.id.studentAgeValue);
        studentGenderValue = findViewById(R.id.studentGenderValue);
        studentSectionValue = findViewById(R.id.studentSectionValue);
        studentAccountValue = findViewById(R.id.studentAccountValue);

        String previousLocations = getIntent().getStringExtra("PREVIOUS_LOCATION");

        // Home fragment intents
        String teacher_name = getIntent().getStringExtra("TEACHER_NAME");
        String teacher_availability = getIntent().getStringExtra("TEACHER_AVAILABILITY");
        String teacherGender = getIntent().getStringExtra("TEACHER_GENDER");
        String teacherToken = getIntent().getStringExtra("teacherToken");

        // History fragment intent
        String student_name = getIntent().getStringExtra("STUDENT_NAME");
        String student_age = getIntent().getStringExtra("STUDENT_AGE");
        String student_gender = getIntent().getStringExtra("STUDENT_GENDER");
        String student_section = getIntent().getStringExtra("STUDENT_SECTION");
        String student_account = getIntent().getStringExtra("STUDENT_ACCOUNT");

        assert previousLocations != null;
        if (previousLocations.equalsIgnoreCase("HOME_FRAGMENT")) {
            userName.setText(teacher_name);
            availabilityContainer.setVisibility(View.VISIBLE);
            beeperContainer.setVisibility(View.VISIBLE);
            studentCredentialsUi.setVisibility(View.GONE);

            assert teacher_availability != null;
            teacherCredentialsUi(teacher_availability);
        } else if (previousLocations.equalsIgnoreCase("HISTORY_FRAGMENT")) {
            studentCredentialsUi.setVisibility(View.VISIBLE);
            availabilityContainer.setVisibility(View.GONE);
            beeperContainer.setVisibility(View.GONE);

            userName.setText(student_name);
            studentAgeValue.setText(student_age);
            studentGenderValue.setText(student_gender);
            studentSectionValue.setText(student_section);
            studentAccountValue.setText(student_account);
        }

        go_back_btn.setOnClickListener(v -> {
            if (previousLocations.equalsIgnoreCase("HOME_FRAGMENT")) {
                startActivity(new Intent(this, MainPage.class));
            } else if (previousLocations.equalsIgnoreCase("HISTORY_FRAGMENT")) {
                Intent intent = new Intent(this, MainPage.class);
                intent.putExtra("NAVIGATE_TO_HISTORY", true);
                startActivity(intent);
            }
            finish();
        });

        beepBtn.setOnClickListener(v -> {
            if (beepBtnAvailable) {
                scanCode();
            } else {
                Toast.makeText(this, "User not available", Toast.LENGTH_SHORT).show();
            }
        });

        FetchUserData fetchUserData = new FetchUserData();

        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                fetchUserData.fetchUserEmailAndName(new FetchUserData.FetchUserEmailAndNameCallback() {
                    @Override
                    public void onSuccess(String userEmail, String userName) {
                        boolean isQrCodeValid = checkQrCodeValidity(result.getContents(), userEmail, userName);
                        if (isQrCodeValid) {
                            FcmNotificationsSender sender = new FcmNotificationsSender(
                                    teacherToken, // User FCM token
                                    result.getContents(), // Overall notification body
                                    getApplicationContext(), // Context
                                    ContentPage.this // Activity
                            );
                            sender.SendNotifications(true);
                            Toast.makeText(ContentPage.this, "Successfully Notified", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ContentPage.this, "Failed to Notify", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure, such as user not logged in or data not found
                        System.out.println("Failed to fetch user email and name: " + e.getMessage());
                    }
                });
            } else {
                Toast.makeText(this, "Failed to Notify", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void teacherCredentialsUi(String teacher_availability) {
        int color_green = ContextCompat.getColor(this, R.color.green);
        int color_red = ContextCompat.getColor(this, R.color.red);

        assert teacher_availability != null;
        if (teacher_availability.equalsIgnoreCase("true")) {
            teacherAvailability.setTextColor(color_green);
            teacherAvailability.setText(R.string.affirmative);
            beepBtnAvailable = true;

        } else if (teacher_availability.equalsIgnoreCase("false")) {
            teacherAvailability.setTextColor(color_red);
            teacherAvailability.setText(R.string.negative);
            beepBtnAvailable = false;
        }
    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on")
                .setBeepEnabled(true)
                .setOrientationLocked(true)
                .setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    public boolean checkQrCodeValidity(String credentials, String user_account, String user_name) {
        String[] fields = credentials.split("\n");

        String account = null;
        String name = null;

        for (String field : fields) {
            String[] parts = field.split(":");
            if (parts.length == 2) {
                String fieldName = parts[0].trim();
                String fieldValue = parts[1].trim();

                if (fieldName.equalsIgnoreCase("Account")) {
                    account = fieldValue;
                } else if (fieldName.equalsIgnoreCase("Name")) {
                    name = fieldValue;
                }
            }
        }
        if (name != null && user_account.equalsIgnoreCase(account)) {
            // Split the user name from the QR code credentials into individual words
            String[] userNameParts = user_name.split(" ");

            // Split the retrieved name from the database into individual words
            String[] dbNameParts = name.split(" ");

            // Check if any part of the user name matches any part of the retrieved name from the database
            for (String userNamePart : userNameParts) {
                for (String dbNamePart : dbNameParts) {
                    if (userNamePart.equalsIgnoreCase(dbNamePart)) {
                        return true; // If any part matches, return true
                    }
                }
            }
        }
        return false;
    }
}