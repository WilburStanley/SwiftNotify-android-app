package com.mawd.swiftnotify;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                    callback.onFailure(task.getException());
                }
            });
        } else {
            callback.onFailure(new Exception("User is not logged in"));
        }
    }
}
