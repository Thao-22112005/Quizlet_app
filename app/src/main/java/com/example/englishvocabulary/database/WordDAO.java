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
        dbHelper = new DatabaseHelper(context);
    }

    //Them
    public long insert(Word word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("english", word.getEnglish());
        values.put("pronunciation", word.getPronunciation());
        values.put("meaning", word.getMeaning());
        values.put("example", word.getExample());
        values.put("note", word.getNote());
        values.put("set_id", word.getSetId());
        long id = db.insert(DatabaseHelper.TABLE_WORD, null, values);
        db.close();
        return id;
    }

    //Sua
    public long update(Word word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("english", word.getEnglish());
        values.put("pronunciation", word.getPronunciation());
        values.put("meaning", word.getMeaning());
        values.put("example", word.getExample());
        values.put("note", word.getNote());

        int result = db.update(
                DatabaseHelper.TABLE_WORD,
                values,
                "id = ?",
                new String[]{
                        String.valueOf(word.getId())
                }
        );
        db.close();
        return result;
    }

    //Xoa
    public int delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(
                DatabaseHelper.TABLE_WORD,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return result;
    }

    // cursor to object
    private Word cursorToWord(Cursor cursor){
        Word word = new Word();
        word.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                )
        );
        word.setEnglish(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("english")
                )
        );

        word.setPronunciation(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("pronunciation")
                )
        );
        word.setMeaning(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("meaning")
                )
        );
        word.setExample(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("example")
                )
        );
        word.setNote(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("note")
                )
        );
        word.setSetId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("set_id")
                )
        );
        word.setIsLearned(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("is_learned")
                )
        );
        return word;
    }

    //get all
    public List<Word> getAll(){
        List<Word> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_WORD,
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );
        while (cursor.moveToNext()){
            Word word = cursorToWord(cursor);
            list.add(word);
        }
        cursor.close();
        db.close();
        return list;
    }

    //get by id
    public Word getWordById(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_WORD,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        Word word = null;
        if (cursor.moveToFirst()) {
            word = cursorToWord(cursor);
        }
        cursor.close();
        db.close();
        return word;
    }

    //get list by set id
    public List<Word> getListBySetId(int setId){
        List<Word> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_WORD,
                null,
                "set_id = ?",
                new String[]{String.valueOf(setId)},
                null,
                null,
                "id DESC"
        );
        while (cursor.moveToNext()){
            Word word = cursorToWord(cursor);
            list.add(word);
        }
        cursor.close();
        db.close();
        return list;
    }

}
