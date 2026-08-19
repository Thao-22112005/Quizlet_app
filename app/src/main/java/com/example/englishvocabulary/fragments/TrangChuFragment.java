package com.example.englishvocabulary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.activities.FlashcardActivity;
import com.example.englishvocabulary.activities.QuizActivity;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.VocabularySet;

public class TrangChuFragment extends Fragment {

    private Button btnTestFlashcard;
    private Button btnQuiz;

    // DATABASE
    private VocabularySetDAO vocabularySetDAO;
    private WordDAO wordDAO;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.fragment_trang_chu,
                        container,
                        false
                );


        // ÁNH XẠ VIEW

        btnTestFlashcard =
                view.findViewById(
                        R.id.btnTestFlashcard
                );

        btnQuiz = view.findViewById(R.id.btnQuiz);


        // DATABASE

        vocabularySetDAO =
                new VocabularySetDAO(
                        requireContext()
                );

        wordDAO =
                new WordDAO(
                        requireContext()
                );

        // TEST FLASHCARD

        btnTestFlashcard.setOnClickListener(v -> {

            testFlashcard();

        });

        btnQuiz.setOnClickListener(v->{
            testQuiz();
        });


        return view;
    }

    private void testQuiz() {

        // TẠO BỘ TỪ TEST QUIZ

        VocabularySet vocabularySet =
                new VocabularySet();

        vocabularySet.setUserUid("test_user");

        vocabularySet.setTitle("Quiz Test Data");

        vocabularySet.setDescription("Bộ từ để test Quiz");

        vocabularySet.setTopic("Test");

        vocabularySet.setLevel("A1");

        vocabularySet.setCoverImage("");

        vocabularySet.setCreatedAt("2026-08-17 19:00:00");

        vocabularySet.setUpdatedAt("2026-08-17 19:00:00");


        // LƯU BỘ TỪ

        long setId =
                vocabularySetDAO.insert(
                        vocabularySet
                );


        if (setId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Tạo bộ test Quiz thất bại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }
        // TẠO 5 TỪ GIẢ (Để đủ 4 đáp án A,B,C,D)

        wordDAO.insertFakeData(
                (int) setId
        );


        // MỞ QUIZ ACTIVITY

        Intent intent =
                new Intent(
                        requireContext(),
                        QuizActivity.class
                );

        intent.putExtra(
                "set_id",
                (int) setId
        );

        startActivity(intent);
    }

    private void testFlashcard() {

        // TẠO BỘ TỪ TEST

        VocabularySet vocabularySet =
                new VocabularySet();

        vocabularySet.setUserUid(
                "test_user"
        );

        vocabularySet.setTitle(
                "Tiếng Anh Test"
        );

        vocabularySet.setDescription(
                "Bộ từ dùng để test Flashcard"
        );

        vocabularySet.setTopic(
                "Cơ bản"
        );

        vocabularySet.setLevel(
                "A1"
        );

        vocabularySet.setCoverImage(
                ""
        );

        vocabularySet.setCreatedAt(
                "2026-08-17 19:00:00"
        );

        vocabularySet.setUpdatedAt(
                "2026-08-17 19:00:00"
        );


        // LƯU BỘ TỪ

        long setId =
                vocabularySetDAO.insert(
                        vocabularySet
                );


        // KIỂM TRA KẾT QUẢ

        if (setId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Tạo bộ test thất bại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // TẠO 5 TỪ GIẢ

        wordDAO.insertFakeData(
                (int) setId
        );


        // MỞ FLASHCARD

        Intent intent =
                new Intent(
                        requireContext(),
                        FlashcardActivity.class
                );

        intent.putExtra(
                "set_id",
                (int) setId
        );

        startActivity(intent);
    }
}