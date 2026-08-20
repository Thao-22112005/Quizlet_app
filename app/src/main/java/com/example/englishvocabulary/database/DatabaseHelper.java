package com.example.englishvocabulary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // =====================================================
    // DATABASE
    // =====================================================

    private static final String DATABASE_NAME = "quizletapp.db";
    private static final int DATABASE_VERSION = 5;


    // =====================================================
    // TÊN CÁC BẢNG
    // =====================================================

    public static final String TABLE_VOCABULARY_SET =
            "VOCABULARY_SET";

    public static final String TABLE_WORD =
            "WORD";

    public static final String TABLE_LEARNING_HISTORY =
            "LEARNING_HISTORY";

    public static final String TABLE_QUIZ_RESULT =
            "QUIZ_RESULT";


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public DatabaseHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }


    // =====================================================
    // BẬT KHÓA NGOẠI
    // =====================================================

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        db.setForeignKeyConstraintsEnabled(true);
    }


    // =====================================================
    // TẠO DATABASE
    // =====================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =================================================
        // BẢNG VOCABULARY_SET
        // =================================================

        String createVocabularySetTable =
                "CREATE TABLE " + TABLE_VOCABULARY_SET + " (" +

                        // ID bộ từ
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        // UID người dùng Firebase
                        "user_uid TEXT NOT NULL, " +

                        // Tên bộ từ
                        "title TEXT NOT NULL, " +

                        // Mô tả
                        "description TEXT, " +

                        // Chủ đề
                        "topic TEXT, " +

                        // Trình độ
                        "level TEXT, " +

                        // Ảnh bìa
                        "cover_image TEXT, " +

                        // Thời gian tạo
                        "created_at TEXT, " +

                        // Thời gian cập nhật
                        "updated_at TEXT" +

                        ")";

        db.execSQL(createVocabularySetTable);


        // =================================================
        // BẢNG WORD
        // =================================================

        String createWordTable =
                "CREATE TABLE " + TABLE_WORD + " (" +

                        // ID từ
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        // ID bộ từ
                        "set_id INTEGER NOT NULL, " +

                        // Từ tiếng Anh
                        "english TEXT NOT NULL, " +

                        // Phiên âm
                        "pronunciation TEXT, " +

                        // Nghĩa tiếng Việt
                        "meaning TEXT NOT NULL, " +

                        // Ví dụ
                        "example TEXT, " +

                        // Ghi chú
                        "note TEXT, " +

                        // Loại từ
                        "loai_tu TEXT, " +

                        // Đã học chưa
                        // 0 = chưa học
                        // 1 = đã học
                        "is_learned INTEGER DEFAULT 0, " +

                        // Khóa ngoại tới VOCABULARY_SET
                        "FOREIGN KEY(set_id) " +
                        "REFERENCES " +
                        TABLE_VOCABULARY_SET +
                        "(id) " +

                        // Xóa bộ từ thì xóa các từ bên trong
                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createWordTable);


        // =================================================
        // BẢNG LEARNING_HISTORY
        // =================================================

        String createLearningHistoryTable =
                "CREATE TABLE " +
                        TABLE_LEARNING_HISTORY +
                        " (" +

                        // ID lịch sử
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        // ID bộ từ
                        "set_id INTEGER NOT NULL, " +

                        // ID từ vựng
                        "word_id INTEGER NOT NULL, " +

                        // Kết quả
                        // 1 = đúng
                        // 0 = sai
                        "is_correct INTEGER NOT NULL, " +

                        // Chế độ học
                        // FLASHCARD / QUIZ / MATCHING...
                        "learning_mode TEXT NOT NULL, " +

                        // Thời gian học
                        "learned_at TEXT, " +

                        // Khóa ngoại tới VOCABULARY_SET
                        "FOREIGN KEY(set_id) " +
                        "REFERENCES " +
                        TABLE_VOCABULARY_SET +
                        "(id) " +

                        "ON DELETE CASCADE, " +

                        // Khóa ngoại tới WORD
                        "FOREIGN KEY(word_id) " +
                        "REFERENCES " +
                        TABLE_WORD +
                        "(id) " +

                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createLearningHistoryTable);


        // =================================================
        // BẢNG QUIZ_RESULT
        // =================================================

        String createQuizResultTable =
                "CREATE TABLE " +
                        TABLE_QUIZ_RESULT +
                        " (" +

                        // ID kết quả
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        // ID bộ từ
                        "set_id INTEGER NOT NULL, " +

                        // Tổng số câu
                        "total_questions INTEGER NOT NULL, " +

                        // Số câu đúng
                        "correct_answers INTEGER NOT NULL, " +

                        // Số câu sai
                        "wrong_answers INTEGER NOT NULL, " +

                        // Điểm
                        "score REAL NOT NULL, " +

                        // Thời gian hoàn thành
                        "completed_at TEXT, " +

                        // Khóa ngoại tới VOCABULARY_SET
                        "FOREIGN KEY(set_id) " +
                        "REFERENCES " +
                        TABLE_VOCABULARY_SET +
                        "(id) " +

                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createQuizResultTable);
    }


    // =====================================================
    // NÂNG VERSION DATABASE
    // =====================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Xóa bảng con trước
        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_LEARNING_HISTORY
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_QUIZ_RESULT
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_WORD
        );

        // Sau cùng mới xóa bảng chính
        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_VOCABULARY_SET
        );

        // Tạo lại database
        onCreate(db);
    }
}