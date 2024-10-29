package com.example.adminjava;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Hiển thị fragment mặc định
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddCourseFragment())
                    .commit();
        }
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_add_course) {
                    selectedFragment = new AddCourseFragment();
                } else if (item.getItemId() == R.id.nav_search_class) {
                    selectedFragment = new SearchFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            };
}
