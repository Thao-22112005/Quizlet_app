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

    // =====================================================
    // KIỂM TRA TÊN BỘ TỪ ĐÃ TỒN TẠI CHƯA
    // =====================================================

    public boolean isTitleExists(String userUid, String title) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                new String[]{"id"},
                "user_uid = ? AND title = ?",
                new String[]{userUid, title},
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);

        cursor.close();

        db.close();

        return exists;
    }


    // THÊM BỘ TỪ VỰNG
    public long insert(VocabularySet vocabularySet) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                "user_uid",
                vocabularySet.getUserUid()
        );

        values.put(
                "title",
                vocabularySet.getTitle()
        );

        values.put(
                "description",
                vocabularySet.getDescription()
        );

        values.put(
                "topic",
                vocabularySet.getTopic()
        );

        values.put(
                "level",
                vocabularySet.getLevel()
        );

        values.put(
                "cover_image",
                vocabularySet.getCoverImage()
        );

        values.put(
                "created_at",
                vocabularySet.getCreatedAt()
        );

        values.put(
                "updated_at",
                vocabularySet.getUpdatedAt()
        );

        long id = db.insert(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                null,
                values
        );

        db.close();

        return id;
    }


    // =====================================================
    // LẤY 1 BỘ TỪ THEO ID
    // =====================================================

    public VocabularySet getVocabularySetById(int id) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                null,
                "id = ?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );

        VocabularySet vocabularySet = null;

        if (cursor.moveToFirst()) {

            vocabularySet =
                    cursorToVocabularySet(cursor);
        }

        cursor.close();
        db.close();

        return vocabularySet;
    }


    // =====================================================
    // LẤY CÁC BỘ TỪ CỦA 1 USER
    // =====================================================

    public List<VocabularySet> getByUserUid(String userUid) {

        List<VocabularySet> list =
                new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VOCABULARY_SET,

                null,

                "user_uid = ?",

                new String[]{
                        userUid
                },

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


    // =====================================================
    // CẬP NHẬT BỘ TỪ
    // =====================================================

    public int update(VocabularySet vocabularySet) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "title",
                vocabularySet.getTitle()
        );

        values.put(
                "description",
                vocabularySet.getDescription()
        );

        values.put(
                "topic",
                vocabularySet.getTopic()
        );

        values.put(
                "level",
                vocabularySet.getLevel()
        );

        values.put(
                "cover_image",
                vocabularySet.getCoverImage()
        );

        values.put(
                "updated_at",
                vocabularySet.getUpdatedAt()
        );

        int result = db.update(
                DatabaseHelper.TABLE_VOCABULARY_SET,
                values,
                "id = ?",
                new String[]{
                        String.valueOf(
                                vocabularySet.getId()
                        )
                }
        );

        db.close();

        return result;
    }


    // =====================================================
    // XÓA BỘ TỪ
    // =====================================================

    public int delete(int id) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        int result = db.delete(
                DatabaseHelper.TABLE_VOCABULARY_SET,
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

    private VocabularySet cursorToVocabularySet(
            Cursor cursor) {

        VocabularySet vocabularySet =
                new VocabularySet();


        // ID

        vocabularySet.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "id"
                        )
                )
        );


        // USER UID

        vocabularySet.setUserUid(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "user_uid"
                        )
                )
        );


        // TITLE

        vocabularySet.setTitle(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "title"
                        )
                )
        );


        // DESCRIPTION

        vocabularySet.setDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "description"
                        )
                )
        );


        // TOPIC

        vocabularySet.setTopic(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "topic"
                        )
                )
        );


        // LEVEL

        vocabularySet.setLevel(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "level"
                        )
                )
        );


        // COVER IMAGE

        vocabularySet.setCoverImage(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "cover_image"
                        )
                )
        );


        // CREATED AT

        vocabularySet.setCreatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "created_at"
                        )
                )
        );


        // UPDATED AT

        vocabularySet.setUpdatedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "updated_at"
                        )
                )
        );


        return vocabularySet;
    }
}