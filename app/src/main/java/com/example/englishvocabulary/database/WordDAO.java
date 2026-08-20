package com.example.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishvocabulary.models.Word;

import java.util.ArrayList;
import java.util.List;

public class WordDAO {

    private DatabaseHelper dbHelper;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public WordDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    // =====================================================
    // THÊM TỪ VỰNG
    // =====================================================

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


    // =====================================================
    // LẤY TỪ VỰNG THEO ID
    // =====================================================

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


    // =====================================================
    // LẤY DANH SÁCH TỪ THEO BỘ TỪ
    // =====================================================

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


    // =====================================================
    // CẬP NHẬT TỪ VỰNG
    // =====================================================

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


    // =====================================================
    // XÓA TỪ VỰNG
    // =====================================================

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


    // =====================================================
    // CHUYỂN CURSOR → WORD
    // =====================================================

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
                "SELECT COUNT(DISTINCT word_id) " +
                        "FROM LEARNING_HISTORY " +
                        "WHERE set_id = ? " +
                        "AND is_correct = 1";

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
}