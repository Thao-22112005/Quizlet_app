package com.example.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishvocabulary.models.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordDAO {

    private DatabaseHelper dbHelper;
    public WordDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(Word word) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();


        // ID BỘ TỪ

        values.put(
                "set_id",
                word.getSetId()
        );


        // TỪ TIẾNG ANH

        values.put(
                "english",
                word.getEnglish()
        );


        // LOẠI TỪ

        values.put(
                "loai_tu",
                word.getLoaiTu()
        );


        // PHIÊN ÂM

        values.put(
                "pronunciation",
                word.getPronunciation()
        );


        // NGHĨA TIẾNG VIỆT

        values.put(
                "meaning",
                word.getMeaning()
        );


        // VÍ DỤ

        values.put(
                "example",
                word.getExample()
        );


        // GHI CHÚ

        values.put(
                "note",
                word.getNote()
        );


        // MẶC ĐỊNH CHƯA HỌC

        values.put(
                "is_learned",
                word.getIsLearned()
        );


        long id =
                db.insert(
                        DatabaseHelper.TABLE_WORD,
                        null,
                        values
                );

        db.close();

        return id;
    }

    public Word getById(int id) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_WORD,

                        null,

                        "id = ?",

                        new String[]{
                                String.valueOf(id)
                        },

                        null,
                        null,
                        null
                );


        Word word = null;

        if (cursor.moveToFirst()) {
            word = cursorToWord(cursor);
        }


        cursor.close();
        db.close();

        return word;
    }

    public List<Word> getBySetId(int setId) {

        List<Word> list =
                new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_WORD,

                        null,

                        "set_id = ?",

                        new String[]{
                                String.valueOf(setId)
                        },

                        null,
                        null,

                        "id ASC"
                );


        while (cursor.moveToNext()) {

            list.add(
                    cursorToWord(cursor)
            );
        }


        cursor.close();
        db.close();

        return list;
    }

    public int update(Word word) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();


        values.put(
                "english",
                word.getEnglish()
        );


        values.put(
                "loai_tu",
                word.getLoaiTu()
        );


        values.put(
                "pronunciation",
                word.getPronunciation()
        );


        values.put(
                "meaning",
                word.getMeaning()
        );


        values.put(
                "example",
                word.getExample()
        );


        values.put(
                "note",
                word.getNote()
        );


        values.put(
                "is_learned",
                word.getIsLearned()
        );


        int result =
                db.update(
                        DatabaseHelper.TABLE_WORD,

                        values,

                        "id = ?",

                        new String[]{
                                String.valueOf(
                                        word.getId()
                                )
                        }
                );


        db.close();

        return result;
    }

    public int delete(int id) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        int result =
                db.delete(
                        DatabaseHelper.TABLE_WORD,

                        "id = ?",

                        new String[]{
                                String.valueOf(id)
                        }
                );


        db.close();

        return result;
    }


    private Word cursorToWord(Cursor cursor) {

        Word word =
                new Word();


        word.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "id"
                        )
                )
        );


        word.setSetId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "set_id"
                        )
                )
        );


        word.setEnglish(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "english"
                        )
                )
        );


        word.setLoaiTu(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "loai_tu"
                        )
                )
        );


        word.setPronunciation(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "pronunciation"
                        )
                )
        );


        word.setMeaning(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "meaning"
                        )
                )
        );


        word.setExample(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "example"
                        )
                )
        );


        word.setNote(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "note"
                        )
                )
        );


        word.setIsLearned(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "is_learned"
                        )
                )
        );


        return word;
    }

    public int getWordCountBySetId(int setId) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " +
                        DatabaseHelper.TABLE_WORD +
                        " WHERE set_id = ?",
                new String[]{
                        String.valueOf(setId)
                }
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public int getLearnedWordCount(int setId) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        String sql =
                "SELECT COUNT(*) " +
                        "FROM " + DatabaseHelper.TABLE_WORD +
                        " WHERE set_id = ? " +
                        "AND is_learned = 1";

        Cursor cursor =
                db.rawQuery(
                        sql,
                        new String[]{
                                String.valueOf(setId)
                        }
                );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public boolean updateLearnedStatus(int wordId, int isLearned) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_learned", isLearned);

        int result = db.update(
                DatabaseHelper.TABLE_WORD,
                values,
                "id = ?",
                new String[]{String.valueOf(wordId)}
        );

        return result > 0;
    }

    public int getLearnedWordCountByUser(String userUid) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        String sql =
                "SELECT COUNT(*) " +
                        "FROM " + DatabaseHelper.TABLE_WORD + " w " +
                        "INNER JOIN " + DatabaseHelper.TABLE_VOCABULARY_SET + " s " +
                        "ON w.set_id = s.id " +
                        "WHERE s.user_uid = ? " +
                        "AND w.is_learned = 1";

        Cursor cursor =
                db.rawQuery(
                        sql,
                        new String[]{userUid}
                );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public int getUnlearnedWordCountByUser(String userUid) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        String sql =
                "SELECT COUNT(*) " +
                        "FROM " + DatabaseHelper.TABLE_WORD + " w " +
                        "INNER JOIN " + DatabaseHelper.TABLE_VOCABULARY_SET + " s " +
                        "ON w.set_id = s.id " +
                        "WHERE s.user_uid = ? " +
                        "AND w.is_learned = 0";

        Cursor cursor =
                db.rawQuery(
                        sql,
                        new String[]{userUid}
                );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    // Kiểm tra từ đã tồn tại chưa
    public boolean isDuplicate(int setId, String english, String loaiTu) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Chuẩn hoá loại từ trước khi so sánh
        String normalizedLoaiTu = normalizeLoaiTu(loaiTu);

        String where = "set_id = ? AND english = ? COLLATE NOCASE";
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(setId));
        args.add(english.trim());

        // Nếu có loại từ → check cả loại từ
        if (normalizedLoaiTu != null && !normalizedLoaiTu.isEmpty()) {
            where += " AND loai_tu = ?";
            args.add(normalizedLoaiTu.trim());
        }

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_WORD
                        + " WHERE " + where,
                args.toArray(new String[0]));

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count > 0;  // true = đã trùng
    }

    // Check trùng nhưng loại trừ 1 ID (dùng khi sửa)
    public boolean isDuplicateExcept(int setId, String english,
                                     String loaiTu, int excludeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Chuẩn hoá loại từ trước khi so sánh
        String normalizedLoaiTu = normalizeLoaiTu(loaiTu);

        String where = "set_id = ? AND english = ? COLLATE NOCASE AND id != ?";
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(setId));
        args.add(english.trim());
        args.add(String.valueOf(excludeId));

        if (normalizedLoaiTu != null && !normalizedLoaiTu.isEmpty()) {
            where += " AND loai_tu = ?";
            args.add(normalizedLoaiTu.trim());
        }

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_WORD
                        + " WHERE " + where,
                args.toArray(new String[0]));

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0;
    }

    // =====================================================
    // CHUẨN HOÁ LOẠI TỪ
    // =====================================================

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

    /**
     * Chuẩn hoá loại từ: n/noun/(n)/noun (n) → "(n) Danh từ"
     */
    public static String normalizeLoaiTu(String input) {
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