package com.example.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishvocabulary.models.VocabularySet;

import java.util.ArrayList;
import java.util.List;

public class VocabularySetDAO {
    private DatabaseHelper dbHelper;

    public VocabularySetDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Thêm bộ từ vựng
    public long insert(VocabularySet vocabularySet){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title", vocabularySet.getTitle());
        values.put("description", vocabularySet.getDescription());
        values.put("topic", vocabularySet.getTopic());
        values.put("level", vocabularySet.getLevel());
        values.put("cover_image", vocabularySet.getCoverImage());
        values.put("created_at", vocabularySet.getCreatedAt());
        values.put("updated_at", vocabularySet.getUpdatedAt());

        long id = db.insert(DatabaseHelper.TABLE_VOCABULARY_SET, null, values);
        db.close();
        return id;
    }

    //Lấy 1 bộ từ theo ID
    public VocabularySet getVocabularySetById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_VOCABULARY_SET,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        VocabularySet vocabularySet = null;
        if (cursor.moveToFirst()) {
            vocabularySet = cursorToVocabularySet(cursor);
        }
        cursor.close();
        db.close();
        return vocabularySet;
    }

    // Lấy tất cả bộ từ
    public List<VocabularySet> getAll() {

        List<VocabularySet> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );

        while (cursor.moveToNext()) {

            VocabularySet vocabularySet =
                    cursorToVocabularySet(cursor);

            list.add(vocabularySet);
        }

        cursor.close();
        db.close();

        return list;
    }

    // CẬP NHẬT BỘ TỪ
    public int update(VocabularySet vocabularySet) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("title", vocabularySet.getTitle());
        values.put("description", vocabularySet.getDescription());
        values.put("topic", vocabularySet.getTopic());
        values.put("level", vocabularySet.getLevel());
        values.put("cover_image", vocabularySet.getCoverImage());
        values.put("updated_at", vocabularySet.getUpdatedAt());

        int result = db.update(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                values,
                "id = ?",
                new String[]{
                        String.valueOf(vocabularySet.getId())
                }
        );

        db.close();

        return result;
    }

    // XÓA BỘ TỪ
    public int delete(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return result;
    }

    // CURSOR → OBJECT
    private VocabularySet cursorToVocabularySet(Cursor cursor) {

        VocabularySet vocabularySet = new VocabularySet();

        vocabularySet.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                )
        );

        vocabularySet.setTitle(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("title")
                )
        );

        vocabularySet.setDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("description")
                )
        );

        vocabularySet.setTopic(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("topic")
                )
        );

        vocabularySet.setLevel(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("level")
                )
        );

        vocabularySet.setCoverImage(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("cover_image")
                )
        );

        vocabularySet.setCreatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("created_at")
                )
        );

        vocabularySet.setUpdatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("updated_at")
                )
        );

        return vocabularySet;
    }

}
