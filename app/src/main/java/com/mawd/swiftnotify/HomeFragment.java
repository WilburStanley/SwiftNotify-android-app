package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mawd.swiftnotify.models.User;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SelectListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    TeacherAdapter teacherAdapter;
    ArrayList<User> teacherList;
    User user;
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        TextView availabilityStatus = view.findViewById(R.id.availabilityStatus);

        // By default availability status must be set to affirmative
        availabilityStatus.setText(R.string.affirmative);

        View affirmativeBtn = view.findViewById(R.id.affirmative_button);
        affirmativeBtn.setClickable(true);
        affirmativeBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Status Changed", Toast.LENGTH_SHORT).show();
            availabilityStatus.setText(R.string.affirmative);
        });

        View negativeBtn = view.findViewById(R.id.negative_button);
        negativeBtn.setClickable(true);
        negativeBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Status Changed", Toast.LENGTH_SHORT).show();
            availabilityStatus.setText(R.string.negative);
        });

        LinearLayout student_ui = view.findViewById(R.id.student_ui);
        LinearLayout teacher_ui = view.findViewById(R.id.teacher_ui);

        FetchUserData fetchUserData = new FetchUserData();

        fetchUserData.fetchUserStatus(new FetchUserData.FetchUserStatusCallback() {
            @Override
            public void onSuccess(String status) {
                if (status.equals("Teacher")) {
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
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        teacherList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(getContext(), teacherList, this);
        recyclerView.setAdapter(teacherAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
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
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        User clickedUser = teacherList.get(position);

        Intent intent = new Intent(getContext(), ContentPage.class);

        intent.putExtra("TEACHER_NAME", clickedUser.getFullName());

        boolean availability_value = clickedUser.isTeacherAvailable();
        intent.putExtra("TEACHER_AVAILABILITY", String.valueOf(availability_value));

        startActivity(intent);
    }
}