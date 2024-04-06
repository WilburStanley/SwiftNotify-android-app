package com.mawd.swiftnotify;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mawd.swiftnotify.models.User;

public class SwiftNotifyUtils {
    private DatabaseReference databaseReference;
    public SwiftNotifyUtils() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }
    public void fetchAndStoreTeachers(final DatabaseHelper databaseHelper) {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userStatus = userSnapshot.child("userStatus").getValue(String.class);
                    if (userStatus != null && userStatus.equalsIgnoreCase("teacher")) {
                        // This user is a teacher, so retrieve their details and store in SQLite
                        String fullName = userSnapshot.child("fullName").getValue(String.class);
                        int age = userSnapshot.child("age").getValue(Integer.class);
                        String email = userSnapshot.child("userEmail").getValue(String.class);
                        String gender = userSnapshot.child("userGender").getValue(String.class);
                        String simNumber = userSnapshot.child("simNumber").getValue(String.class);

                        User teacher = new User();
                        teacher.setFullName(fullName);
                        teacher.setAge(age);
                        teacher.setUserEmail(email);
                        teacher.setUserGender(gender);
                        teacher.setSimNumber(simNumber);

                        databaseHelper.addTeacher(teacher);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
