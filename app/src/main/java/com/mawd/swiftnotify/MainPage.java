package com.mawd.swiftnotify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mawd.swiftnotify.databinding.ActivityMainPageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPage extends AppCompatActivity{
    ActivityMainPageBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        verifyEmail();
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home){
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.navigation_history){
                replaceFragment(new HistoryFragment());
            } else if (item.getItemId() == R.id.navigation_profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        boolean navigateToHistory = getIntent().getBooleanExtra("NAVIGATE_TO_HISTORY", false);
        if (navigateToHistory) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HistoryFragment())
                    .commit();
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void verifyEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if(user != null && !user.isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            Toast.makeText(this, "Verify your email first", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}