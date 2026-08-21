package com.example.englishvocabulary.activities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.Word;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickAddActivity extends AppCompatActivity {

    // 1. View
    private ImageButton btnBack;
    private TextInputEditText edtBulkInput;
    private Button btnPreview, btnSaveAll;
    private TextView tvPreviewTitle, tvPreviewResult;

    // 2. Data
    private WordDAO wordDAO;
    private int setId;
    private List<Word> validWords = new ArrayList<>();

    // Map chuẩn hoá loại từ
    private static final Map<String, String> LOAI_TU_MAP = new HashMap<>();
    static {
        LOAI_TU_MAP.put("n", "(n) Danh từ");
        LOAI_TU_MAP.put("noun", "(n) Danh từ");
        LOAI_TU_MAP.put("(n)", "(n) Danh từ");
        LOAI_TU_MAP.put("v", "(v) Động từ");
        LOAI_TU_MAP.put("verb", "(v) Động từ");
        LOAI_TU_MAP.put("(v)", "(v) Động từ");
        LOAI_TU_MAP.put("adj", "(adj) Tính từ");
        LOAI_TU_MAP.put("adjective", "(adj) Tính từ");
        LOAI_TU_MAP.put("(adj)", "(adj) Tính từ");
        LOAI_TU_MAP.put("adv", "(adv) Trạng từ");
        LOAI_TU_MAP.put("adverb", "(adv) Trạng từ");
        LOAI_TU_MAP.put("(adv)", "(adv) Trạng từ");
        LOAI_TU_MAP.put("prep", "(prep) Giới từ");
        LOAI_TU_MAP.put("preposition", "(prep) Giới từ");
        LOAI_TU_MAP.put("(prep)", "(prep) Giới từ");
        LOAI_TU_MAP.put("conj", "(conj) Liên từ");
        LOAI_TU_MAP.put("conjunction", "(conj) Liên từ");
        LOAI_TU_MAP.put("(conj)", "(conj) Liên từ");
        LOAI_TU_MAP.put("pron", "(pron) Đại từ");
        LOAI_TU_MAP.put("pronoun", "(pron) Đại từ");
        LOAI_TU_MAP.put("(pron)", "(pron) Đại từ");
        LOAI_TU_MAP.put("det", "(det) Mạo từ");
        LOAI_TU_MAP.put("determiner", "(det) Mạo từ");
        LOAI_TU_MAP.put("(det)", "(det) Mạo từ");
        LOAI_TU_MAP.put("interj", "(interj) Thán từ");
        LOAI_TU_MAP.put("interjection", "(interj) Thán từ");
        LOAI_TU_MAP.put("(interj)", "(interj) Thán từ");
        LOAI_TU_MAP.put("phr", "(phr) Cụm từ");
        LOAI_TU_MAP.put("phrase", "(phr) Cụm từ");
        LOAI_TU_MAP.put("(phr)", "(phr) Cụm từ");
        LOAI_TU_MAP.put("idiom", "(idiom) Thành ngữ");
        LOAI_TU_MAP.put("(idiom)", "(idiom) Thành ngữ");
    }

    // 3. onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_add);

        setId = getIntent().getIntExtra("set_id", -1);
        if (setId == -1) {
            Toast.makeText(this,
                    "Lỗi: Không tìm thấy bộ từ",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        wordDAO = new WordDAO(this);
        initViews();
        setupListeners();
    }

    // 4. Ánh xạ View
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtBulkInput = findViewById(R.id.edtBulkInput);
        btnPreview = findViewById(R.id.btnPreview);
        btnSaveAll = findViewById(R.id.btnSaveAll);
        tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        tvPreviewResult = findViewById(R.id.tvPreviewResult);
    }

    // 5. Listeners
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPreview.setOnClickListener(v -> parseAndPreview());
        btnSaveAll.setOnClickListener(v -> saveAllWords());
    }

    // 6. Parse input + hiển thị preview
    private void parseAndPreview() {
        String input = edtBulkInput.getText()
                .toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập từ vựng",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        validWords.clear();
        String[] lines = input.split("\n");

        SpannableStringBuilder preview =
                new SpannableStringBuilder();
        int validCount = 0;
        int errorCount = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Bỏ qua dòng trống
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\|");

            // Lấy các trường (6 trường)
            String english = parts.length > 0
                    ? parts[0].trim() : "";
            String loaiTu = parts.length > 1
                    ? normalizeLoaiTu(parts[1].trim()) : "";
            String pronunciation = parts.length > 2
                    ? parts[2].trim() : "";
            String meaning = parts.length > 3
                    ? parts[3].trim() : "";
            String example = parts.length > 4
                    ? parts[4].trim() : "";
            String note = parts.length > 5
                    ? parts[5].trim() : "";

            // Validate
            if (english.isEmpty() || meaning.isEmpty()) {
                // LỖI - thiếu trường bắt buộc
                errorCount++;
                String errorLine = "❌ Dòng " + (i + 1) + ": ";
                if (english.isEmpty()) {
                    errorLine += "thiếu từ tiếng Anh";
                } else {
                    errorLine += "thiếu nghĩa";
                }
                errorLine += "\n";

                int start = preview.length();
                preview.append(errorLine);
                preview.setSpan(
                        new ForegroundColorSpan(0xFFEF4444),
                        start, preview.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if (wordDAO.isDuplicate(setId, english, loaiTu)) {
                // TRÙNG - từ đã tồn tại
                errorCount++;
                String warnLine = "⚠️ Dòng " + (i + 1)
                        + ": \"" + english + "\" đã tồn tại\n";

                int start = preview.length();
                preview.append(warnLine);
                preview.setSpan(
                        new ForegroundColorSpan(0xFFF59E0B),
                        start, preview.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                // HỢP LỆ
                validCount++;
                Word word = new Word();
                word.setSetId(setId);
                word.setEnglish(english);
                word.setLoaiTu(loaiTu);
                word.setPronunciation(pronunciation);
                word.setMeaning(meaning);
                word.setExample(example);
                word.setNote(note);
                word.setIsLearned(0);
                validWords.add(word);

                String okLine = "✅ " + english
                        + " → " + meaning + "\n";
                int start = preview.length();
                preview.append(okLine);
                preview.setSpan(
                        new ForegroundColorSpan(0xFF10B981),
                        start, preview.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        // Hiển thị kết quả
        tvPreviewTitle.setVisibility(View.VISIBLE);
        tvPreviewTitle.setText(
                "Kết quả: " + validCount + " hợp lệ, "
                        + errorCount + " lỗi");
        tvPreviewResult.setVisibility(View.VISIBLE);
        tvPreviewResult.setText(preview);

        if (validCount > 0) {
            btnSaveAll.setVisibility(View.VISIBLE);
            btnSaveAll.setText(
                    "Lưu " + validCount + " từ hợp lệ");
        } else {
            btnSaveAll.setVisibility(View.GONE);
        }
    }

    // 7. Lưu tất cả từ hợp lệ vào DB
    private void saveAllWords() {
        int saved = 0;
        for (Word word : validWords) {
            long result = wordDAO.insert(word);
            if (result > 0) saved++;
        }

        Toast.makeText(this,
                "Đã thêm " + saved + " từ!",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Chuẩn hoá loại từ: n/noun/(n)/noun (n) → "(n) Danh từ"
     * Hỗ trợ nhiều format: "n", "noun", "(n)", "noun (n)", "(n) noun",...
     */
    private String normalizeLoaiTu(String input) {
        if (input == null || input.isEmpty()) return "";
        String key = input.toLowerCase().trim();

        // 1. Exact match
        String normalized = LOAI_TU_MAP.get(key);
        if (normalized != null) return normalized;

        // 2. Trích xuất viết tắt từ ngoặc: "noun (n)" → thử "(n)"
        int start = key.indexOf('(');
        int end = key.indexOf(')');
        if (start != -1 && end > start) {
            String abbr = key.substring(start, end + 1).trim();
            normalized = LOAI_TU_MAP.get(abbr);
            if (normalized != null) return normalized;
        }

        // 3. Thử từng key đã biết (ưu tiên key dài hơn)
        String bestMatch = null;
        int bestLen = 0;
        for (Map.Entry<String, String> entry : LOAI_TU_MAP.entrySet()) {
            String mapKey = entry.getKey();
            if (key.contains(mapKey) && mapKey.length() > bestLen) {
                bestMatch = entry.getValue();
                bestLen = mapKey.length();
            }
        }
        if (bestMatch != null) return bestMatch;

        return input;
    }
}
