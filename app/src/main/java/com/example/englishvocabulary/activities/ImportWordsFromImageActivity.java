package com.example.englishvocabulary.activities;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.Word;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImportWordsFromImageActivity extends AppCompatActivity {

    private static final String TAG = "OCR_RESULT";

    private int setId;

    private TextRecognizer textRecognizer;

    private ActivityResultLauncher<String> imagePickerLauncher;

    private WordDAO wordDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_import_words_from_image
        );

        setId = getIntent().getIntExtra(
                "set_id",
                -1
        );

        if (setId == -1) {

            Toast.makeText(
                    this,
                    "Không tìm thấy bộ từ",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        wordDAO = new WordDAO(this);

        textRecognizer =
                TextRecognition.getClient(
                        TextRecognizerOptions.DEFAULT_OPTIONS
                );

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {

                            if (uri != null) {
                                recognizeTextFromImage(uri);
                            }
                        }
                );

        // mở gallery
        imagePickerLauncher.launch("image/*");
    }


    // OCR
    private void recognizeTextFromImage(Uri imageUri) {

        try {

            InputImage image =
                    InputImage.fromFilePath(
                            this,
                            imageUri
                    );

            textRecognizer
                    .process(image)

                    .addOnSuccessListener(result -> {

                        Log.d(
                                TAG,
                                "=============================="
                        );

                        Log.d(
                                TAG,
                                "OCR RAW:"
                        );

                        Log.d(
                                TAG,
                                result.getText()
                        );

                        Log.d(
                                TAG,
                                "=============================="
                        );

                        parseTable(result);
                    })

                    .addOnFailureListener(e -> {

                        Log.e(
                                TAG,
                                "OCR FAILED",
                                e
                        );

                        Toast.makeText(
                                this,
                                "Không thể đọc ảnh",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Cannot process image",
                    e
            );

            Toast.makeText(
                    this,
                    "Không thể xử lý ảnh",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // OCR LINE
    private static class OCRLine {

        String text;

        Rect box;

        OCRLine(
                String text,
                Rect box
        ) {

            this.text = text;
            this.box = box;
        }

        int centerX() {
            return box.centerX();
        }

        int centerY() {
            return box.centerY();
        }
    }

    // WORD ROW
    private static class WordRow {

        String english = "";

        String pronunciation = "";

        String loaiTu = "";

        String meaning = "";

        String example = "";
    }

    // PARSE TABLE
    private void parseTable(Text result) {

        List<OCRLine> lines = new ArrayList<>();

        // LẤY TẤT CẢ OCR LINE
        for (Text.TextBlock block : result.getTextBlocks()) {

            for (Text.Line line : block.getLines()) {

                Rect box = line.getBoundingBox();

                if (box == null) {
                    continue;
                }

                String text =
                        line.getText()
                                .trim();

                if (text.isEmpty()) {
                    continue;
                }

                lines.add(
                        new OCRLine(
                                text,
                                box
                        )
                );
            }
        }


        if (lines.isEmpty()) {

            Toast.makeText(
                    this,
                    "Không tìm thấy nội dung trong ảnh",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // IMAGE WIDTH
        int imageWidth =
                getImageMaxX(lines);
        Log.d(
                TAG,
                "IMAGE WIDTH = "
                        + imageWidth
        );

        // SORT
        sortByY(lines);

        // DEBUG
        Log.d(
                TAG,
                "===== ALL OCR LINES ====="
        );

        for (OCRLine line : lines) {

            Log.d(
                    TAG,
                    line.text
                            + " | X="
                            + line.centerX()
                            + " | Y="
                            + line.centerY()
                            + " | X%="
                            + getXPercent(
                            line,
                            imageWidth
                    )
            );
        }

        // TÌM CÁC LINE TỪ VỰNG
        List<OCRLine> englishLines =
                new ArrayList<>();


        for (OCRLine line : lines) {

            float xPercent =
                    getXPercent(
                            line,
                            imageWidth
                    );

            String text =
                    line.text.trim();


            if (isHeader(text)
                    || isNoise(text)) {

                continue;
            }

            // CỘT TỪ VỰNG
            if (xPercent >= 12f
                    && xPercent < 31f) {

                englishLines.add(line);
            }
        }


        sortByY(englishLines);

        // DEBUG WORD LINES
        Log.d(
                TAG,
                "===== ENGLISH LINES ====="
        );

        for (OCRLine line : englishLines) {

            Log.d(
                    TAG,
                    "WORD = "
                            + line.text
                            + " | Y="
                            + line.centerY()
            );
        }


        if (englishLines.isEmpty()) {

            Toast.makeText(
                    this,
                    "Không tìm thấy từ vựng trong bảng",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =================================================
        // GOM CÁC LINE THUỘC CÙNG MỘT TỪ
        //
        // Ví dụ:
        //
        // Stay-at-home
        // parent
        //
        // => Stay-at-home parent
        // =================================================

        List<List<OCRLine>> wordGroups =
                groupEnglishLines(
                        englishLines
                );


        Log.d(
                TAG,
                "================================"
        );

        Log.d(
                TAG,
                "WORD GROUPS = "
                        + wordGroups.size()
        );

        Log.d(
                TAG,
                "================================"
        );


        for (List<OCRLine> group : wordGroups) {

            Log.d(
                    TAG,
                    "GROUP = "
                            + joinLines(group)
            );
        }

        // TẠO ROW
        List<WordRow> rows =
                new ArrayList<>();


        for (int i = 0;
             i < wordGroups.size();
             i++) {

            List<OCRLine> currentGroup =
                    wordGroups.get(i);


            int currentY =
                    getGroupCenterY(
                            currentGroup
                    );


            int startY;

            int endY;

            // START Y
            if (i == 0) {

                /*
                 * Không lấy header.
                 * Header nằm khoảng Y=737
                 * Sibling nằm khoảng Y=764
                 * Lùi 20px là đủ.
                 */

                startY =
                        currentY - 20;

            } else {

                int previousY =
                        getGroupCenterY(
                                wordGroups.get(i - 1)
                        );

                startY =
                        (previousY + currentY) / 2;
            }

            // END Y
            if (i + 1 < wordGroups.size()) {

                int nextY =
                        getGroupCenterY(
                                wordGroups.get(i + 1)
                        );

                endY =
                        (currentY + nextY) / 2;

            } else {

                endY =
                        Integer.MAX_VALUE;
            }

            // TẠO ROW
            WordRow row =
                    createRowFromArea(
                            lines,
                            imageWidth,
                            startY,
                            endY
                    );


            if (row != null) {

                rows.add(row);
            }
        }

        // LOG RESULT
        Log.d(
                TAG,
                "================================"
        );

        Log.d(
                TAG,
                "WORDS PARSED = "
                        + rows.size()
        );

        Log.d(
                TAG,
                "================================"
        );


        for (WordRow row : rows) {

            Log.d(
                    TAG,
                    "WORD = "
                            + row.english
                            + " | TYPE = "
                            + row.loaiTu
                            + " | MEANING = "
                            + row.meaning
                            + " | EXAMPLE = "
                            + row.example
            );
        }

        // SAVE
        showImportConfirmation(rows);
    }

    // GROUP ENGLISH LINES
    private List<List<OCRLine>> groupEnglishLines(
            List<OCRLine> englishLines) {

        List<List<OCRLine>> groups =
                new ArrayList<>();


        if (englishLines.isEmpty()) {
            return groups;
        }


        sortByY(englishLines);


        List<OCRLine> currentGroup =
                new ArrayList<>();


        currentGroup.add(
                englishLines.get(0)
        );


        /*
         * Khoảng cách tối đa để coi là cùng một
         * từ nhiều dòng.
         *
         * Trong ảnh:
         *
         * Stay-at-home = 994
         * parent        = 1021
         *
         * khoảng cách = 27
         *
         * Trong khi:
         *
         * parent             = 1021
         * Household chores   = 1056
         *
         * khoảng cách = 35
         *
         * Vì vậy chọn 30.
         */

        final int MAX_SAME_WORD_GAP = 30;


        for (int i = 1;
             i < englishLines.size();
             i++) {

            OCRLine previous =
                    englishLines.get(i - 1);

            OCRLine current =
                    englishLines.get(i);


            int gap =
                    current.centerY()
                            - previous.centerY();


            if (gap <= MAX_SAME_WORD_GAP) {

                // Cùng một từ
                currentGroup.add(current);

            } else {

                // Từ mới
                groups.add(
                        currentGroup
                );

                currentGroup =
                        new ArrayList<>();

                currentGroup.add(current);
            }
        }


        if (!currentGroup.isEmpty()) {

            groups.add(
                    currentGroup
            );
        }


        return groups;
    }

    // GROUP CENTER Y
    private int getGroupCenterY(
            List<OCRLine> group) {

        if (group == null
                || group.isEmpty()) {

            return 0;
        }


        int minY =
                Integer.MAX_VALUE;

        int maxY =
                Integer.MIN_VALUE;


        for (OCRLine line : group) {

            minY =
                    Math.min(
                            minY,
                            line.centerY()
                    );

            maxY =
                    Math.max(
                            maxY,
                            line.centerY()
                    );
        }


        return (minY + maxY) / 2;
    }


    // CREATE ROW
    private WordRow createRowFromArea(
            List<OCRLine> allLines,
            int imageWidth,
            int startY,
            int endY) {


        List<OCRLine> english =
                new ArrayList<>();

        List<OCRLine> type =
                new ArrayList<>();

        List<OCRLine> meaning =
                new ArrayList<>();

        List<OCRLine> example =
                new ArrayList<>();


        // DUYỆT OCR LINE
        for (OCRLine line : allLines) {

            int y =
                    line.centerY();


            if (y < startY
                    || y >= endY) {

                continue;
            }


            float xPercent =
                    getXPercent(
                            line,
                            imageWidth
                    );


            String text =
                    line.text.trim();


            if (isHeader(text)
                    || isNoise(text)) {

                continue;
            }


            // STT
            if (xPercent < 12f) {
                continue;
            }


            // TỪ VỰNG
            if (xPercent >= 12f
                    && xPercent < 31f) {

                english.add(line);

                continue;
            }


            // TỪ LOẠI

            if (xPercent >= 31f
                    && xPercent < 39f) {

                type.add(line);

                continue;
            }


            // NGHĨA
            if (xPercent >= 39f
                    && xPercent < 56f) {

                meaning.add(line);

                continue;
            }

            // EXAMPLE
            if (xPercent >= 56f) {

                example.add(line);
            }
        }

        // KHÔNG CÓ TỪ
        if (english.isEmpty()) {

            return null;
        }


        // CREATE ROW
        WordRow row =
                new WordRow();


        row.english =
                cleanText(
                        joinLines(english)
                );


        row.loaiTu =
                normalizeType(
                        joinLines(type)
                );


        row.meaning =
                cleanText(
                        joinLines(meaning)
                );


        row.example =
                cleanText(
                        joinLines(example)
                );


        return row;
    }

    // JOIN LINES
    private String joinLines(
            List<OCRLine> lines) {

        if (lines == null
                || lines.isEmpty()) {

            return "";
        }


        sortByY(lines);


        StringBuilder result =
                new StringBuilder();


        for (OCRLine line : lines) {

            String text =
                    line.text
                            .trim();


            if (text.isEmpty()) {
                continue;
            }


            if (result.length() > 0) {

                result.append(" ");
            }


            result.append(text);
        }


        return result
                .toString()
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }

    // X %
    private float getXPercent(
            OCRLine line,
            int imageWidth) {

        if (imageWidth <= 0) {
            return 0;
        }


        return line.centerX()
                * 100f
                / imageWidth;
    }


    // IMAGE WIDTH
    private int getImageMaxX(
            List<OCRLine> lines) {

        int maxX = 0;


        for (OCRLine line : lines) {

            maxX =
                    Math.max(
                            maxX,
                            line.box.right
                    );
        }


        return maxX;
    }

    // HEADER
    private boolean isHeader(
            String text) {

        if (text == null) {
            return true;
        }


        String value =
                text.toLowerCase()
                        .trim();


        // HEADER

        return value.equals("stt")

                || value.equals("từ vựng")
                || value.equals("tu vung")

                || value.equals("từ loại")
                || value.equals("tu loai")
                || value.equals("từ loai")

                || value.equals("nghĩa tiếng việt")
                || value.equals("nghia tieng viet")
                || value.equals("nghĩa tieng viet")

                || value.equals("ví dụ tiếng anh")
                || value.equals("vi du tieng anh");
    }

    // NOISE
    private boolean isNoise(
            String text) {

        if (text == null) {
            return true;
        }


        String value =
                text.toLowerCase()
                        .trim();


        if (value.isEmpty()) {
            return true;
        }


        if (value.contains("mapstudy")) {
            return true;
        }


        if (value.contains("30 chuyên đề")) {
            return true;
        }


        if (value.equals("family life")) {
            return true;
        }


        if (value.equals("học online tại mapstudy")) {
            return true;
        }


        return false;
    }

    // NUMBER
    private boolean isNumber(
            String text) {

        if (text == null) {
            return false;
        }


        String value =
                text.trim();


        return value.matches(
                "^\\d{1,3}\\.?$"
        );
    }


    // NORMALIZE TYPE

    private String normalizeType(
            String type) {

        if (type == null) {
            return "";
        }


        String value =
                type.trim()
                        .toLowerCase()
                        .replaceAll(
                                "\\s+",
                                " "
                        );


        // OCR ADJ
        if (value.equals("adi")
                || value.equals("ad")
                || value.equals("adj")) {

            return "adj";
        }

        // NOUN
        if (value.equals("n")) {
            return "n";
        }

        // VERB
        if (value.equals("v")) {
            return "v";
        }


        if (value.equals("verb")) {
            return "v";
        }


        // PHRASAL VERB
        if (value.equals("phrasal")
                || value.equals("phrasal verb")) {

            return "phrasal verb";
        }


        // =================================================
        // IDIOM
        // =================================================

        if (value.equals("idiom")) {
            return "idiom";
        }


        return value;
    }


    // =====================================================
    // CLEAN TEXT
// =====================================================

    private String cleanText(
            String text) {

        if (text == null) {
            return "";
        }


        return text
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }


    // =====================================================
    // SORT Y
    // =====================================================

    private void sortByY(
            List<OCRLine> lines) {

        Collections.sort(
                lines,
                Comparator.comparingInt(
                        OCRLine::centerY
                )
        );
    }

    private void showImportConfirmation(
            List<WordRow> rows
    ) {

        if (rows == null || rows.isEmpty()) {

            Toast.makeText(
                    this,
                    "Không đọc được từ vựng nào",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =================================================
        // TỪ ĐẦU TIÊN
        // =================================================

        WordRow firstWord =
                rows.get(0);


        // =================================================
        // INFLATE FORM
        // =================================================

        android.view.View view =
                getLayoutInflater().inflate(
                        R.layout.activity_confirm_import_words,
                        null
                );


        android.widget.TextView tvImportTitle =
                view.findViewById(
                        R.id.tvImportTitle
                );

        android.widget.TextView tvEnglish =
                view.findViewById(
                        R.id.tvEnglish
                );

        android.widget.TextView tvType =
                view.findViewById(
                        R.id.tvType
                );

        android.widget.TextView tvMeaning =
                view.findViewById(
                        R.id.tvMeaning
                );

        android.widget.TextView tvPronunciation =
                view.findViewById(
                        R.id.tvPronunciation
                );

        android.widget.TextView tvExample =
                view.findViewById(
                        R.id.tvExample
                );

        android.widget.Button btnCancel =
                view.findViewById(
                        R.id.btnCancel
                );

        android.widget.Button btnConfirm =
                view.findViewById(
                        R.id.btnConfirm
                );


        // =================================================
        // HIỂN THỊ SỐ LƯỢNG
        // =================================================

        tvImportTitle.setText(
                "Đã đọc được "
                        + rows.size()
                        + " từ vựng"
        );


        // =================================================
        // HIỂN THỊ TỪ ĐẦU TIÊN
        // =================================================

        tvEnglish.setText("Từ vựng: "+
                firstWord.english
        );


        tvType.setText(
                "Loại từ: "
                        + (firstWord.loaiTu.isEmpty()
                        ? "Không có"
                        : firstWord.loaiTu)
        );


        tvMeaning.setText(
                "Nghĩa: "
                        + (firstWord.meaning.isEmpty()
                        ? "Không có"
                        : firstWord.meaning)
        );


        tvPronunciation.setText(
                "Phiên âm: "
                        + (firstWord.pronunciation.isEmpty()
                        ? "Không có"
                        : firstWord.pronunciation)
        );


        tvExample.setText(
                "Ví dụ: "
                        + (firstWord.example.isEmpty()
                        ? "Không có"
                        : firstWord.example)
        );


        // =================================================
        // TẠO DIALOG
        // =================================================

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(
                        this
                )
                        .setView(view)
                        .setCancelable(false)
                        .create();


        // =================================================
        // HỦY
        // =================================================

        btnCancel.setOnClickListener(v -> {

            dialog.dismiss();

            Toast.makeText(
                    this,
                    "Đã hủy thêm từ vựng",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });


        // =================================================
        // XÁC NHẬN
        // =================================================

        btnConfirm.setOnClickListener(v -> {

            dialog.dismiss();

            saveWords(rows);
        });


        dialog.show();
    }


    // =====================================================
    // SAVE SQLITE
    // =====================================================

    private void saveWords(
            List<WordRow> rows) {

        int inserted = 0;

        int duplicated = 0;

        int invalid = 0;


        for (WordRow row : rows) {

            String english =
                    row.english.trim();


            String meaning =
                    row.meaning.trim();


            String loaiTu =
                    WordDAO.normalizeLoaiTu(
                            row.loaiTu
                    );


            // =================================================
            // KHÔNG CÓ TỪ
            // =================================================

            if (english.isEmpty()) {

                invalid++;

                continue;
            }


            // =================================================
            // KHÔNG CÓ NGHĨA
            // =================================================

            if (meaning.isEmpty()) {

                invalid++;

                Log.d(
                        TAG,
                        "BỎ QUA - KHÔNG CÓ NGHĨA: "
                                + english
                );

                continue;
            }


            // =================================================
            // CHECK DUPLICATE
            // =================================================

            if (wordDAO.isDuplicate(
                    setId,
                    english,
                    loaiTu
            )) {

                duplicated++;

                Log.d(
                        TAG,
                        "DUPLICATE: "
                                + english
                );

                continue;
            }


            // =================================================
            // CREATE WORD
            // =================================================

            Word word =
                    new Word();


            word.setSetId(
                    setId
            );


            word.setEnglish(
                    english
            );


            word.setLoaiTu(
                    loaiTu
            );

            word.setPronunciation(
                    ""
            );


            word.setMeaning(
                    meaning
            );


            word.setExample(
                    row.example
            );


            word.setNote(
                    ""
            );


            word.setIsLearned(
                    0
            );

            // INSERT
            long id =
                    wordDAO.insert(word);


            if (id > 0) {

                inserted++;

                Log.d(
                        TAG,
                        "INSERT OK: "
                                + english
                );

            } else {

                Log.e(
                        TAG,
                        "INSERT FAILED: "
                                + english
                );
            }
        }

        // RESULT
        String message =
                "Đã thêm "
                        + inserted
                        + " từ";


        if (duplicated > 0) {

            message +=
                    " • Bỏ qua "
                            + duplicated
                            + " từ trùng";
        }


        if (invalid > 0) {

            message +=
                    " • Bỏ qua "
                            + invalid
                            + " dòng lỗi";
        }


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();


        Log.d(
                TAG,
                "================================"
        );

        Log.d(
                TAG,
                "INSERTED   = " + inserted
        );

        Log.d(
                TAG,
                "DUPLICATED = " + duplicated
        );

        Log.d(
                TAG,
                "INVALID    = " + invalid
        );

        Log.d(
                TAG,
                "================================"
        );


        finish();
    }


    // =====================================================
    // DESTROY
    // =====================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (textRecognizer != null) {

            textRecognizer.close();
        }
    }
}