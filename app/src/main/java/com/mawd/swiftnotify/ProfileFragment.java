package com.mawd.swiftnotify;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {
    private AppCompatButton settingsBtn, personalDetailsBtn, changeUserEmailBtn;
    private TextView username;
    private EditText inputted_current_email, inputted_new_email, inputted_current_password, inputted_new_password;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private AlertDialog dialog;
    private LinearLayout change_credentials_settings, changeEmailContainer, changePasswordContainer;
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
        personalDetailsBtn = view.findViewById(R.id.personalDetailBtn);

        settingsBtn.setOnClickListener(v -> {
            View settingsBottomSheet = LayoutInflater.from(getContext()).inflate(R.layout.settings_dialog_layout, null);

            SwitchCompat vibrator_switch = settingsBottomSheet.findViewById(R.id.vibrator_switch);
            SwitchCompat notification_switch = settingsBottomSheet.findViewById(R.id.notification_switch);

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
            bottomSheetDialog.setContentView(settingsBottomSheet);
            bottomSheetDialog.show();

        });

        personalDetailsBtn.setOnClickListener(v -> {
            View profileBottomSheet = LayoutInflater.from(getContext()).inflate(R.layout.profile_dialog_layout, null);

            Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(profileBottomSheet);

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            }
            dialog.show();

            setReference(profileBottomSheet);

            AppCompatImageButton personalDetailExitBtn = profileBottomSheet.findViewById(R.id.personalDetailExitBtn);
            personalDetailExitBtn.setOnClickListener(exit -> dialog.dismiss());

            AppCompatButton changeEmailBtn = profileBottomSheet.findViewById(R.id.changeEmailBtn);
            AppCompatButton changePasswordBtn = profileBottomSheet.findViewById(R.id.changePasswordBtn);

            AppCompatButton changeUserEmailBtn = profileBottomSheet.findViewById(R.id.changeUserEmailBtn);

            inputted_current_email = profileBottomSheet.findViewById(R.id.inputted_current_email);
            inputted_new_email = profileBottomSheet.findViewById(R.id.inputted_new_email);

            changeEmailBtn.setOnClickListener(changeCredentials -> {
                checkAuthorization(isAuthorized -> {
                    if (isAuthorized) {
                        change_credentials_settings.setVisibility(View.GONE);
                        changeEmailContainer.setVisibility(View.VISIBLE);

                        changeUserEmailBtn.setOnClickListener(change -> {
                            String user_current_email = inputted_current_email.getText().toString().trim();
                            String new_email_value = inputted_new_email.getText().toString().trim();

                            if (!user_current_email.isEmpty() && !new_email_value.isEmpty()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user != null && user.getEmail() != null && user.getEmail().equalsIgnoreCase(user_current_email)) {
                                    changeEmail(new_email_value);
                                } else {
                                    Toast.makeText(getContext(), "Incorrect Email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Email fields cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            });

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

    interface AuthorizationCallback {
        void onAuthorizationResult(boolean isAuthorized);
    }

    private void checkAuthorization(AuthorizationCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.authorization_check_dialog, null);

        EditText inputted_password = dialogView.findViewById(R.id.inputted_password);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        AppCompatImageButton exitButton = dialogView.findViewById(R.id.authorization_check_exit_btn);
        exitButton.setOnClickListener(exit -> {
            alertDialog.dismiss();
        });

        AppCompatButton authorization_enter_btn = dialogView.findViewById(R.id.authorization_enter_btn);
        authorization_enter_btn.setOnClickListener(v -> {
            String inputted_password_value = inputted_password.getText().toString().trim();

            if (inputted_password_value.isEmpty()) {
                Toast.makeText(getContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), inputted_password_value);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            callback.onAuthorizationResult(true);
                            Toast.makeText(getContext(), "Authorization Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Authorization Denied: Incorrect Password", Toast.LENGTH_SHORT).show();
                            callback.onAuthorizationResult(false); // Authorization failed
                        }
                    });
        });
    }

    private void setReference(View view) {
        change_credentials_settings = view.findViewById(R.id.change_credentials_settings);
        changeEmailContainer = view.findViewById(R.id.changeEmailContainer);
        changePasswordContainer = view.findViewById(R.id.changePasswordContainer);

        inputted_current_password = view.findViewById(R.id.inputted_current_password);
        inputted_new_password = view.findViewById(R.id.inputted_new_password);

        changeUserEmailBtn = view.findViewById(R.id.changeUserEmailBtn);

        change_credentials_settings.setVisibility(View.VISIBLE);

        changeEmailContainer.setVisibility(View.GONE);
        changePasswordContainer.setVisibility(View.GONE);
    }

    private void changeEmail(String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail.trim())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Verification sent to the new email, please check.", Toast.LENGTH_SHORT).show();
                            inputted_current_email.setText("");
                            inputted_new_email.setText("");
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getContext(), "Failed to update email: " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.d("UPDATE_FAILED_MSG", errorMessage);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCurrentUsername() {
        FirebaseUser currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String name = snapshot.exists() ? String.valueOf(snapshot.child("fullName").getValue()) : "No Name";
                username.setText(name);
            } else {
                Toast.makeText(getContext(), "Failed to read.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}