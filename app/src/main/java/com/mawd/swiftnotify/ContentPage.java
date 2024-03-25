package com.mawd.swiftnotify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
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
    TextView teacherName, teacherAvailability;
    AppCompatButton beepBtn;
    ActivityResultLauncher<ScanOptions> barLauncher;
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

        teacherName = findViewById(R.id.teacherName);
        teacherAvailability = findViewById(R.id.availabilityStatus);

        beepBtn = findViewById(R.id.beepBtn);

        String teacher_name = getIntent().getStringExtra("TEACHER_NAME");
        String teacher_availability = getIntent().getStringExtra("TEACHER_AVAILABILITY");
        String teacherToken = getIntent().getStringExtra("teacherToken");

        teacherName.setText(teacher_name);

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

        go_back_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainPage.class));
        });

        beepBtn.setOnClickListener(v -> {
            if (beepBtnAvailable) {
                scanCode();
            } else {
                Toast.makeText(this, "User not available", Toast.LENGTH_SHORT).show();
            }
        });

        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Result")
                        .setMessage(result.getContents())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();*/
                FcmNotificationsSender sender = new FcmNotificationsSender(teacherToken, "Swift Notify", result.getContents(), getApplicationContext(),  ContentPage.this);
                sender.SendNotifications();
            }
        });


    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on")
                .setBeepEnabled(true)
                .setOrientationLocked(true)
                .setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
}