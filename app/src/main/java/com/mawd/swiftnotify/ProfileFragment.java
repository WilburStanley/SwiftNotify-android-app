package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mawd.swiftnotify.models.User;

public class ProfileFragment extends Fragment {
    private AppCompatButton settingsBtn;
    private TextView username;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        auth = FirebaseAuth.getInstance();
        setCurrentUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        settingsBtn = view.findViewById(R.id.settingsBtn);

        settingsBtn.setOnClickListener(v -> {
            View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.settings_dialog_layout, null);

            SwitchCompat vibrator_switch = bottomSheetView.findViewById(R.id.vibrator_switch);
            SwitchCompat notification_switch = bottomSheetView.findViewById(R.id.notification_switch);

            vibrator_switch.setOnCheckedChangeListener((btnView, isChecked) -> {
                if (isChecked) {
                    Toast.makeText(getContext(), "Vibration enabled", Toast.LENGTH_SHORT).show();
                } else {
                    notification_switch.setChecked(true);
                    Toast.makeText(getContext(), "Vibration disabled", Toast.LENGTH_SHORT).show();
                }
            });

            notification_switch.setOnCheckedChangeListener((btnView, isChecked) -> {
                if (isChecked) {
                    Toast.makeText(getContext(), "Notification enabled", Toast.LENGTH_SHORT).show();
                } else {
                    vibrator_switch.setChecked(true);
                    Toast.makeText(getContext(), "Notification disabled", Toast.LENGTH_SHORT).show();
                }
            });

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

        });

        AppCompatButton log_out_btn = view.findViewById(R.id.log_out_btn);

        log_out_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), GetStarted.class));
            requireActivity().finish();
        });
        username = view.findViewById(R.id.username);
        return view;
    }

    private void setCurrentUsername() {
        FirebaseUser currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        DataSnapshot snapshot = task.getResult();
                        String name = String.valueOf(snapshot.child("fullName").getValue());
                        username.setText(name);
                    }else {
                        Toast.makeText(getContext(), "User doesn't exists.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Failed to read.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}