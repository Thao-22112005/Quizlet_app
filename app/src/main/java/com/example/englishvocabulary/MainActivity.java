package com.example.englishvocabulary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.activities.CreateVocabularySetActivity;
import com.example.englishvocabulary.activities.FlashcardActivity;
import com.example.englishvocabulary.fragments.AIChatFragment;
import com.example.englishvocabulary.fragments.ThuVienFragment;
import com.example.englishvocabulary.fragments.TrangChuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;


    // LƯU MENU HIỆN TẠI
    private int currentNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // ÁNH XẠ VIEW
        bottomNavigation = findViewById(R.id.bottomNavigation);



        currentNavItemId = R.id.nav_home;

        bottomNavigation.setSelectedItemId(R.id.nav_home);

        loadFragment(new TrangChuFragment());


        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {

                currentNavItemId = R.id.nav_home;

                loadFragment(new TrangChuFragment());

                return true;
            }

            else if (itemId == R.id.nav_library) {

                currentNavItemId = R.id.nav_library;

                loadFragment(new ThuVienFragment());

                return true;
            }

            else if (itemId == R.id.nav_ai) {

                currentNavItemId = R.id.nav_ai;

                loadFragment(new AIChatFragment());

                return true;
            }

            else if (itemId == R.id.nav_add) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                CreateVocabularySetActivity.class
                        );

                // Gửi trang hiện tại sang CreateVocabularySetActivity
                intent.putExtra(
                        "previous_nav_item",
                        currentNavItemId
                );

                startActivity(intent);

                // Không chọn nav_add
                return false;
            }

            return false;
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        setIntent(intent);

        int previousNavItemId =
                intent.getIntExtra(
                        "previous_nav_item",
                        R.id.nav_home
                );

        // Chọn lại menu trước đó
        bottomNavigation.setSelectedItemId(
                previousNavItemId
        );
    }


    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragmentContainer,
                        fragment
                )
                .commit();
    }
}