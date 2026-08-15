package com.example.englishvocabulary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên database
    private static final String DATABASE_NAME = "quizletapp.db";

    // Phiên bản database
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    public static final String TABLE_VOCABULARY_SET = "VOCABULARY_SET";
    public static final String TABLE_WORD = "WORD";
    public static final String TABLE_LEARNING_HISTORY = "LEARNING_HISTORY";
    public static final String TABLE_QUIZ_RESULT = "QUIZ_RESULT";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // bật khóa ngoài
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    // TẠO DATABASE
    @Override
    public void onCreate(SQLiteDatabase db) {

        // BẢNG VOCABULARY_SET
        String createVocabularySetTable =
                "CREATE TABLE " + TABLE_VOCABULARY_SET + " (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "title TEXT NOT NULL, " +

                        "description TEXT, " +

                        "topic TEXT, " +

                        "level TEXT, " +

                        "cover_image TEXT, " +

                        "created_at TEXT, " +

                        "updated_at TEXT" +

                        ")";

        db.execSQL(createVocabularySetTable);

        // BẢNG WORD
        String createWordTable =
                "CREATE TABLE " + TABLE_WORD + " (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "set_id INTEGER NOT NULL, " +

                        "english TEXT NOT NULL, " +

                        "pronunciation TEXT, " +

                        "meaning TEXT NOT NULL, " +

                        "example TEXT, " +

                        "note TEXT, " +

                        "FOREIGN KEY(set_id) " +
                        "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createWordTable);

        // BẢNG LEARNING_HISTORY
        String createLearningHistoryTable =
                "CREATE TABLE " + TABLE_LEARNING_HISTORY + " (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "set_id INTEGER NOT NULL, " +

                        "word_id INTEGER NOT NULL, " +

                        "is_correct INTEGER NOT NULL, " +

                        "learning_mode TEXT NOT NULL, " +

                        "learned_at TEXT, " +

                        "FOREIGN KEY(set_id) " +
                        "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                        "ON DELETE CASCADE, " +

                        "FOREIGN KEY(word_id) " +
                        "REFERENCES " + TABLE_WORD + "(id) " +

                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createLearningHistoryTable);


        // BẢNG QUIZ_RESULT
        String createQuizResultTable =
                "CREATE TABLE " + TABLE_QUIZ_RESULT + " (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "set_id INTEGER NOT NULL, " +

                        "total_questions INTEGER NOT NULL, " +

                        "correct_answers INTEGER NOT NULL, " +

                        "wrong_answers INTEGER NOT NULL, " +

                        "score REAL NOT NULL, " +

                        "completed_at TEXT, " +

                        "FOREIGN KEY(set_id) " +
                        "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createQuizResultTable);
    }

    // KHI NÂNG VERSION DATABASE
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY_SET);

        onCreate(db);
    }
}