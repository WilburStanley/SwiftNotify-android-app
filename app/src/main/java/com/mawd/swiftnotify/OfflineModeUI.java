package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mawd.swiftnotify.models.User;

import java.util.ArrayList;

public class OfflineModeUI extends AppCompatActivity {
    AppCompatImageButton go_back_btn;
    RecyclerView recyclerView;
    TeacherAdapter teacherAdapter;
    DatabaseHelper databaseHelper;
    ArrayList<User> teacherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline_mode_ui);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        go_back_btn = findViewById(R.id.go_back_btn);
        recyclerView = findViewById(R.id.teacherList);

        go_back_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        ArrayList<User> teachers = databaseHelper.getAllTeachers();
        teacherList = new ArrayList<>(teachers);

        teacherAdapter = new TeacherAdapter(this, teachers, position -> {
            User clickedUser = teacherList.get(position);

            Intent intent = new Intent(this, ContentPage.class);

            intent.putExtra("TEACHER_NAME", clickedUser.getFullName());

            boolean availability_value = clickedUser.isTeacherAvailable();
            intent.putExtra("TEACHER_AVAILABILITY", String.valueOf(availability_value));
            intent.putExtra("TEACHER_GENDER", clickedUser.getUserGender());
            intent.putExtra("teacherToken", clickedUser.getDeviceToken());
            intent.putExtra("PREVIOUS_LOCATION", "OFFLINE_MODE");
            startActivity(intent);
            finish();
        });

        recyclerView.setAdapter(teacherAdapter);
    }
}
