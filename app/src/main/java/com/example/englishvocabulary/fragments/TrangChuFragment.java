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

import java.util.List;

public class TrangChuFragment extends Fragment {

    // ==============================
    // VIEW
    // ==============================

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


    // ==============================
    // DATABASE
    // ==============================

    private VocabularySetDAO vocabularySetDAO;
    private WordDAO wordDAO;


    // ==============================
    // FIREBASE
    // ==============================

    private FirebaseAuth firebaseAuth;


    // ==============================
    // DATA
    // ==============================

    private List<VocabularySet> vocabularySets;


    // ==============================
    // ON CREATE VIEW
    // ==============================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_trang_chu,
                container,
                false
        );


        // ==============================
        // ÁNH XẠ VIEW
        // ==============================

        tvGreeting =
                view.findViewById(R.id.tvGreeting);


        tvContinueTitle =
                view.findViewById(R.id.tvContinueTitle);

        tvContinueCount =
                view.findViewById(R.id.tvContinueCount);

        tvProgressText =
                view.findViewById(R.id.tvProgressText);

        tvNoRecent =
                view.findViewById(R.id.tvNoRecent);

        tvViewAll =
                view.findViewById(R.id.tvViewAll);

        progressLearning =
                view.findViewById(R.id.progressLearning);

        btnContinue =
                view.findViewById(R.id.btnContinue);

        tvAvatar =
                view.findViewById(R.id.tvAvatar);


        layoutRecentSets =
                view.findViewById(R.id.layoutRecentSets);


        // ==============================
        // DATABASE
        // ==============================

        vocabularySetDAO =
                new VocabularySetDAO(requireContext());

        wordDAO =
                new WordDAO(requireContext());


        // ==============================
        // FIREBASE AUTH
        // ==============================

        firebaseAuth =
                FirebaseAuth.getInstance();

        loadUserInfo();


        // ==============================
        // LOAD DATA
        // ==============================
        tvGreeting.setText("Xin chào: "+firebaseAuth.getCurrentUser().getDisplayName());
        loadVocabularySets();


        // ==============================
        // XEM TẤT CẢ
        // ==============================

        tvViewAll.setOnClickListener(v -> {

            // Phần này để sau khi bạn kết nối
            // với ThuVienFragment.

            BottomNavigationView bottomNavigation =
                    requireActivity().findViewById(R.id.bottomNavigation);

            bottomNavigation.setSelectedItemId(R.id.nav_library);

        });


        return view;
    }

    private void loadUserInfo() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String displayName =
                firebaseAuth.getCurrentUser().getDisplayName();

        // ==============================
        // CÓ TÊN NGƯỜI DÙNG
        // ==============================

        if (displayName != null
                && !displayName.trim().isEmpty()) {

            displayName =
                    displayName.trim();

            // Lấy chữ cái đầu
            String firstLetter =
                    displayName
                            .substring(0, 1)
                            .toUpperCase();

            tvAvatar.setText(
                    firstLetter
            );


            // Lời chào
            tvGreeting.setText(
                    "Xin chào "
                            + displayName
                            + " 👋"
            );

        } else {

            // ==============================
            // KHÔNG CÓ TÊN
            // ==============================

            String email =
                    firebaseAuth
                            .getCurrentUser()
                            .getEmail();

            if (email != null
                    && !email.isEmpty()) {

                String firstLetter =
                        email
                                .substring(0, 1)
                                .toUpperCase();

                tvAvatar.setText(
                        firstLetter
                );

            } else {

                tvAvatar.setText("U");
            }

            tvGreeting.setText(
                    "Xin chào "
            );
        }
    }


    // ==============================
    // LOAD BỘ TỪ CỦA USER
    // ==============================

    private void loadVocabularySets() {

        if (firebaseAuth.getCurrentUser() == null) {

            showEmptyState();

            return;
        }


        String userUid =
                firebaseAuth.getCurrentUser().getUid();


        // Lấy danh sách bộ từ của user
        // DAO đã sắp xếp id DESC
        vocabularySets =
                vocabularySetDAO.getByUserUid(
                        userUid
                );


        if (vocabularySets == null
                || vocabularySets.isEmpty()) {

            showEmptyState();

            return;
        }


        layoutRecentSets.setVisibility(
                View.VISIBLE
        );

        tvNoRecent.setVisibility(
                View.GONE
        );


        // ==============================
        // BỘ ĐANG HỌC
        // ==============================

        VocabularySet continueSet =
                vocabularySets.get(0);


        showContinueSet(
                continueSet
        );


        // ==============================
        // 4 - 5 BỘ GẦN ĐÂY
        // ==============================

        showRecentSets();
    }


    // ==============================
    // TRẠNG THÁI KHÔNG CÓ DỮ LIỆU
    // ==============================

    private void showEmptyState() {

        layoutRecentSets.setVisibility(
                View.GONE
        );

        tvNoRecent.setVisibility(
                View.VISIBLE
        );


        tvContinueTitle.setText(
                "Chưa có bộ từ"
        );

        tvContinueCount.setText(
                "0 từ"
        );

        progressLearning.setProgress(0);

        tvProgressText.setText(
                "Chưa có dữ liệu học tập"
        );

        btnContinue.setEnabled(false);
    }


    // ==============================
    // HIỂN THỊ BỘ ĐANG HỌC
    // ==============================

    private void showContinueSet(
            VocabularySet set) {

        int wordCount =
                wordDAO
                        .getBySetId(
                                set.getId()
                        )
                        .size();


        tvContinueTitle.setText(
                set.getTitle()
        );

        tvContinueCount.setText(
                wordCount + " từ"
        );


        // ==============================
        // TÍNH TIẾN ĐỘ
        // ==============================

        int learnedCount =
                wordDAO.getLearnedWordCount(
                        set.getId()
                );

        int progress = 0;

        if (wordCount > 0) {

            progress =
                    (learnedCount * 100) / wordCount;
        }


        progressLearning.setProgress(
                progress
        );

        tvProgressText.setText(
                "Đã học thuộc "
                        + learnedCount
                        + " từ ("
                        + progress
                        + "%)"
        );


        // ==============================
        // TIẾP TỤC HỌC
        // ==============================

        btnContinue.setEnabled(
                wordCount > 0
        );

        btnContinue.setOnClickListener(v -> {

            if (wordCount == 0) {
                return;
            }

            Intent intent =
                    new Intent(
                            requireContext(),
                            FlashcardActivity.class
                    );

            intent.putExtra(
                    "set_id",
                    set.getId()
            );

            startActivity(intent);
        });
    }


    // ==============================
    // HIỂN THỊ BỘ GẦN ĐÂY
    // ==============================

    private void showRecentSets() {

        layoutRecentSets.removeAllViews();


        // DAO đã trả về id DESC
        // nên phần tử đầu tiên là mới nhất

        int count =
                Math.min(
                        5,
                        vocabularySets.size()
                );


        for (int i = 0; i < count; i++) {

            VocabularySet set =
                    vocabularySets.get(i);


            addRecentSetView(set);
        }
    }


    // ==============================
    // TẠO ITEM BỘ TỪ
    // ==============================

    private void addRecentSetView(VocabularySet set) {

        View item =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.item_recent_vocabulary_set,
                                layoutRecentSets,
                                false
                        );

        ImageView imgCover =
                item.findViewById(R.id.imgCover);

        TextView tvTitle =
                item.findViewById(R.id.tvRecentTitle);

        TextView tvInfo =
                item.findViewById(R.id.tvRecentInfo);


        // ==============================
        // TÊN BỘ TỪ
        // ==============================

        tvTitle.setText(set.getTitle());


        // ==============================
        // SỐ LƯỢNG TỪ
        // ==============================

        int wordCount =
                wordDAO
                        .getBySetId(set.getId())
                        .size();

        tvInfo.setText(
                wordCount + " từ"
        );


        // ==============================
        // ẢNH BÌA
        // ==============================

        String coverPath =
                set.getCoverImage();

        if (coverPath != null
                && !coverPath.isEmpty()) {

            java.io.File imgFile =
                    new java.io.File(coverPath);

            if (imgFile.exists()) {

                imgCover.setImageURI(
                        android.net.Uri.fromFile(imgFile)
                );

            } else {

                // Không tìm thấy file
                imgCover.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

        } else {

            // Không có ảnh
            imgCover.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }


        // ==============================
        // CLICK BỘ TỪ
        // ==============================

        item.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            VocabularySetDetailActivity.class
                    );

            intent.putExtra(
                    "set_id",
                    set.getId()
            );

            intent.putExtra(
                    "set_title",
                    set.getTitle()
            );

            startActivity(intent);
        });


        // ==============================
        // THÊM ITEM
        // ==============================

        layoutRecentSets.addView(item);
    }


    // ==============================
    // LOAD LẠI KHI QUAY VỀ
    // ==============================

    @Override
    public void onResume() {

        super.onResume();


        if (vocabularySetDAO != null) {

            loadVocabularySets();
        }
    }
}