package com.example.englishvocabulary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // TÊN DATABASE
    private static final String DATABASE_NAME = "quizletapp.db";

    // Vẫn giữ version 2
    private static final int DATABASE_VERSION = 3;
        // =====================================================
        // TÊN DATABASE
        // =====================================================

        private static final String DATABASE_NAME = "quizletapp.db";

        // Version mới:
        // - Thêm is_learned vào WORD
        // - Them du lieu mau
        private static final int DATABASE_VERSION = 3;

        // =====================================================
        // TÊN CÁC BẢNG
        // =====================================================

    // TÊN CÁC BẢNG
    public static final String TABLE_VOCABULARY_SET = "VOCABULARY_SET";
    public static final String TABLE_WORD = "WORD";
    public static final String TABLE_LEARNING_HISTORY = "LEARNING_HISTORY";
    public static final String TABLE_QUIZ_RESULT = "QUIZ_RESULT";

        public DatabaseHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

    // CONSTRUCTOR
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // BẬT KHÓA NGOÀI
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

                // =================================================
                // BẢNG VOCABULARY_SET
                // =================================================

                String createVocabularySetTable = "CREATE TABLE " + TABLE_VOCABULARY_SET + " (" +

    // TẠO DATABASE
    @Override
    public void onCreate(SQLiteDatabase db) {

        // BẢNG VOCABULARY_SET
        String createVocabularySetTable =
                "CREATE TABLE " + TABLE_VOCABULARY_SET + " (" +

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

                String createWordTable = "CREATE TABLE " + TABLE_WORD + " (" +

                // ID từ
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                                // ID bộ từ
                                "set_id INTEGER NOT NULL, " +

        // BẢNG WORD
        String createWordTable =
                "CREATE TABLE " + TABLE_WORD + " (" +

                                // Ví dụ
                                "example TEXT, " +

                                // Ghi chú
                                "note TEXT, " +

                                // Đã học chưa (0: chưa, 1: đã)
                                "is_learned INTEGER DEFAULT 0, " +

                        //Loại từ (n,v, adj)
                        "loai_tu TEXT NOT NULL," +

                        // Phiên âm
                        "pronunciation TEXT, " +
                                // Khóa ngoại tới VOCABULARY_SET
                                "FOREIGN KEY(set_id) " +
                                "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                                // Xóa bộ từ thì xóa luôn các từ
                                "ON DELETE CASCADE" +

                                ")";

                db.execSQL(createWordTable);

                // =================================================
                // BẢNG LEARNING_HISTORY
                // =================================================

                String createLearningHistoryTable = "CREATE TABLE " + TABLE_LEARNING_HISTORY + " (" +

                // ID lịch sử
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                                // ID bộ từ
                                "set_id INTEGER NOT NULL, " +

                                // ID từ vựng
                                "word_id INTEGER NOT NULL, " +

        // BẢNG LEARNING_HISTORY
        String createLearningHistoryTable =
                "CREATE TABLE " + TABLE_LEARNING_HISTORY + " (" +
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
                                "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                                "ON DELETE CASCADE, " +

                                // Khóa ngoại tới WORD
                                "FOREIGN KEY(word_id) " +
                                "REFERENCES " + TABLE_WORD + "(id) " +

                                "ON DELETE CASCADE" +

                                ")";

                db.execSQL(createLearningHistoryTable);

                // =================================================
                // BẢNG QUIZ_RESULT
                // =================================================

                String createQuizResultTable = "CREATE TABLE " + TABLE_QUIZ_RESULT + " (" +

                // ID kết quả
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                                // ID bộ từ
                                "set_id INTEGER NOT NULL, " +

                                // Tổng số câu
                                "total_questions INTEGER NOT NULL, " +


        // BẢNG QUIZ_RESULT
        String createQuizResultTable =
                "CREATE TABLE " + TABLE_QUIZ_RESULT + " (" +

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
                        "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                        "ON DELETE CASCADE" +

                        ")";

        db.execSQL(createQuizResultTable);
    }


    // NÂNG VERSION DATABASE
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Xóa bảng theo thứ tự từ bảng phụ → bảng chính

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

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_VOCABULARY_SET
        );

        // Tạo lại database
        onCreate(db);
    }
}
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
                                "REFERENCES " + TABLE_VOCABULARY_SET + "(id) " +

                                "ON DELETE CASCADE" +

                                ")";

                db.execSQL(createQuizResultTable);

                insertSampleData(db);
        }

        // du lieu mau
        private void insertSampleData(SQLiteDatabase db) {

                // ===== Bộ từ 1: Từ vựng hằng ngày =====
                db.execSQL("INSERT INTO " + TABLE_VOCABULARY_SET +
                                " (user_uid, title, description, topic, level, created_at, updated_at) " +
                                "VALUES ('test_user', 'Từ vựng hằng ngày', 'Các từ thông dụng trong cuộc sống', 'Đời sống', 'Cơ bản', datetime('now'), datetime('now'))");

                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'breakfast', '/ˈbrek.fəst/', 'bữa sáng', 'I have breakfast at 7 AM.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'clothes', '/kloʊðz/', 'quần áo', 'She bought new clothes yesterday.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'drink', '/drɪŋk/', 'uống', 'Do you want something to drink?', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'evening', '/ˈiːv.nɪŋ/', 'buổi tối', 'We went for a walk in the evening.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'family', '/ˈfæm.əl.i/', 'gia đình', 'My family has five members.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'garden', '/ˈɡɑːr.dən/', 'khu vườn', 'There are roses in the garden.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'happy', '/ˈhæp.i/', 'vui vẻ', 'She feels happy today.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (1, 'kitchen', '/ˈkɪtʃ.ɪn/', 'nhà bếp', 'Mom is cooking in the kitchen.', 0)");

                // ===== Bộ từ 2: Chủ đề Du lịch =====
                db.execSQL("INSERT INTO " + TABLE_VOCABULARY_SET +
                                " (user_uid, title, description, topic, level, created_at, updated_at) " +
                                "VALUES ('test_user', 'Chủ đề Du lịch', 'Từ vựng khi đi du lịch', 'Du lịch', 'Trung bình', datetime('now'), datetime('now'))");

                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'airport', '/ˈeə.pɔːt/', 'sân bay', 'We arrived at the airport early.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'luggage', '/ˈlʌɡ.ɪdʒ/', 'hành lý', 'Do not leave your luggage unattended.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'passport', '/ˈpɑːs.pɔːt/', 'hộ chiếu', 'Please show your passport.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'journey', '/ˈdʒɜːr.ni/', 'hành trình', 'The journey took three hours.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'ticket', '/ˈtɪk.ɪt/', 'vé', 'I bought a round-trip ticket.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'souvenir', '/ˌsuː.vəˈnɪr/', 'quà lưu niệm', 'She bought a souvenir for her friend.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (2, 'vacation', '/veɪˈkeɪ.ʃən/', 'kỳ nghỉ', 'We are planning a vacation next month.', 0)");

                // ===== Bộ từ 3: Công việc & Văn phòng =====
                db.execSQL("INSERT INTO " + TABLE_VOCABULARY_SET +
                                " (user_uid, title, description, topic, level, created_at, updated_at) " +
                                "VALUES ('test_user', 'Công việc & Văn phòng', 'Từ vựng dùng trong môi trường làm việc', 'Công việc', 'Trung bình', datetime('now'), datetime('now'))");

                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'interview', '/ˈɪn.tə.vjuː/', 'phỏng vấn', 'I have a job interview tomorrow.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'manager', '/ˈmæn.ɪ.dʒər/', 'quản lý', 'The manager approved our project.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'negotiate', '/nɪˈɡoʊ.ʃi.eɪt/', 'đàm phán', 'We need to negotiate the contract.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'overtime', '/ˈoʊ.vər.taɪm/', 'làm thêm giờ', 'He works overtime every Friday.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'qualify', '/ˈkwɒl.ɪ.faɪ/', 'đủ điều kiện', 'You need experience to qualify for this role.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'resume', '/rɪˈzjuːm/', 'sơ yếu lý lịch', 'Please send your resume by email.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'deadline', '/ˈded.laɪn/', 'hạn chót', 'The deadline is next Monday.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (3, 'experience', '/ɪkˈspɪr.i.əns/', 'kinh nghiệm', 'She has five years of experience.', 0)");

                // ===== Bộ từ 4: Thiên nhiên & Động vật =====
                db.execSQL("INSERT INTO " + TABLE_VOCABULARY_SET +
                                " (user_uid, title, description, topic, level, created_at, updated_at) " +
                                "VALUES ('test_user', 'Thiên nhiên & Động vật', 'Khám phá thế giới tự nhiên', 'Thiên nhiên', 'Cơ bản', datetime('now'), datetime('now'))");

                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'mountain', '/ˈmaʊn.tɪn/', 'núi', 'They climbed the mountain last weekend.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'ocean', '/ˈoʊ.ʃən/', 'đại dương', 'The ocean is very deep.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'rainbow', '/ˈreɪn.boʊ/', 'cầu vồng', 'A beautiful rainbow appeared after the rain.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'waterfall', '/ˈwɔː.tər.fɔːl/', 'thác nước', 'The waterfall is stunning.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'dolphin', '/ˈdɒl.fɪn/', 'cá heo', 'Dolphins are very intelligent animals.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'eagle', '/ˈiː.ɡəl/', 'đại bàng', 'The eagle flew high in the sky.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'universe', '/ˈjuː.nɪ.vɜːrs/', 'vũ trụ', 'The universe is incredibly vast.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'xenon', '/ˈzen.ɒn/', 'khí xenon', 'Xenon is used in flash lamps.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (4, 'zebra', '/ˈziː.brə/', 'ngựa vằn', 'A zebra has black and white stripes.', 0)");

                // ===== Bộ từ 5: Tiếng Anh giao tiếp =====
                db.execSQL("INSERT INTO " + TABLE_VOCABULARY_SET +
                                " (user_uid, title, description, topic, level, created_at, updated_at) " +
                                "VALUES ('test_user', 'Tiếng Anh giao tiếp', 'Từ vựng thường dùng trong giao tiếp hàng ngày', 'Giao tiếp', 'Cơ bản', datetime('now'), datetime('now'))");

                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'apologize', '/əˈpɒl.ə.dʒaɪz/', 'xin lỗi', 'I apologize for being late.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'believe', '/bɪˈliːv/', 'tin tưởng', 'I believe you can do it.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'communicate', '/kəˈmjuː.nɪ.keɪt/', 'giao tiếp', 'We communicate through email.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'describe', '/dɪˈskraɪb/', 'mô tả', 'Can you describe the problem?', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'encourage', '/ɪnˈkʌr.ɪdʒ/', 'khuyến khích', 'Teachers encourage students to read more.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'forgive', '/fəˈɡɪv/', 'tha thứ', 'Please forgive me for the mistake.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'greet', '/ɡriːt/', 'chào hỏi', 'She greeted everyone with a smile.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'honest', '/ˈɒn.ɪst/', 'trung thực', 'He is an honest person.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'introduce', '/ˌɪn.trəˈdjuːs/', 'giới thiệu', 'Let me introduce myself.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'joke', '/dʒoʊk/', 'trò đùa', 'He told a funny joke.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'kind', '/kaɪnd/', 'tốt bụng', 'She is very kind to everyone.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'listen', '/ˈlɪs.ən/', 'lắng nghe', 'Please listen carefully.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'misunderstand', '/ˌmɪs.ʌn.dəˈstænd/', 'hiểu lầm', 'I think you misunderstand me.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'nervous', '/ˈnɜːr.vəs/', 'lo lắng', 'I feel nervous before the exam.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'opinion', '/əˈpɪn.jən/', 'ý kiến', 'What is your opinion on this?', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'polite', '/pəˈlaɪt/', 'lịch sự', 'He is always polite to strangers.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'question', '/ˈkwes.tʃən/', 'câu hỏi', 'Do you have any questions?', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'respect', '/rɪˈspekt/', 'tôn trọng', 'We should respect each other.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'suggest', '/səˈdʒest/', 'gợi ý', 'I suggest we take a break.', 0)");
                db.execSQL("INSERT INTO " + TABLE_WORD +
                                " (set_id, english, pronunciation, meaning, example, is_learned) " +
                                "VALUES (5, 'thank', '/θæŋk/', 'cảm ơn', 'Thank you for your help.', 0)");
        }

        // =====================================================
        // NÂNG VERSION DATABASE
        // =====================================================

        @Override
        public void onUpgrade(
                        SQLiteDatabase db,
                        int oldVersion,
                        int newVersion) {

                // Xóa bảng theo thứ tự từ bảng phụ → bảng chính

                db.execSQL(
                                "DROP TABLE IF EXISTS " +
                                                TABLE_LEARNING_HISTORY);

                db.execSQL(
                                "DROP TABLE IF EXISTS " +
                                                TABLE_QUIZ_RESULT);

                db.execSQL(
                                "DROP TABLE IF EXISTS " +
                                                TABLE_WORD);

                db.execSQL(
                                "DROP TABLE IF EXISTS " +
                                                TABLE_VOCABULARY_SET);

                // Tạo lại database
                onCreate(db);
        }
}
