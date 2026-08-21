package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvCorrect, tvCorrectTitle, tvWrongTitle;
    private TextView tvWrong;
    private TextView tvScore;

    private Button btnRetry;
    private Button btnHome;

    private int correct;
    private int wrong;
    private int total;

    private int setId;

    // XÁC ĐỊNH LOẠI KẾT QUẢ
    private String learningMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_quiz_result
        );


        tvCorrect =
                findViewById(R.id.tvCorrect);

        tvWrong =
                findViewById(R.id.tvWrong);

        tvScore =
                findViewById(R.id.tvScore);

        btnRetry =
                findViewById(R.id.btnRetry);

        btnHome =
                findViewById(R.id.btnHome);

        tvCorrectTitle = findViewById(R.id.tvCorrectTitle);

        tvWrongTitle = findViewById(R.id.tvWrongTitle);

        learningMode =
                getIntent().getStringExtra(
                        "learning_mode"
                );


        setId =
                getIntent().getIntExtra(
                        "set_id",
                        -1
                );


        if ("FLASHCARD".equals(learningMode)) {
            tvCorrectTitle.setText("Đã nhớ");
            tvWrongTitle.setText("Chưa nhớ");
            correct =
                    getIntent().getIntExtra(
                            "remembered_count",
                            0
                    );

            wrong =
                    getIntent().getIntExtra(
                            "not_remembered_count",
                            0
                    );

            total =
                    correct + wrong;

        } else if ("MATCHING".equals(learningMode)) {
            tvCorrectTitle.setText("Số cặp");
            tvWrongTitle.setText("Ghép sai");

            correct =
                    getIntent().getIntExtra(
                            "correct",
                            0
                    );

            wrong =
                    getIntent().getIntExtra(
                            "wrong",
                            0
                    );

            total =
                    getIntent().getIntExtra(
                            "total",
                            correct + wrong
                    );

        } else {
            tvCorrectTitle.setText("Đúng");
            tvWrongTitle.setText("Sai");

            // NẾU LÀ QUIZ

            correct =
                    getIntent().getIntExtra(
                            "correct",
                            0
                    );

            wrong =
                    getIntent().getIntExtra(
                            "wrong",
                            0
                    );

            total =
                    getIntent().getIntExtra(
                            "total",
                            correct + wrong
                    );
        }


        // TÍNH ĐIỂM
        int score = 0;

        if ("MATCHING".equals(learningMode)) {
            // Matching: điểm dựa trên hiệu suất
            // 0 lần sai = 100%, càng sai nhiều càng thấp
            if (correct > 0) {
                score = correct * 100 / (correct + wrong);
            }
        } else if (total > 0) {

            score =
                    correct * 100 / total;
        }


        // HIỂN THỊ

        tvCorrect.setText(
                String.valueOf(correct)
        );

        tvWrong.setText(
                String.valueOf(wrong)
        );

        tvScore.setText(
                score + "%"
        );


        // HỌC LẠI

        btnRetry.setOnClickListener(v -> {

            if ("FLASHCARD".equals(learningMode)) {

                // HỌC LẠI FLASHCARD
                Intent intent =
                        new Intent(
                                QuizResultActivity.this,
                                FlashcardActivity.class
                        );

                intent.putExtra(
                        "set_id",
                        setId
                );

                startActivity(intent);

            } else if ("MATCHING".equals(learningMode)) {

                // CHƠI LẠI MATCHING
                Intent intent =
                        new Intent(
                                QuizResultActivity.this,
                                MatchingGameActivity.class
                        );

                intent.putExtra(
                        "set_id",
                        setId
                );

                startActivity(intent);

            } else {

                // HỌC LẠI QUIZ
                Intent intent =
                        new Intent(
                                QuizResultActivity.this,
                                QuizActivity.class
                        );

                intent.putExtra(
                        "set_id",
                        setId
                );

                startActivity(intent);
            }

            finish();
        });


        // VỀ TRANG CHỦ

        btnHome.setOnClickListener(v -> {

            finish();

        });
    }
}