package com.mawd.swiftnotify;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.mawd.swiftnotify.models.NotificationInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    LogAdapter logAdapter;
    ArrayList<NotificationInfo> logList;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.logList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        logList = new ArrayList<>();
        logAdapter = new LogAdapter(getContext(), logList);
        recyclerView.setAdapter(logAdapter);

        fetchNotificationData();

        return view;
    }

    private void fetchNotificationData() {
        String TAG = "NotificationDebug";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FetchUserData fetchUserData = new FetchUserData();
            fetchUserData.fetchFullName(new FetchUserData.FetchEmailCallback() {
                @Override
                public void onSuccess(String fullName) {
                    String uniqueKey = generateUniqueKey(fullName);
                    if (uniqueKey != null) {
                        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference()
                                .child("notifications")
                                .child(uniqueKey); // Use the unique key instead of the full name
                        listenerForSingleValue(notificationRef);
                    } else {
                        Log.e(TAG, "Failed to generate a unique key for full name: " + fullName);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });

        } else {
            Log.e(TAG, "Current user is null");
        }
    }

    private void listenerForSingleValue(DatabaseReference ref) {
        String TAG = "NotificationDebug";
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        NotificationInfo notification = notificationSnapshot.getValue(NotificationInfo.class);
                        if (notification != null) {
                            logList.add(notification);
                        }
                    }
                    logAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "No notifications found for current user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching notification data: " + databaseError.getMessage());
            }
        });
    }
    private String generateUniqueKey(String fullName) {
        try {
            // Create a SHA-256 MessageDigest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Apply the digest to the full name bytes
            byte[] hash = digest.digest(fullName.getBytes());
            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                // Convert each byte to a 2-character hexadecimal representation
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            // Return the hexadecimal string as the unique key
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle the exception or return a default value if needed
            return null;
        }
    }
}