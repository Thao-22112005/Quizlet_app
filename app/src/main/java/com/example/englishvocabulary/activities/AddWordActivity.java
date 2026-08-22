package com.example.englishvocabulary.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.Word;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddWordActivity extends AppCompatActivity {

    // Danh sách loại từ chuẩn
    public static final String[] WORD_TYPES = {
            "(n) Danh từ",
            "(v) Động từ",
            "(adj) Tính từ",
            "(adv) Trạng từ",
            "(prep) Giới từ",
            "(conj) Liên từ",
            "(pron) Đại từ",
            "(det) Mạo từ",
            "(interj) Thán từ",
            "(phr) Cụm từ",
            "(idiom) Thành ngữ"
    };

    private TextInputLayout tilEnglish;
    private TextInputLayout tilMeaning;

    private TextInputEditText edtEnglish;
    private AutoCompleteTextView edtLoaiTu;
    private TextInputEditText edtPronunciation;
    private TextInputEditText edtMeaning;
    private TextInputEditText edtExample;
    private TextInputEditText edtNote;

    private TextView tvTitle;

    private Button btnSaveWord;
    private ImageButton btnBack;

    private WordDAO wordDAO;
    private int setId;
    private int wordId;

    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        // Lấy ID bộ từ
        setId = getIntent().getIntExtra("set_id", 0);

        // Không có bộ từ
        if (setId == 0) {
            Toast.makeText(this, "Không tìm thấy bộ từ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Khởi tạo DAO
        wordDAO = new WordDAO(this);
        // Ánh xạ View
        initViews();
        // Setup dropdown loại từ
        setupWordTypeDropdown();
        // Kiểm tra chế độ sửa
        editMode = getIntent().getBooleanExtra("edit_mode", false);
        if (editMode) {
            setupEditMode();
        } else {
            setupAddMode();
        }
        // Sự kiện
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        btnSaveWord = findViewById(R.id.btnSaveWord);
        tilEnglish = findViewById(R.id.tilEnglish);
        tilMeaning = findViewById(R.id.tilMeaning);
        edtEnglish = findViewById(R.id.edtEnglish);
        edtLoaiTu = findViewById(R.id.edtLoaiTu);
        edtPronunciation = findViewById(R.id.edtPronunciation);
        edtMeaning = findViewById(R.id.edtMeaning);
        edtExample = findViewById(R.id.edtExample);
        edtNote = findViewById(R.id.edtNote);
    }

    private void setupWordTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_dropdown_word_type,
                WORD_TYPES
        );
        edtLoaiTu.setAdapter(adapter);
    }

    private void setupAddMode() {
        tvTitle.setText("Thêm từ mới");
        btnSaveWord.setText("Lưu từ");
    }

    private void setupEditMode() {
        tvTitle.setText("Sửa từ vựng");
        btnSaveWord.setText("Cập nhật");
        // Lấy ID từ cần sửa
        wordId = getIntent().getIntExtra("word_id", 0);
        if (wordId == 0) {
            Toast.makeText(this, "Không tìm thấy từ cần sửa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Lấy dữ liệu từ database
        Word word = wordDAO.getById(wordId);
        if (word == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu từ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Kiểm tra từ có thuộc đúng bộ từ không
        if (word.getSetId() != setId) {
            Toast.makeText(this, "Từ vựng không thuộc bộ từ này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Hiển thị dữ liệu
        edtEnglish.setText(word.getEnglish());

        // Set loại từ trong dropdown
        String savedLoaiTu = word.getLoaiTu();
        if (savedLoaiTu != null && !savedLoaiTu.isEmpty()) {
            edtLoaiTu.setText(savedLoaiTu, false);
        }
        edtPronunciation.setText(word.getPronunciation());
        edtMeaning.setText(word.getMeaning());
        edtExample.setText(word.getExample());
        edtNote.setText(word.getNote());
    }

    private void setupListeners() {
        // Quay lại
        btnBack.setOnClickListener(v -> finish());
        // Lưu / cập nhật
        btnSaveWord.setOnClickListener(v -> saveWord());
    }
    private void saveWord() {
        String english = edtEnglish.getText().toString().trim();
        String loaiTu = WordDAO.normalizeLoaiTu(edtLoaiTu.getText().toString().trim());
        String pronunciation = edtPronunciation.getText().toString().trim();
        String meaning = edtMeaning.getText().toString().trim();
        String example = edtExample.getText().toString().trim();
        String note = edtNote.getText().toString().trim();
        if (english.isEmpty()) {
            tilEnglish.setError("Vui lòng nhập từ tiếng Anh");
            edtEnglish.requestFocus();
            return;
        }
        tilEnglish.setError(null);
        if (meaning.isEmpty()) {
            tilMeaning.setError("Vui lòng nhập nghĩa tiếng Việt");
            edtMeaning.requestFocus();
            return;
        }

        tilMeaning.setError(null);

        if (!editMode && wordDAO.isDuplicate(setId, english, loaiTu)) {
            tilEnglish.setError("Từ này đã tồn tại trong bộ từ");
            edtEnglish.requestFocus();
            return;
        }

        Word word = new Word();
        word.setSetId(setId);
        word.setEnglish(english);
        word.setLoaiTu(loaiTu);
        word.setPronunciation(pronunciation);
        word.setMeaning(meaning);
        word.setExample(example);
        word.setNote(note);

        if (editMode) {
            word.setId(wordId);
            // Lấy dữ liệu cũ để giữ isLearned
            Word oldWord = wordDAO.getById(wordId);

            if (oldWord != null) {
                word.setIsLearned(oldWord.getIsLearned());
            } else {
                word.setIsLearned(0);
            }
            if (editMode && wordDAO.isDuplicateExcept(setId, english, loaiTu, wordId)) {
                tilEnglish.setError("Từ này đã tồn tại trong bộ từ");
                edtEnglish.requestFocus();
                return;
            }

            int result = wordDAO.update(word);
            if (result > 0) {
                Toast.makeText(this, "Đã cập nhật từ vựng", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        word.setIsLearned(0);
        long result = wordDAO.insert(word);
        if (result > 0) {
            Toast.makeText(this, "Đã thêm từ mới", Toast.LENGTH_SHORT).show();
            clearForm();
            edtEnglish.requestFocus();
        } else {

            Toast.makeText(this, "Thêm từ thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        edtEnglish.setText("");
        edtLoaiTu.setText("", false);
        edtPronunciation.setText("");
        edtMeaning.setText("");
        edtExample.setText("");
        edtNote.setText("");
        tilEnglish.setError(null);
        tilMeaning.setError(null);
    }
}