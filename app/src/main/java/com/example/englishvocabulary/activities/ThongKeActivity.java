package com.example.englishvocabulary.activities;

import android.os.Bundle;
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
    private TextView tvTabWeek, tvTabMonth, tvChartInfo;

    private ProgressBar progressTopSet;
    private ChartView weeklyChart;
    private ImageButton btnBack;

    private FirebaseAuth firebaseAuth;
    private WordDAO wordDAO;
    private VocabularySetDAO vocabularySetDAO;
    private LearningHistoryDAO learningHistoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        // =========================
        // ÁNH XẠ VIEW
        // =========================

        tvLearned = findViewById(R.id.tvLearned);
        tvRemembered = findViewById(R.id.tvRemembered);
        tvNeedReview = findViewById(R.id.tvNeedReview);

        tvTopSetTitle = findViewById(R.id.tvTopSetTitle);
        tvTopSetCount = findViewById(R.id.tvTopSetCount);

        tvTabWeek = findViewById(R.id.tvTabWeek);
        tvTabMonth = findViewById(R.id.tvTabMonth);
        tvChartInfo = findViewById(R.id.tvChartInfo);

        progressTopSet = findViewById(R.id.progressTopSet);
        weeklyChart = findViewById(R.id.weeklyChart);

        btnBack = findViewById(R.id.btnBack);

        // =========================
        // KHỞI TẠO DATABASE & AUTH
        // =========================

        wordDAO = new WordDAO(this);
        vocabularySetDAO = new VocabularySetDAO(this);
        learningHistoryDAO = new LearningHistoryDAO(this);

        firebaseAuth = FirebaseAuth.getInstance();

        // =========================
        // BUTTON BACK
        // =========================

        btnBack.setOnClickListener(v -> finish());

        // =========================
        // TAB
        // =========================

        tvTabWeek.setOnClickListener(v -> {
            showWeekStatistics();
        });

        tvTabMonth.setOnClickListener(v -> {
            showMonthStatistics();
        });

        // =========================
        // LOAD DỮ LIỆU BAN ĐẦU
        // =========================

        loadStatistics();
        loadTopVocabularySet();

        // Mặc định mở tab Tuần
        showWeekStatistics();
    }

    // =========================================================
    // TỔNG QUAN
    // =========================================================

    private void loadStatistics() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        String uid = user.getUid();

        int learned =
                wordDAO.getLearnedWordCountByUser(uid);

        int remembered =
                learningHistoryDAO.getRememberedCountByUser(uid);

        // Không cho "Đã nhớ" lớn hơn "Đã học"
        if (remembered > learned) {
            remembered = learned;
        }

        int needReview =
                learned - remembered;

        tvLearned.setText(String.valueOf(learned));
        tvRemembered.setText(String.valueOf(remembered));
        tvNeedReview.setText(String.valueOf(needReview));
    }

    // =========================================================
    // BỘ TỪ VỰNG HỌC NHIỀU NHẤT
    // =========================================================

    private void loadTopVocabularySet() {

        FirebaseUser user =
                firebaseAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        List<VocabularySet> sets =
                vocabularySetDAO.getByUserUid(user.getUid());

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

            int learned =
                    wordDAO.getLearnedWordCount(set.getId());

            if (learned > maxLearned) {

                maxLearned = learned;

                topSet = set;

                totalTop =
                        wordDAO.getWordCountBySetId(
                                set.getId()
                        );
            }
        }

        if (topSet != null) {

            tvTopSetTitle.setText(
                    topSet.getTitle()
            );

            tvTopSetCount.setText(
                    totalTop + " từ"
            );

            int progress =
                    totalTop > 0
                            ? (maxLearned * 100) / totalTop
                            : 0;

            progressTopSet.setProgress(progress);
        }
    }

    // =========================================================
    // TAB TUẦN
    // =========================================================

    private void showWeekStatistics() {

        tvTabWeek.setTextColor(
                0xFF3155E7
        );

        tvTabWeek.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        tvTabMonth.setTextColor(
                0xFF6B7280
        );

        tvTabMonth.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );

        tvChartInfo.setText(
                "Số từ học trong 7 ngày qua"
        );

        loadWeeklyChart();
    }

    // =========================================================
    // TAB THÁNG
    // =========================================================

    private void showMonthStatistics() {

        tvTabMonth.setTextColor(
                0xFF3155E7
        );

        tvTabMonth.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        tvTabWeek.setTextColor(
                0xFF6B7280
        );

        tvTabWeek.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );

        tvChartInfo.setText(
                "Số từ học trong tháng này"
        );

        loadMonthlyChart();
    }

    // =========================================================
    // BIỂU ĐỒ TUẦN
    // =========================================================

    private void loadWeeklyChart() {

        FirebaseUser user =
                firebaseAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        int[] values = new int[7];
        String[] labels = new String[7];

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        SimpleDateFormat labelSdf =
                new SimpleDateFormat(
                        "dd/MM",
                        Locale.getDefault()
                );

        Calendar cal =
                Calendar.getInstance();

        // Lùi 6 ngày
        cal.add(
                Calendar.DAY_OF_YEAR,
                -6
        );

        for (int i = 0; i < 7; i++) {

            String dateStr =
                    sdf.format(cal.getTime());

            labels[i] =
                    labelSdf.format(cal.getTime());

            values[i] =
                    learningHistoryDAO
                            .getDailyActivityCount(
                                    user.getUid(),
                                    dateStr
                            );

            cal.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        weeklyChart.setDays(labels);
        weeklyChart.setValues(values);
    }

    // =========================================================
    // BIỂU ĐỒ THÁNG
    // =========================================================

    private void loadMonthlyChart() {

        FirebaseUser user =
                firebaseAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        int[] values = new int[12];
        String[] labels = new String[12];

        Calendar cal = Calendar.getInstance();

        int currentYear =
                cal.get(Calendar.YEAR);

        SimpleDateFormat monthFormat =
                new SimpleDateFormat(
                        "MM",
                        Locale.getDefault()
                );

        // 12 tháng trong năm
        for (int month = 0; month < 12; month++) {

            labels[month] = "T" + (month + 1);

            // Nếu tháng chưa tới thì = 0
            if (month > cal.get(Calendar.MONTH)) {
                values[month] = 0;
                continue;
            }

            int totalActivity = 0;

            // Đưa Calendar về tháng đang xét
            Calendar monthCal = Calendar.getInstance();

            monthCal.set(
                    Calendar.YEAR,
                    currentYear
            );

            monthCal.set(
                    Calendar.MONTH,
                    month
            );

            monthCal.set(
                    Calendar.DAY_OF_MONTH,
                    1
            );

            int daysInMonth =
                    monthCal.getActualMaximum(
                            Calendar.DAY_OF_MONTH
                    );

            // Duyệt từng ngày trong tháng
            for (int day = 1; day <= daysInMonth; day++) {

                monthCal.set(
                        Calendar.DAY_OF_MONTH,
                        day
                );

                String dateStr =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        ).format(
                                monthCal.getTime()
                        );

                totalActivity +=
                        learningHistoryDAO
                                .getDailyActivityCount(
                                        user.getUid(),
                                        dateStr
                                );
            }

            values[month] = totalActivity;
        }

        weeklyChart.setDays(labels);
        weeklyChart.setValues(values);
    }
}