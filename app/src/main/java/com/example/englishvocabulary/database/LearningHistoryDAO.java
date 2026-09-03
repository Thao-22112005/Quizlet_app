package com.example.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishvocabulary.models.LearningHistory;

import java.util.ArrayList;
import java.util.List;

public class LearningHistoryDAO {

    private DatabaseHelper dbHelper;


    public LearningHistoryDAO(Context context) {

        dbHelper =
                new DatabaseHelper(context);
    }


    public long insert(
            LearningHistory learningHistory) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();


        // ID BỘ TỪ

        values.put(
                "set_id",
                learningHistory.getSetId()
        );


        // ID TỪ VỰNG

        values.put(
                "word_id",
                learningHistory.getWordId()
        );


        // ĐÚNG / SAI

        values.put(
                "is_correct",
                learningHistory.getIsCorrect()
        );


        // CHẾ ĐỘ HỌC

        values.put(
                "learning_mode",
                learningHistory.getLearningMode()
        );


        // THỜI GIAN HỌC

        values.put(
                "learned_at",
                learningHistory.getLearnedAt()
        );


        long id =
                db.insert(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,
                        null,
                        values
                );


        db.close();


        return id;
    }

    public LearningHistory getById(int id) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,

                        null,

                        "id = ?",

                        new String[]{
                                String.valueOf(id)
                        },

                        null,
                        null,
                        null
                );


        LearningHistory learningHistory =
                null;


        if (cursor.moveToFirst()) {

            learningHistory =
                    cursorToLearningHistory(cursor);
        }


        cursor.close();

        db.close();


        return learningHistory;
    }

    public List<LearningHistory> getBySetId(
            int setId) {

        List<LearningHistory> list =
                new ArrayList<>();


        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,

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

            LearningHistory learningHistory =
                    cursorToLearningHistory(cursor);

            list.add(learningHistory);
        }


        cursor.close();

        db.close();


        return list;
    }


    // LẤY LỊCH SỬ CỦA 1 TỪ
    public List<LearningHistory> getByWordId(
            int wordId) {

        List<LearningHistory> list =
                new ArrayList<>();


        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,

                        null,

                        "word_id = ?",

                        new String[]{
                                String.valueOf(wordId)
                        },

                        null,
                        null,

                        "id DESC"
                );


        while (cursor.moveToNext()) {

            LearningHistory learningHistory =
                    cursorToLearningHistory(cursor);

            list.add(learningHistory);
        }


        cursor.close();

        db.close();


        return list;
    }

    public List<LearningHistory> getBySetIdAndMode(
            int setId,
            String learningMode) {

        List<LearningHistory> list =
                new ArrayList<>();


        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,

                        null,

                        "set_id = ? AND learning_mode = ?",

                        new String[]{
                                String.valueOf(setId),
                                learningMode
                        },

                        null,
                        null,

                        "id ASC"
                );


        while (cursor.moveToNext()) {

            LearningHistory learningHistory =
                    cursorToLearningHistory(cursor);

            list.add(learningHistory);
        }


        cursor.close();

        db.close();


        return list;
    }


    public LearningHistory getLatestByWordId(
            int wordId,
            String learningMode) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.query(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,

                        null,

                        "word_id = ? AND learning_mode = ?",

                        new String[]{
                                String.valueOf(wordId),
                                learningMode
                        },

                        null,
                        null,

                        "id DESC",

                        "1"
                );


        LearningHistory learningHistory =
                null;


        if (cursor.moveToFirst()) {

            learningHistory =
                    cursorToLearningHistory(cursor);
        }


        cursor.close();

        db.close();


        return learningHistory;
    }

    public int countCorrect(
            int setId,
            String learningMode) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) " +
                                "FROM " +
                                DatabaseHelper.TABLE_LEARNING_HISTORY +
                                " WHERE set_id = ? " +
                                "AND learning_mode = ? " +
                                "AND is_correct = 1",

                        new String[]{
                                String.valueOf(setId),
                                learningMode
                        }
                );


        int count = 0;


        if (cursor.moveToFirst()) {

            count =
                    cursor.getInt(0);
        }


        cursor.close();

        db.close();


        return count;
    }


    public int countWrong(
            int setId,
            String learningMode) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) " +
                                "FROM " +
                                DatabaseHelper.TABLE_LEARNING_HISTORY +
                                " WHERE set_id = ? " +
                                "AND learning_mode = ? " +
                                "AND is_correct = 0",

                        new String[]{
                                String.valueOf(setId),
                                learningMode
                        }
                );


        int count = 0;


        if (cursor.moveToFirst()) {

            count =
                    cursor.getInt(0);
        }


        cursor.close();

        db.close();


        return count;
    }

    public int deleteBySetId(int setId) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();


        int result =
                db.delete(
                        DatabaseHelper.TABLE_LEARNING_HISTORY,

                        "set_id = ?",

                        new String[]{
                                String.valueOf(setId)
                        }
                );


        db.close();


        return result;
    }


    private LearningHistory cursorToLearningHistory(
            Cursor cursor) {

        LearningHistory learningHistory =
                new LearningHistory();


        // ID

        learningHistory.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "id"
                        )
                )
        );


        // ID BỘ TỪ

        learningHistory.setSetId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "set_id"
                        )
                )
        );


        // ID TỪ VỰNG

        learningHistory.setWordId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "word_id"
                        )
                )
        );


        // ĐÚNG / SAI

        learningHistory.setIsCorrect(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "is_correct"
                        )
                )
        );


        // CHẾ ĐỘ HỌC

        learningHistory.setLearningMode(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "learning_mode"
                        )
                )
        );


        // THỜI GIAN HỌC

        learningHistory.setLearnedAt(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "learned_at"
                        )
                )
        );


        return learningHistory;
    }


    //thong ke
    public int getCorrectCountByDate(String date) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) " +
                        "FROM " + DatabaseHelper.TABLE_LEARNING_HISTORY +
                        " WHERE learned_at LIKE ? " +
                        " AND is_correct = 1",
                new String[]{
                        date + "%"
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

    public int getDailyActivityCount(String userUid, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_LEARNING_HISTORY + " lh " +
                "INNER JOIN " + DatabaseHelper.TABLE_VOCABULARY_SET + " vs ON lh.set_id = vs.id " +
                "WHERE vs.user_uid = ? AND lh.learned_at LIKE ?";
        Cursor cursor = db.rawQuery(sql, new String[]{userUid, date + "%"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

//    public int getRememberedCountByUser(String userUid) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        String SQL = "SELECT COUNT(DISTINCT lh.word_id) " +
//                "FROM " + DatabaseHelper.TABLE_LEARNING_HISTORY + " lh " +
//                "INNER JOIN " + DatabaseHelper.TABLE_VOCABULARY_SET + " vs ON lh.set_id = vs.id " +
//                "WHERE vs.user_uid = ? AND lh.is_correct = 1";
//        Cursor cursor = db.rawQuery(sql, new String[]{userUid});
//        int count = 0;
//        if (cursor.moveToFirst()) {
//            count = cursor.getInt(0);
//        }
//        cursor.close();
//        db.close();
//        return count;
//    }
    public int getRememberedCountByUser(String userUid) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        String sql =
                "SELECT COUNT(*) " +
                        "FROM " + DatabaseHelper.TABLE_LEARNING_HISTORY + " lh " +
                        "INNER JOIN " +
                        DatabaseHelper.TABLE_VOCABULARY_SET +
                        " vs ON lh.set_id = vs.id " +

                        "WHERE vs.user_uid = ? " +

                        // Chỉ xét Flashcard
                        "AND lh.learning_mode = 'FLASHCARD' " +

                        // Chỉ lấy kết quả mới nhất của mỗi từ
                        "AND lh.id = ( " +
                        "SELECT MAX(lh2.id) " +
                        "FROM " +
                        DatabaseHelper.TABLE_LEARNING_HISTORY +
                        " lh2 " +
                        "WHERE lh2.word_id = lh.word_id " +
                        "AND lh2.learning_mode = 'FLASHCARD' " +
                        ") " +

                        // Lần gần nhất là Đã nhớ
                        "AND lh.is_correct = 1";

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
    public int getRememberedCountBySetId(int setId) {

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        String sql =
                "SELECT COUNT(*) " +
                        "FROM " +
                        DatabaseHelper.TABLE_LEARNING_HISTORY +
                        " lh " +

                        "WHERE lh.set_id = ? " +

                        // Chỉ xét Flashcard
                        "AND lh.learning_mode = 'FLASHCARD' " +

                        // Chỉ lấy trạng thái mới nhất của mỗi từ
                        "AND lh.id = ( " +
                        "SELECT MAX(lh2.id) " +
                        "FROM " +
                        DatabaseHelper.TABLE_LEARNING_HISTORY +
                        " lh2 " +
                        "WHERE lh2.word_id = lh.word_id " +
                        "AND lh2.learning_mode = 'FLASHCARD' " +
                        ") " +

                        // Trạng thái mới nhất là Đã nhớ
                        "AND lh.is_correct = 1";

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