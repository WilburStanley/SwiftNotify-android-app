package com.mawd.swiftnotify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mawd.swiftnotify.models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements SelectListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean teacherAvailable = false;
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    TeacherAdapter teacherAdapter;
    ArrayList<User> teacherList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        auth = FirebaseAuth.getInstance();

        FetchUserData fetchUserData = new FetchUserData();

        TextView availabilityStatus = view.findViewById(R.id.availabilityStatus);

        fetchUserData.fetchTeacherAvailability(isTeacherAvailable -> {
            if (isTeacherAvailable) {
                availabilityStatus.setText(R.string.affirmative);
            } else {
                availabilityStatus.setText(R.string.negative);
            }
        });

        View affirmativeBtn = view.findViewById(R.id.affirmative_button);
        affirmativeBtn.setClickable(true);
        affirmativeBtn.setOnClickListener(v -> {
            teacherAvailable = true;
            updateTeacherAvailability(true);
            availabilityStatus.setText(teacherAvailable ? "Affirmative" : "Error");
        });

        View negativeBtn = view.findViewById(R.id.negative_button);
        negativeBtn.setClickable(true);
        negativeBtn.setOnClickListener(v -> {
            teacherAvailable = false;
            updateTeacherAvailability(false);
            availabilityStatus.setText(!teacherAvailable ? "Negative" : "Error");
        });

        LinearLayout student_ui = view.findViewById(R.id.student_ui);
        LinearLayout teacher_ui = view.findViewById(R.id.teacher_ui);

        fetchUserData.fetchUserStatus(new FetchUserData.FetchUserStatusCallback() {
            @Override
            public void onSuccess(String status) {
                if (status.equals("Teacher")) {
                    updateTeacherAvailability(true);
                    availabilityStatus.setText(R.string.affirmative);
                    teacher_ui.setVisibility(View.VISIBLE);
                    student_ui.setVisibility(View.GONE);
                } else if (status.equals("Student")) {
                    teacher_ui.setVisibility(View.GONE);
                    student_ui.setVisibility(View.VISIBLE);
                } else {
                    teacher_ui.setVisibility(View.GONE);
                    student_ui.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure in retrieving user status
                e.printStackTrace(); // Print stack trace for debugging
            }
        });

        recyclerView = view.findViewById(R.id.teacherList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        teacherList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(getContext(), teacherList, this);
        recyclerView.setAdapter(teacherAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User teacher = dataSnapshot.getValue(User.class);

                    assert teacher != null;
                    String userStatus = teacher.getUserStatus();
                    if (userStatus != null && userStatus.equalsIgnoreCase("Teacher")) {
                        Log.d("Teacher_Added:", "Teacher added to teacherList: " + teacher.getFullName());
                        teacherList.add(teacher);
                    }
                }
                teacherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        // FETCH AND STORE TEACHER'S VALUES WHILE ONLINE AND STORE IN SQLITE DB FOR OFFLINE VIEWING
        final SwiftNotifyUtils swiftNotifyUtils = new SwiftNotifyUtils();
        final DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        swiftNotifyUtils.fetchAndStoreTeachers(databaseHelper);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        User clickedUser = teacherList.get(position);

        Intent intent = new Intent(getContext(), ContentPage.class);

        intent.putExtra("TEACHER_NAME", clickedUser.getFullName());

        boolean availability_value = clickedUser.isTeacherAvailable();
        intent.putExtra("TEACHER_AVAILABILITY", String.valueOf(availability_value));
        intent.putExtra("TEACHER_GENDER", clickedUser.getUserGender());
        intent.putExtra("teacherToken", clickedUser.getDeviceToken());
        intent.putExtra("PREVIOUS_LOCATION", "HOME_FRAGMENT");
        startActivity(intent);
        requireActivity().finish();
    }

    private void updateTeacherAvailability(boolean teacherAvailable) {
        FirebaseUser teacher = auth.getCurrentUser();
        HashMap<String, Object> availabilityMap = new HashMap<>();
        availabilityMap.put("teacherAvailable", teacherAvailable);
        assert teacher != null;
        databaseReference.child(teacher.getUid()).updateChildren(availabilityMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Success", "Availability Updated");
                    } else {
                        Log.d("Failed", "Availability Failed to Update");
                    }
                });
    }

}