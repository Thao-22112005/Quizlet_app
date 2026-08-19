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


    public WordDAO(Context context) {

        dbHelper =
                new DatabaseHelper(context);
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
    // LẤY 1 TỪ THEO ID
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

            word =
                    cursorToWord(cursor);
        }


        cursor.close();

        db.close();


        return word;
    }


    // =====================================================
    // LẤY CÁC TỪ CỦA 1 BỘ TỪ
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

            Word word =
                    cursorToWord(cursor);

            list.add(word);
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


        // TỪ TIẾNG ANH

        values.put(
                "english",
                word.getEnglish()
        );

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
    // CURSOR → OBJECT
    // =====================================================

    private Word cursorToWord(
            Cursor cursor) {

        Word word =
                new Word();


        // ID

        word.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "id"
                        )
                )
        );


        // ID BỘ TỪ

        word.setSetId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "set_id"
                        )
                )
        );


        // TỪ TIẾNG ANH

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


        // PHIÊN ÂM

        word.setPronunciation(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "pronunciation"
                        )
                )
        );


        // NGHĨA TIẾNG VIỆT

        word.setMeaning(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "meaning"
                        )
                )
        );


        // VÍ DỤ

        word.setExample(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "example"
                        )
                )
        );


        // GHI CHÚ

        word.setNote(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "note"
                        )
                )
        );


        return word;
    }

    // TẠO DỮ LIỆU WORD GIẢ ĐỂ TEST
    public void insertFakeData(int setId) {

        // TỪ 1
        Word word1 = new Word();

        word1.setSetId(setId);
        word1.setEnglish("apple");
        word1.setLoaiTu("n");
        word1.setPronunciation("/ˈæpəl/");
        word1.setMeaning("quả táo");
        word1.setExample("I eat an apple every day.");
        word1.setNote("Tôi ăn táo mọi ngày.");


        // TỪ 2
        Word word2 = new Word();

        word2.setSetId(setId);
        word2.setEnglish("book");
        word2.setLoaiTu("v");
        word2.setPronunciation("/bʊk/");
        word2.setMeaning("quyển sách");
        word2.setExample("I am reading a book.");
        word2.setNote("Tôi đang đọc sách");


        // TỪ 3
        Word word3 = new Word();

        word3.setSetId(setId);
        word3.setEnglish("teacher");
        word3.setLoaiTu("n");
        word3.setPronunciation("/ˈtiːtʃər/");
        word3.setMeaning("giáo viên");
        word3.setExample("My teacher is very kind.");
        word3.setNote("Giáo viên của tôi rất tốt bụng");


        // TỪ 4
        Word word4 = new Word();

        word4.setSetId(setId);
        word4.setEnglish("student");
        word4.setLoaiTu("n");
        word4.setPronunciation("/ˈstuːdənt/");
        word4.setMeaning("học sinh");
        word4.setExample("She is a good student.");
        word4.setNote("Danh từ");


        // TỪ 5
        Word word5 = new Word();

        word5.setSetId(setId);
        word5.setEnglish("school");
        word5.setLoaiTu("n");
        word5.setPronunciation("/skuːl/");
        word5.setMeaning("trường học");
        word5.setExample("I go to school every day.");
        word5.setNote("Danh từ");


        // LƯU DATABASE

        insert(word1);
        insert(word2);
        insert(word3);
        insert(word4);
        insert(word5);
    }
}