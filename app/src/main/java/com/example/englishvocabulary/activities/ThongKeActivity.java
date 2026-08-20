package com.example.englishvocabulary.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.LearningHistoryDAO;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.VocabularySet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ThongKeActivity extends AppCompatActivity {

    private TextView tvLearned, tvRemembered, tvNeedReview;
    private TextView tvTopSetTitle, tvTopSetCount;
    private ProgressBar progressTopSet;
    private WeeklyChartView weeklyChart;
    private ImageButton btnBack;

    private FirebaseAuth firebaseAuth;
    private WordDAO wordDAO;
    private VocabularySetDAO vocabularySetDAO;
    private LearningHistoryDAO learningHistoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        // Ánh xạ View
        tvLearned = findViewById(R.id.tvLearned);
        tvRemembered = findViewById(R.id.tvRemembered);
        tvNeedReview = findViewById(R.id.tvNeedReview);
        tvTopSetTitle = findViewById(R.id.tvTopSetTitle);
        tvTopSetCount = findViewById(R.id.tvTopSetCount);
        progressTopSet = findViewById(R.id.progressTopSet);
        weeklyChart = findViewById(R.id.weeklyChart);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo DB & Auth
        wordDAO = new WordDAO(this);
        vocabularySetDAO = new VocabularySetDAO(this);
        learningHistoryDAO = new LearningHistoryDAO(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> finish());

        loadStatistics();
        loadTopVocabularySet();
        loadWeeklyChart();
    }

    private void loadStatistics() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        int learned = wordDAO.getLearnedWordCountByUser(uid);
        int remembered = learningHistoryDAO.getRememberedCountByUser(uid);

        // Tránh trường hợp số từ "nhớ" nhiều hơn số từ "đã học" (do logic đếm khác nhau)
        if (remembered > learned) remembered = learned;
        int needReview = learned - remembered;

        tvLearned.setText(String.valueOf(learned));
        tvRemembered.setText(String.valueOf(remembered));
        tvNeedReview.setText(String.valueOf(needReview));
    }

    private void loadTopVocabularySet() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        List<VocabularySet> sets = vocabularySetDAO.getByUserUid(user.getUid());
        if (sets == null || sets.isEmpty()) {
            tvTopSetTitle.setText("Chưa có bộ từ");
            tvTopSetCount.setText("0 từ");
            progressTopSet.setProgress(0);
            return;
        }

        VocabularySet topSet = null;
        int maxLearned = -1;
        int totalTop = 0;

        for (VocabularySet set : sets) {
            int learned = wordDAO.getLearnedWordCount(set.getId());
            if (learned > maxLearned) {
                maxLearned = learned;
                topSet = set;
                totalTop = wordDAO.getWordCountBySetId(set.getId());
            }
        }

        if (topSet != null) {
            tvTopSetTitle.setText(topSet.getTitle());
            tvTopSetCount.setText(totalTop + " từ");
            int progress = (totalTop > 0) ? (maxLearned * 100) / totalTop : 0;
            progressTopSet.setProgress(progress);
        }
    }

    private void loadWeeklyChart() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        int[] values = new int[7];
        String[] labels = new String[7];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat labelSdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // Lùi về 6 ngày trước (để hôm nay là ngày cuối cùng bên phải biểu đồ)
        cal.add(Calendar.DAY_OF_YEAR, -6);

        for (int i = 0; i < 7; i++) {
            String dateStr = sdf.format(cal.getTime());
            labels[i] = labelSdf.format(cal.getTime());
            values[i] = learningHistoryDAO.getDailyActivityCount(user.getUid(), dateStr);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        weeklyChart.setDays(labels);
        weeklyChart.setValues(values);
    }
}
