package com.mawd.swiftnotify;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
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
        return view;
    }
}