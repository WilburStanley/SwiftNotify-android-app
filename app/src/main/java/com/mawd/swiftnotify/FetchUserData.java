package com.mawd.swiftnotify;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FetchUserData {
    private FirebaseAuth auth;
    private DatabaseReference reference;

    public FetchUserData() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
    }

    public interface FetchUserStatusCallback {
        void onSuccess(String status);

        void onFailure(Exception e);
    }

    public void fetchUserStatus(FetchUserStatusCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            reference.child(currentUser.getUid()).child("userStatus").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String status = snapshot.exists() ? String.valueOf(snapshot.getValue()) : "Status unknown";
                    callback.onSuccess(status);
                } else {
                    callback.onFailure(new Exception("Failed to fetch user status"));
                }
            });
        } else {
            callback.onFailure(new Exception("User is not logged in"));
        }
    }

    public interface TeacherAvailabilityCallback {
        void onTeacherAvailabilityFetched(boolean isTeacherAvailable);
    }

    public void fetchTeacherAvailability(TeacherAvailabilityCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            reference.child(currentUser.getUid()).child("teacherAvailable").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isTeacherAvailable = Boolean.TRUE.equals(task.getResult().getValue(Boolean.class));
                    callback.onTeacherAvailabilityFetched(isTeacherAvailable);
                } else {
                    callback.onTeacherAvailabilityFetched(false);
                }
            });
        } else {
            callback.onTeacherAvailabilityFetched(false);
        }
    }

    public interface FetchUserEmailAndNameCallback {
        void onSuccess(String userEmail, String userName);

        void onFailure(Exception e);
    }

    public void fetchUserEmailAndName(FetchUserEmailAndNameCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            reference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                        String userName = dataSnapshot.child("fullName").getValue(String.class);
                        callback.onSuccess(userEmail != null ? userEmail : "Email Unknown", userName != null ? userName : "Name Unknown");
                    } else {
                        callback.onFailure(new Exception("User data not found"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onFailure(databaseError.toException());
                }
            });
        } else {
            callback.onFailure(new Exception("User is not logged in"));
        }
    }

    public interface FetchEmailCallback {
        void onSuccess(String fullName);

        void onFailure(Exception e);
    }

    public void fetchFullName(FetchEmailCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            reference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        callback.onSuccess(fullName);
                    } else {
                        callback.onFailure(new Exception("User data not found"));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
        } else {
            callback.onFailure(new Exception("User is not logged in"));
        }
    }
}