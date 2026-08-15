package com.example.englishvocabulary;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.fragments.AIChatFragment;
import com.example.englishvocabulary.fragments.ThuVienFragment;
import com.example.englishvocabulary.fragments.TrangChuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Mặc định mở Trang chủ
        loadFragment(new TrangChuFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                loadFragment(new TrangChuFragment());

            } else if (id == R.id.nav_library) {

                loadFragment(new ThuVienFragment());

            } else if (id == R.id.nav_ai) {

                loadFragment(new AIChatFragment());

            } else if (id == R.id.nav_add) {

                // Tạm thời chưa làm màn hình tạo bộ
                // Bước sau sẽ mở TaoBoTuVungActivity
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}