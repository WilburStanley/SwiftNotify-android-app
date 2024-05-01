package com.mawd.swiftnotify;

import android.content.Intent;
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
import java.util.Collection;
import java.util.Collections;

public class HistoryFragment extends Fragment implements SelectListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
        logAdapter = new LogAdapter(getContext(), logList, this);
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
                    DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference()
                            .child("notifications")
                            .child(currentUser.getUid()); // Use the unique key instead of the full name
                    listenerForSingleValue(notificationRef);
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
                    // Clear the list before adding new items
                    logList.clear();
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        NotificationInfo notification = notificationSnapshot.getValue(NotificationInfo.class);
                        if (notification != null) {
                            logList.add(notification);
                        }
                    }
                    // Reverse the list to display the latest item at the top
                    Collections.reverse(logList);
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


    @Override
    public void onItemClick(int position) {
        NotificationInfo clickedUser = logList.get(position);

        Intent intent = new Intent(getContext(), ContentPage.class);

        intent.putExtra("STUDENT_NAME", clickedUser.getUsername());
        intent.putExtra("STUDENT_AGE", clickedUser.getAge());
        intent.putExtra("STUDENT_GENDER", clickedUser.getUserGender());
        intent.putExtra("STUDENT_SECTION", clickedUser.getUserSection());
        intent.putExtra("STUDENT_ACCOUNT", clickedUser.getAccount());
        intent.putExtra("PREVIOUS_LOCATION", "HISTORY_FRAGMENT");

        startActivity(intent);
        requireActivity().finish();
    }
}