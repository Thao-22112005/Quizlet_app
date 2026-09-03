package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.LearningHistoryDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.LearningHistory;
import com.example.englishvocabulary.models.SpeakWord;
import com.example.englishvocabulary.models.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchingGameActivity extends AppCompatActivity {

    // View
    private ImageButton btnClose;
    private TextView tvTitle, tvProgress, tvInstruction;
    private LinearLayout columnLeft, columnRight;

    // Data
    private WordDAO wordDAO;
    private LearningHistoryDAO learningHistoryDAO;
    private int setId;

    // Multi-round
    private List<List<Word>> rounds;
    private int currentRound = 0;
    private int totalRounds;

    // Current round
    private List<Word> gameWords;
    private int totalPairs;
    private int matchedPairs = 0;

    // Tổng tất cả vòng
    private int totalPairsAll = 0;
    private int matchedPairsAll = 0;

    // Thống kê kết quả
    private int wrongCount = 0;

    // Game state
    private CardView selectedLeftCard = null;
    private CardView selectedRightCard = null;
    private int selectedLeftIndex = -1;
    private int selectedRightIndex = -1;

    // Chặn click nhanh khi đang xử lý animation
    private boolean isProcessing = false;

    // speak word
    private TextToSpeech textToSpeech;
    private SpeakWord speakWord;

    // Màu sắc - Light theme
    private static final int COLOR_LEFT_DEFAULT = Color.parseColor("#EEF2FF");
    private static final int COLOR_RIGHT_DEFAULT = Color.parseColor("#F3E8FF");
    private static final int COLOR_SELECTED = Color.parseColor("#C7D2FE");
    private static final int COLOR_CORRECT = Color.parseColor("#D1FAE5");
    private static final int COLOR_WRONG = Color.parseColor("#FEE2E2");
    private static final int COLOR_MATCHED = Color.parseColor("#F3F4F6");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);

        // Nhận dữ liệu
        setId = getIntent().getIntExtra("set_id", -1);

        // Khởi tạo
        wordDAO = new WordDAO(this);
        learningHistoryDAO = new LearningHistoryDAO(this);
        speakWord = new SpeakWord();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {

                textToSpeech.setLanguage(Locale.US);

            }
        });
        initViews();
        setupListeners();
        loadGame();
    }

    // =====================================================
    // ÁNH XẠ VIEW
    // =====================================================

    private void initViews() {
        btnClose = findViewById(R.id.btnClose);
        tvTitle = findViewById(R.id.tvTitle);
        tvProgress = findViewById(R.id.tvProgress);
        tvInstruction = findViewById(R.id.tvInstruction);
        columnLeft = findViewById(R.id.columnLeft);
        columnRight = findViewById(R.id.columnRight);
    }

    // =====================================================
    // SỰ KIỆN
    // =====================================================

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }

    // =====================================================
    // LOAD GAME - CHIA THÀNH NHIỀU VÒNG
    // =====================================================

    private void loadGame() {
        List<Word> allWords = wordDAO.getBySetId(setId);

        if (allWords.size() < 4) {
            Toast.makeText(this,
                    "Cần ít nhất 4 từ để chơi",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xáo trộn tất cả từ
        Collections.shuffle(allWords);

        // Chia thành các vòng, mỗi vòng tối đa 10 từ
        rounds = new ArrayList<>();
        for (int i = 0; i < allWords.size(); i += 10) {
            int end = Math.min(i + 10, allWords.size());
            // Cần ít nhất 2 từ cho 1 vòng
            if (end - i >= 2) {
                rounds.add(new ArrayList<>(allWords.subList(i, end)));
            } else if (!rounds.isEmpty()) {
                // Nếu còn 1 từ lẻ, gộp vào vòng trước
                rounds.get(rounds.size() - 1)
                        .addAll(allWords.subList(i, end));
            }
        }

        totalRounds = rounds.size();
        currentRound = 0;
        wrongCount = 0;

        // Tính tổng tất cả cặp từ
        totalPairsAll = 0;
        for (List<Word> round : rounds) {
            totalPairsAll += round.size();
        }
        matchedPairsAll = 0;

        startRound();
    }

    // =====================================================
    // BẮT ĐẦU VÒNG MỚI
    // =====================================================

    private void startRound() {
        gameWords = rounds.get(currentRound);
        totalPairs = gameWords.size();
        matchedPairs = 0;

        // Reset selection
        selectedLeftCard = null;
        selectedRightCard = null;
        selectedLeftIndex = -1;
        selectedRightIndex = -1;

        // Cập nhật tiêu đề nếu có nhiều vòng
        if (totalRounds > 1) {
            tvInstruction.setText(
                    "Vòng " + (currentRound + 1) + "/" + totalRounds
                            + " • Ghép từ tiếng Anh với nghĩa tiếng Việt");
        }

        updateProgress();
        buildGameBoard();
    }

    // =====================================================
    // XÂY DỰNG BÀN CHƠI
    // =====================================================

    private void buildGameBoard() {
        columnLeft.removeAllViews();
        columnRight.removeAllViews();

        // Tạo danh sách tiếng Anh (xáo trộn)
        List<Integer> leftOrder = new ArrayList<>();
        for (int i = 0; i < gameWords.size(); i++) {
            leftOrder.add(i);
        }
        Collections.shuffle(leftOrder);

        // Tạo danh sách tiếng Việt (xáo trộn khác)
        List<Integer> rightOrder = new ArrayList<>();
        for (int i = 0; i < gameWords.size(); i++) {
            rightOrder.add(i);
        }
        Collections.shuffle(rightOrder);

        // Tạo các card bên trái (Tiếng Anh)
        for (int i = 0; i < leftOrder.size(); i++) {
            int wordIndex = leftOrder.get(i);
            CardView card = createCard(
                    gameWords.get(wordIndex).getEnglish(),
                    wordIndex,
                    true);
            columnLeft.addView(card);
        }

        // Tạo các card bên phải (Tiếng Việt)
        for (int i = 0; i < rightOrder.size(); i++) {
            int wordIndex = rightOrder.get(i);
            CardView card = createCard(
                    gameWords.get(wordIndex).getMeaning(),
                    wordIndex,
                    false);
            columnRight.addView(card);
        }
    }

    // =====================================================
    // TẠO CARD
    // =====================================================

    private CardView createCard(String text, int wordIndex,
                                 boolean isLeft) {

        // Card
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(8);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(0));
        card.setClickable(true);
        card.setFocusable(true);

        // Màu nền khác nhau cho 2 cột
        if (isLeft) {
            card.setCardBackgroundColor(COLOR_LEFT_DEFAULT);
        } else {
            card.setCardBackgroundColor(COLOR_RIGHT_DEFAULT);
        }

        // Tag lưu index của từ
        card.setTag(wordIndex);

        // Text bên trong
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(15);
        tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        tv.setPadding(
                dpToPx(14), dpToPx(14),
                dpToPx(14), dpToPx(14));
        tv.setGravity(Gravity.CENTER);

        // Màu text khác nhau
        if (isLeft) {
            tv.setTextColor(Color.parseColor("#4055F5"));
        } else {
            tv.setTextColor(Color.parseColor("#7C3AED"));
        }

        card.addView(tv);

        // Click
        card.setOnClickListener(v -> onCardClick(card, isLeft));

        return card;
    }

    // =====================================================
    // XỬ LÝ CLICK CARD
    // =====================================================

    private void onCardClick(CardView card, boolean isLeft) {
        // Chặn click khi đang xử lý animation
        if (isProcessing) return;

        int wordIndex = (int) card.getTag();

        if (isLeft) {
            if (selectedLeftCard != null) {
                selectedLeftCard.setCardBackgroundColor(
                        COLOR_LEFT_DEFAULT);
            }
            selectedLeftCard = card;
            selectedLeftIndex = wordIndex;
            card.setCardBackgroundColor(COLOR_SELECTED);
        } else {
            if (selectedRightCard != null) {
                selectedRightCard.setCardBackgroundColor(
                        COLOR_RIGHT_DEFAULT);
            }
            selectedRightCard = card;
            selectedRightIndex = wordIndex;
            card.setCardBackgroundColor(COLOR_SELECTED);
        }

        // Nếu đã chọn cả 2 bên → kiểm tra
        if (selectedLeftCard != null && selectedRightCard != null) {
            checkMatch();
        }
    }

    // =====================================================
    // KIỂM TRA GHÉP ĐÚNG
    // =====================================================

    private void checkMatch() {
        // Khoá click trong lúc animation
        isProcessing = true;

        if (selectedLeftIndex == selectedRightIndex) {

            speakWord.speakWord(
                    textToSpeech,
                    gameWords,
                    selectedLeftIndex
            );
            // ĐÚNG!
            selectedLeftCard.setCardBackgroundColor(COLOR_CORRECT);
            selectedRightCard.setCardBackgroundColor(COLOR_CORRECT);

            // Lưu lịch sử - ghép đúng
            saveLearningHistory(
                    gameWords.get(selectedLeftIndex), 1);

            // Lưu reference trước khi reset
            final CardView leftCard = selectedLeftCard;
            final CardView rightCard = selectedRightCard;

            matchedPairs++;
            matchedPairsAll++;
            updateProgress();

            // Reset selection trước khi delay
            selectedLeftCard = null;
            selectedRightCard = null;
            selectedLeftIndex = -1;
            selectedRightIndex = -1;

            // Sau 500ms: làm mờ card đã ghép
            new Handler().postDelayed(() -> {
                leftCard.setCardBackgroundColor(COLOR_MATCHED);
                rightCard.setCardBackgroundColor(COLOR_MATCHED);
                leftCard.setClickable(false);
                rightCard.setClickable(false);
                leftCard.setAlpha(0.5f);
                rightCard.setAlpha(0.5f);

                // Mở khoá click
                isProcessing = false;

                // Kiểm tra hoàn thành vòng hiện tại
                if (matchedPairs == totalPairs) {
                    onRoundComplete();
                }
            }, 500);

        } else {
            // SAI!
            wrongCount++;

            selectedLeftCard.setCardBackgroundColor(COLOR_WRONG);
            selectedRightCard.setCardBackgroundColor(COLOR_WRONG);

            // Lưu lịch sử - ghép sai
            saveLearningHistory(
                    gameWords.get(selectedLeftIndex), 0);

            final CardView leftCard = selectedLeftCard;
            final CardView rightCard = selectedRightCard;

            // Reset selection trước khi delay
            selectedLeftCard = null;
            selectedRightCard = null;
            selectedLeftIndex = -1;
            selectedRightIndex = -1;

            // Sau 500ms: reset về mặc định
            new Handler().postDelayed(() -> {
                leftCard.setCardBackgroundColor(COLOR_LEFT_DEFAULT);
                rightCard.setCardBackgroundColor(COLOR_RIGHT_DEFAULT);
                // Mở khoá click
                isProcessing = false;
            }, 500);
        }
    }

    // =====================================================
    // HOÀN THÀNH 1 VÒNG
    // =====================================================

    private void onRoundComplete() {
        currentRound++;

        if (currentRound < totalRounds) {
            // Còn vòng tiếp theo → chờ 800ms rồi bắt đầu
            new Handler().postDelayed(() -> startRound(), 800);
        } else {
            // Hết tất cả vòng → hiển thị kết quả
            showResult();
        }
    }

    // =====================================================
    // LƯU LỊCH SỬ HỌC
    // =====================================================

    private void saveLearningHistory(Word word, int isCorrect) {

        String currentTime =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

        LearningHistory history = new LearningHistory();
        history.setSetId(setId);
        history.setWordId(word.getId());
        history.setIsCorrect(isCorrect);
        history.setLearningMode("MATCHING");
        history.setLearnedAt(currentTime);

        learningHistoryDAO.insert(history);
    }

    // =====================================================
    // CẬP NHẬT TIẾN ĐỘ (tổng tất cả vòng)
    // =====================================================

    private void updateProgress() {
        tvProgress.setText(matchedPairsAll + "/" + totalPairsAll);
    }

    // =====================================================
    // HIỂN THỊ KẾT QUẢ (QuizResultActivity)
    // =====================================================

    private void showResult() {

        // Chờ 800ms để người chơi thấy cặp cuối cùng
        new Handler().postDelayed(() -> {

            Intent intent = new Intent(
                    MatchingGameActivity.this,
                    QuizResultActivity.class
            );

            // Tổng số cặp đã ghép
            intent.putExtra("correct", totalPairsAll);

            // Số lần ghép sai
            intent.putExtra("wrong", wrongCount);

            // Tổng số cặp (dùng để tính điểm)
            intent.putExtra("total", totalPairsAll);

            // ID bộ từ
            intent.putExtra("set_id", setId);

            // Loại trò chơi
            intent.putExtra("learning_mode", "MATCHING");

            startActivity(intent);
            finish();

        }, 800);
    }

    // =====================================================
    // HELPER: DP → PX
    // =====================================================

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
