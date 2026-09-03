package com.example.englishvocabulary.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.activities.ForgotPasswordActivity;
import com.example.englishvocabulary.activities.LoginActivity;
import com.example.englishvocabulary.activities.ThongKeActivity;
import com.example.englishvocabulary.database.LearningHistoryDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TrangCaNhanFragment extends Fragment {

    private TextView tvAvatar;

    private LearningHistoryDAO learningHistoryDAO;
    private TextView tvName;
    private TextView tvEmail;

    private TextView tvLearned;
    private TextView tvRemembered;
    private TextView tvNeedReview;

    private LinearLayout layoutStatistics;
    private LinearLayout layoutChangePassword;
    private LinearLayout layoutLogout;

    private ImageButton btnBack;


    private FirebaseAuth firebaseAuth;

    private WordDAO wordDAO;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.activity_trang_ca_nhan_fragment,
                container,
                false
        );

        tvAvatar =
                view.findViewById(R.id.tvAvatar);

        tvName =
                view.findViewById(R.id.tvName);

        tvEmail =
                view.findViewById(R.id.tvEmail);

        tvLearned =
                view.findViewById(R.id.tvLearned);

        tvRemembered =
                view.findViewById(R.id.tvRemembered);

        tvNeedReview =
                view.findViewById(R.id.tvNeedReview);

        layoutStatistics =
                view.findViewById(R.id.layoutStatistics);

        layoutChangePassword =
                view.findViewById(R.id.layoutChangePassword);

        layoutLogout =
                view.findViewById(R.id.layoutLogout);

        btnBack =
                view.findViewById(R.id.btnBack);


        firebaseAuth =
                FirebaseAuth.getInstance();


        wordDAO =
                new WordDAO(requireContext());

        learningHistoryDAO =
                new LearningHistoryDAO(requireContext());


        loadUserInfo();


        loadLearningProgress();



        layoutStatistics.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ThongKeActivity.class
                    );

            startActivity(intent);
        });

        layoutChangePassword.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ForgotPasswordActivity.class
                    );

            startActivity(intent);
        });


        layoutLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(requireContext()).setTitle("Đăng xuất").setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Có", (dialog, which) -> { logout(); }).setNegativeButton("Không", (dialog, which) -> { dialog.dismiss(); }).show();
        });

        btnBack.setOnClickListener(v -> {

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });


        return view;
    }


//    private void loadLearningProgress() {
//
//        FirebaseUser user =
//                firebaseAuth.getCurrentUser();
//
//
//        // Không có user
//        if (user == null) {
//
//            tvLearned.setText("0");
//            tvRemembered.setText("0");
//            tvNeedReview.setText("0");
//
//            return;
//        }
//
//
//        // UID của tài khoản Firebase
//        String userUid =
//                user.getUid();
//
//        int learned =
//                wordDAO.getLearnedWordCountByUser(userUid);
//
//
//        int needReview =
//                wordDAO.getUnlearnedWordCountByUser(userUid);
//
//
//        tvLearned.setText(
//                String.valueOf(learned)
//        );
//
//
//        // Hiện tại "Đã nhớ" dùng số từ đã học
//        tvRemembered.setText(
//                String.valueOf(learned)
//        );
//
//
//        tvNeedReview.setText(
//                String.valueOf(needReview)
//        );
//    }

    private void loadLearningProgress() {

        FirebaseUser user =
                firebaseAuth.getCurrentUser();

        if (user == null) {

            tvLearned.setText("0");
            tvRemembered.setText("0");
            tvNeedReview.setText("0");

            return;
        }

        String userUid =
                user.getUid();

        // Số từ đã học
        int learned =
                wordDAO.getLearnedWordCountByUser(userUid);

        // Số từ đã nhớ
        int remembered =
                learningHistoryDAO.getRememberedCountByUser(userUid);

        // Số từ chưa học
        int needReview =
                wordDAO.getUnlearnedWordCountByUser(userUid);

        tvLearned.setText(
                String.valueOf(learned)
        );

        tvRemembered.setText(
                String.valueOf(remembered)
        );

        tvNeedReview.setText(
                String.valueOf(needReview)
        );
    }

    private void loadUserInfo() {

        FirebaseUser user =
                firebaseAuth.getCurrentUser();


        if (user == null) {

            tvName.setText("Người dùng");
            tvEmail.setText("");
            tvAvatar.setText("U");

            return;
        }


        String email =
                user.getEmail();


        if (email != null
                && !email.trim().isEmpty()) {

            tvEmail.setText(email);

        } else {

            tvEmail.setText("");
        }

        String displayName =
                user.getDisplayName();


        if (displayName != null
                && !displayName.trim().isEmpty()) {

            displayName =
                    displayName.trim();


            tvName.setText(
                    displayName
            );


            //avatar
            String firstLetter =
                    displayName
                            .substring(0, 1)
                            .toUpperCase();


            tvAvatar.setText(
                    firstLetter
            );

        } else {
            // KHÔNG CÓ TÊN
            // LẤY CHỮ ĐẦU EMAIL
            if (email != null
                    && !email.trim().isEmpty()) {

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


            tvName.setText(
                    "Người dùng"
            );
        }
    }


    private void logout() {

        firebaseAuth.signOut();


        Toast.makeText(
                requireContext(),
                "Đã đăng xuất",
                Toast.LENGTH_SHORT
        ).show();


        Intent intent =
                new Intent(
                        requireContext(),
                        LoginActivity.class
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(intent);
    }


    @Override
    public void onResume() {

        super.onResume();

        if (wordDAO != null) {

            loadUserInfo();
            loadLearningProgress();
        }
    }
}