package com.example.englishvocabulary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.activities.CreateVocabularySetActivity;
import com.example.englishvocabulary.fragments.AIChatFragment;
import com.example.englishvocabulary.fragments.ThuVienFragment;
import com.example.englishvocabulary.fragments.TrangChuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private int previousNavItemId = R.id.nav_home;
    private int currentNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        currentNavItemId = R.id.nav_home;

        if (savedInstanceState == null) {
            loadFragment(new TrangChuFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                currentNavItemId = R.id.nav_home;
                loadFragment(new TrangChuFragment());
                return true;
            } else if (itemId == R.id.nav_library) {
                currentNavItemId = R.id.nav_library;
                loadFragment(new ThuVienFragment());
                return true;
            } else if (itemId == R.id.nav_ai) {
                previousNavItemId = currentNavItemId;

                currentNavItemId = R.id.nav_ai;

                AIChatFragment fragment = new AIChatFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("previous_nav_item", previousNavItemId);
                fragment.setArguments(bundle);

                loadFragment(fragment);

                return true;
            } else if (itemId == R.id.nav_add) {
                Intent intent = new Intent(MainActivity.this, CreateVocabularySetActivity.class);
                intent.putExtra("previous_nav_item", currentNavItemId);
                startActivity(intent);
                return false;
            }
            return false;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int previousNavItemId = intent.getIntExtra("previous_nav_item", R.id.nav_home);
        bottomNavigation.setSelectedItemId(previousNavItemId);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
