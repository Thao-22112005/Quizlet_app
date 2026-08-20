package com.example.englishvocabulary.activities;

import android.os.Bundle;
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
    private ImageButton btnBack;
    private TextInputLayout tilEnglish, tilMeaning;
    private TextInputEditText edtEnglish, edtMeaning, edtPronunciation, edtExample, edtNote;
    private TextView tvTitle;
    private Button btnSaveWord;

    private WordDAO wordDAO;
    private int setId;
    private int wordId;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        setId = getIntent().getIntExtra("set_id", 0);
        if(setId == 0){
            Toast.makeText(this, "Không tìm thấy bộ từ", Toast.LENGTH_SHORT).show();
            finish();
        }
        wordDAO = new WordDAO(this);
        initViews();
        editMode = getIntent().getBooleanExtra("edit_mode", false);
        if(editMode){
            setupEditMode();
        }
        setupListeners();

    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilEnglish = findViewById(R.id.tilEnglish);
        tilMeaning = findViewById(R.id.tilMeaning);
        tvTitle = findViewById(R.id.tvTitle);
        edtExample = findViewById(R.id.edtExample);
        edtNote = findViewById(R.id.edtNote);
        edtEnglish = findViewById(R.id.edtEnglish);
        edtMeaning = findViewById(R.id.edtMeaning);
        edtPronunciation = findViewById(R.id.edtPronunciation);
        btnSaveWord = findViewById(R.id.btnSaveWord);
    }

    private void setupEditMode(){
        tvTitle.setText("Sửa từ");
        btnSaveWord.setText("Lưu");

        wordId = getIntent().getIntExtra("word_id", 0);
        if(wordId != 0){
            Word word = wordDAO.getWordById(wordId);
            if(word != null){
                edtEnglish.setText(word.getEnglish());
                edtMeaning.setText(word.getMeaning());
                edtPronunciation.setText(word.getPronunciation());
                edtExample.setText(word.getExample());
                edtNote.setText(word.getNote());

            }else {
                Toast.makeText(this, "Không tìm thấy từ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void saveWord() {
        String english = edtEnglish.getText().toString().trim();
        String meaning = edtMeaning.getText().toString().trim();
        String pronunciation = edtPronunciation.getText().toString().trim();
        String example = edtExample.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        boolean valid = true;
        if(english.isEmpty()){
            tilEnglish.setError("Vui lòng nhập từ tiếng Anh");
            valid = false;
        }else {
            tilEnglish.setError(null);
        }
        if(meaning.isEmpty()){
            tilMeaning.setError("Vui lòng nhập nghĩa tiếng Việt");
            valid = false;
        }else {
            tilMeaning.setError(null);
        }
        if(!valid){
            return;
        }

        Word word = new Word();
        if(editMode && wordId != 0){
            word.setId(wordId);
            word.setSetId(setId);
            word.setEnglish(english);
            word.setMeaning(meaning);
            word.setPronunciation(pronunciation);
            word.setExample(example);
            word.setNote(note);
            if(wordDAO.update(word) > 0){
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            word.setSetId(setId);
            word.setEnglish(english);
            word.setMeaning(meaning);
            word.setPronunciation(pronunciation);
            word.setExample(example);
            word.setNote(note);

            if(wordDAO.insert(word) > 0){
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                clearForm();
                finish();
                }else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupListeners(){
        btnBack.setOnClickListener(v -> finish());
        btnSaveWord.setOnClickListener(v -> saveWord());
    }

    private void clearForm(){
        edtEnglish.setText("");
        edtMeaning.setText("");
        edtPronunciation.setText("");
        edtExample.setText("");
        edtNote.setText("");
    }
}
