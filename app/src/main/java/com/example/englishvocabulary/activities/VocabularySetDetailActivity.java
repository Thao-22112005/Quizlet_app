package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.adapters.WordAdapter;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.Word;

import java.util.ArrayList;
import java.util.List;

public class VocabularySetDetailActivity
        extends AppCompatActivity {

    // 1. Biến View
    private ImageButton btnBack;
    private TextView tvSetTitle, tvWordCount;
    private LinearLayout layoutEmpty;
    private RecyclerView rvWords;
    private Button btnAddWord, btnQuickAdd;
    private View btnFlashcard, btnQuiz, btnMatching;

    // 2. Biến Data
    private WordDAO wordDAO;
    private WordAdapter wordAdapter;
    private List<Word> wordList;
    private int setId;
    private String setTitle;

    // 3. onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_vocabulary_set_detail);

        // Nhận dữ liệu từ Intent
        setId = getIntent().getIntExtra("set_id", -1);
        setTitle = getIntent().getStringExtra("set_title");

        if (setId == -1) {
            Toast.makeText(this,
                    "Lỗi: Không tìm thấy bộ từ vựng",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        wordDAO = new WordDAO(this);
        initViews();
        setupRecyclerView();
        setupListeners();
    }

    // 4. onResume - load lại khi quay về
    @Override
    protected void onResume() {
        super.onResume();
        loadWords();
    }

    // 5. Ánh xạ View
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSetTitle = findViewById(R.id.tvSetTitle);
        tvWordCount = findViewById(R.id.tvWordCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        rvWords = findViewById(R.id.rvWords);
        btnAddWord = findViewById(R.id.btnAddWord);
        btnQuickAdd = findViewById(R.id.btnQuickAdd);
        btnFlashcard = findViewById(R.id.btnFlashcard);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnMatching = findViewById(R.id.btnMatching);

        tvSetTitle.setText(
                setTitle != null ? setTitle : "Chi tiết bộ từ");
    }

    private void setupRecyclerView() {
        wordList = new ArrayList<>();

        wordAdapter = new WordAdapter(this, wordList,
                new WordAdapter.OnWordActionListener() {

                    @Override
                    public void onEdit(Word word) {
                        Intent intent = new Intent(
                                VocabularySetDetailActivity.this,
                                AddWordActivity.class);
                        intent.putExtra("set_id", setId);
                        intent.putExtra("edit_mode", true);
                        intent.putExtra("word_id",
                                word.getId());
                        intent.putExtra("word_english",
                                word.getEnglish());
                        intent.putExtra("word_pronunciation",
                                word.getPronunciation());
                        intent.putExtra("word_meaning",
                                word.getMeaning());
                        intent.putExtra("word_example",
                                word.getExample());
                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(Word word) {
                        new MaterialAlertDialogBuilder(
                                VocabularySetDetailActivity.this)
                                .setTitle("Xoá từ")
                                .setMessage("Xoá từ \""
                                        + word.getEnglish()
                                        + "\"?")
                                .setPositiveButton("Xoá",
                                        (d, w) -> {
                                            wordDAO.delete(word.getId());
                                            loadWords();
                                        })
                                .setNegativeButton("Huỷ", null)
                                .show();
                    }
                });

        rvWords.setLayoutManager(
                new LinearLayoutManager(this));
        rvWords.setAdapter(wordAdapter);

        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction) {

                        int pos = viewHolder.getAdapterPosition();
                        Word word = wordAdapter.getItem(pos);

                        new MaterialAlertDialogBuilder(
                                VocabularySetDetailActivity.this)
                                .setTitle("Xoá từ")
                                .setMessage("Xoá \""
                                        + word.getEnglish() + "\"?")
                                .setPositiveButton("Xoá", (d, w) -> {
                                    wordDAO.delete(word.getId());
                                    wordAdapter.removeItem(pos);
                                    loadWords();
                                })
                                .setNegativeButton("Huỷ", (d, w) -> {
                                    wordAdapter
                                            .notifyItemChanged(pos);
                                })
                                .setCancelable(false)
                                .show();
                    }

                    @Override
                    public void onChildDraw(
                            @NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isActive) {

                        if (dX < 0) {
                            View itemView = viewHolder.itemView;
                            float density = recyclerView.getResources()
                                    .getDisplayMetrics().density;
                            float radius = 14 * density;
                            float margin = 8 * density;

                            // Nền đỏ bo góc
                            Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setColor(
                                    Color.parseColor("#EF4444"));
                            RectF bg = new RectF(
                                    itemView.getRight() + dX,
                                    itemView.getTop(),
                                    itemView.getRight(),
                                    itemView.getBottom() - margin);
                            c.drawRoundRect(
                                    bg, radius, radius, paint);

                            // Chữ "Xoá"
                            Paint textPaint = new Paint();
                            textPaint.setColor(Color.WHITE);
                            textPaint.setTextSize(14 * density);
                            textPaint.setAntiAlias(true);
                            textPaint.setTextAlign(
                                    Paint.Align.CENTER);
                            float cardH =
                                    itemView.getHeight() - margin;
                            float textY = itemView.getTop()
                                    + (cardH / 2f) + (6 * density);
                            c.drawText("Xoá",
                                    itemView.getRight()
                                            - (40 * density),
                                    textY, textPaint);
                        }

                        super.onChildDraw(c, recyclerView,
                                viewHolder, dX, dY,
                                actionState, isActive);
                    }
                };

        new ItemTouchHelper(callback)
                .attachToRecyclerView(rvWords);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAddWord.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this, AddWordActivity.class);
            intent.putExtra("set_id", setId);
            startActivity(intent);
        });

        btnQuickAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this, QuickAddActivity.class);
            intent.putExtra("set_id", setId);
            startActivity(intent);
        });


        btnFlashcard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VocabularySetDetailActivity.this,
                    FlashcardActivity.class
            );

            intent.putExtra(
                    "set_id",
                    setId
            );

            startActivity(intent);
        });

        btnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VocabularySetDetailActivity.this,
                    QuizActivity.class
            );

            intent.putExtra(
                    "set_id",
                    setId
            );

            startActivity(intent);
        });

        btnMatching.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VocabularySetDetailActivity.this,
                    MatchingGameActivity.class
            );

            intent.putExtra(
                    "set_id",
                    setId
            );

            startActivity(intent);
        });
    }

    // 9. Load từ từ DB
    private void loadWords() {
        wordList = wordDAO.getBySetId(setId);
        tvWordCount.setText(wordList.size() + " từ");

        if (wordList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvWords.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvWords.setVisibility(View.VISIBLE);
            wordAdapter.updateList(wordList);
        }
    }
}
