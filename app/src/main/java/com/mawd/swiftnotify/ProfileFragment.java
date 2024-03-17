package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProfileFragment extends Fragment {
    AppCompatButton settingsBtn;
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
            startActivity(new Intent(getContext(), GetStarted.class));
            requireActivity().finish();
        });
        return view;
    }

}