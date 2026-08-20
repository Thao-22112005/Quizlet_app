package com.example.englishvocabulary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.activities.FlashcardActivity;
import com.example.englishvocabulary.activities.VocabularySetDetailActivity;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.VocabularySet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TrangChuFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvAvatar;
    private TextView tvContinueTitle;
    private TextView tvContinueCount;
    private TextView tvProgressText;
    private TextView tvNoRecent;
    private TextView tvViewAll;
    private ProgressBar progressLearning;
    private Button btnContinue;
    private LinearLayout layoutRecentSets;

    private VocabularySetDAO vocabularySetDAO;
    private WordDAO wordDAO;
    private FirebaseAuth firebaseAuth;
    private List<VocabularySet> vocabularySets;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);

        // Ánh xạ View
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvAvatar = view.findViewById(R.id.tvAvatar);
        tvContinueTitle = view.findViewById(R.id.tvContinueTitle);
        tvContinueCount = view.findViewById(R.id.tvContinueCount);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        tvNoRecent = view.findViewById(R.id.tvNoRecent);
        tvViewAll = view.findViewById(R.id.tvViewAll);
        progressLearning = view.findViewById(R.id.progressLearning);
        btnContinue = view.findViewById(R.id.btnContinue);
        layoutRecentSets = view.findViewById(R.id.layoutRecentSets);

        // Khởi tạo Database & Auth
        vocabularySetDAO = new VocabularySetDAO(requireContext());
        wordDAO = new WordDAO(requireContext());
        firebaseAuth = FirebaseAuth.getInstance();

        // Tải thông tin
        loadUserInfo();
        loadVocabularySets();

        // Sự kiện click
        tvViewAll.setOnClickListener(v -> {
            BottomNavigationView bottomNavigation = requireActivity().findViewById(R.id.bottomNavigation);
            bottomNavigation.setSelectedItemId(R.id.nav_library);
        });

        tvAvatar.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new TrangCaNhanFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadUserInfo() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            tvAvatar.setText("U");
            tvGreeting.setText("Xin chào 👋");
            return;
        }

        String displayName = user.getDisplayName();
        String email = user.getEmail();
        String initial = "U";

        if (displayName != null && !displayName.trim().isEmpty()) {
            String name = displayName.trim();
            initial = name.substring(0, 1).toUpperCase();
            tvGreeting.setText("Xin chào " + name);
        } else if (email != null && !email.isEmpty()) {
            initial = email.substring(0, 1).toUpperCase();
            tvGreeting.setText("Xin chào ");
        } else {
            tvGreeting.setText("Xin chào ");
        }

        tvAvatar.setText(initial);
    }

    private void loadVocabularySets() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            showEmptyState();
            return;
        }

        vocabularySets = vocabularySetDAO.getByUserUid(user.getUid());

        if (vocabularySets == null || vocabularySets.isEmpty()) {
            showEmptyState();
            return;
        }

        layoutRecentSets.setVisibility(View.VISIBLE);
        tvNoRecent.setVisibility(View.GONE);

        // Hiển thị bộ mới nhất (Học tiếp)
        showContinueSet(vocabularySets.get(0));

        // Hiển thị 5 bộ gần đây
        showRecentSets();
    }

    private void showContinueSet(VocabularySet set) {
        int total = wordDAO.getBySetId(set.getId()).size();
        int learned = wordDAO.getLearnedWordCount(set.getId());
        int progress = (total > 0) ? (learned * 100) / total : 0;

        tvContinueTitle.setText(set.getTitle());
        tvContinueCount.setText(total + " từ");
        progressLearning.setProgress(progress);
        tvProgressText.setText("Đã học thuộc " + learned + " từ (" + progress + "%)");

        btnContinue.setEnabled(total > 0);
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FlashcardActivity.class);
            intent.putExtra("set_id", set.getId());
            startActivity(intent);
        });
    }

    private void showRecentSets() {
        layoutRecentSets.removeAllViews();
        int count = Math.min(5, vocabularySets.size());

        for (int i = 0; i < count; i++) {
            VocabularySet set = vocabularySets.get(i);
            addRecentSetView(set);
        }
    }

    private void addRecentSetView(VocabularySet set) {
        View item = LayoutInflater.from(requireContext()).inflate(R.layout.item_recent_vocabulary_set, layoutRecentSets, false);

        ImageView imgCover = item.findViewById(R.id.imgCover);
        TextView tvTitle = item.findViewById(R.id.tvRecentTitle);
        TextView tvInfo = item.findViewById(R.id.tvRecentInfo);

        tvTitle.setText(set.getTitle());
        int wordCount = wordDAO.getBySetId(set.getId()).size();
        tvInfo.setText(wordCount + " từ");

        String path = set.getCoverImage();
        if (path != null && !path.isEmpty() && new java.io.File(path).exists()) {
            imgCover.setImageURI(android.net.Uri.fromFile(new java.io.File(path)));
        } else {
            imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        item.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), VocabularySetDetailActivity.class);
            intent.putExtra("set_id", set.getId());
            intent.putExtra("set_title", set.getTitle());
            startActivity(intent);
        });

        layoutRecentSets.addView(item);
    }

    private void showEmptyState() {
        layoutRecentSets.setVisibility(View.GONE);
        tvNoRecent.setVisibility(View.VISIBLE);
        tvContinueTitle.setText("Chưa có bộ từ");
        tvContinueCount.setText("0 từ");
        progressLearning.setProgress(0);
        tvProgressText.setText("Chưa có dữ liệu học tập");
        btnContinue.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (vocabularySetDAO != null) {
            loadUserInfo();
            loadVocabularySets();
        }
    }
}
