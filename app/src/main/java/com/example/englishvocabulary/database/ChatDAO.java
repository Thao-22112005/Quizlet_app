package com.example.englishvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishvocabulary.models.ChatConversation;
import com.example.englishvocabulary.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatDAO {

    private final DatabaseHelper databaseHelper;

    public ChatDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // ==========================================
    // TẠO CUỘC TRÒ CHUYỆN MỚI
    // ==========================================

    public long createConversation(
            String userUid,
            String title
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        String currentTime = getCurrentTime();

        values.put("user_uid", userUid);
        values.put("title", title);
        values.put("created_at", currentTime);
        values.put("updated_at", currentTime);

        return db.insert(
                DatabaseHelper.TABLE_CHAT_CONVERSATION,
                null,
                values
        );
    }


    // ==========================================
    // LƯU TIN NHẮN
    // ==========================================

    public long addMessage(
            long conversationId,
            String sender,
            String message
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "conversation_id",
                conversationId
        );

        values.put(
                "sender",
                sender
        );

        values.put(
                "message",
                message
        );

        values.put(
                "created_at",
                getCurrentTime()
        );

        long result = db.insert(
                DatabaseHelper.TABLE_CHAT_MESSAGE,
                null,
                values
        );

        // Cập nhật thời gian cuộc chat
        if (result != -1) {
            updateConversationTime(conversationId);
        }

        return result;
    }


    // ==========================================
    // CẬP NHẬT THỜI GIAN CHAT
    // ==========================================

    private void updateConversationTime(
            long conversationId
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "updated_at",
                getCurrentTime()
        );

        db.update(
                DatabaseHelper.TABLE_CHAT_CONVERSATION,
                values,
                "id = ?",
                new String[]{
                        String.valueOf(conversationId)
                }
        );
    }


    // ==========================================
    // LẤY DANH SÁCH CUỘC TRÒ CHUYỆN CỦA USER
    // ==========================================

    public List<ChatConversation> getConversations(
            String userUid
    ) {

        List<ChatConversation> list =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CHAT_CONVERSATION,
                null,
                "user_uid = ?",
                new String[]{userUid},
                null,
                null,
                "updated_at DESC"
        );

        while (cursor.moveToNext()) {

            ChatConversation conversation =
                    new ChatConversation();

            conversation.setId(
                    cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                    )
            );

            conversation.setUserUid(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "user_uid"
                            )
                    )
            );

            conversation.setTitle(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "title"
                            )
                    )
            );

            conversation.setCreatedAt(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "created_at"
                            )
                    )
            );

            conversation.setUpdatedAt(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "updated_at"
                            )
                    )
            );

            list.add(conversation);
        }

        cursor.close();

        return list;
    }


    // ==========================================
    // LẤY TIN NHẮN CỦA 1 CUỘC TRÒ CHUYỆN
    // ==========================================

    public List<ChatMessage> getMessages(
            long conversationId
    ) {

        List<ChatMessage> list =
                new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CHAT_MESSAGE,
                null,
                "conversation_id = ?",
                new String[]{
                        String.valueOf(conversationId)
                },
                null,
                null,
                "id ASC"
        );

        while (cursor.moveToNext()) {

            ChatMessage chatMessage =
                    new ChatMessage();

            chatMessage.setId(
                    cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                    )
            );

            chatMessage.setConversationId(
                    cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    "conversation_id"
                            )
                    )
            );

            chatMessage.setSender(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "sender"
                            )
                    )
            );

            chatMessage.setMessage(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "message"
                            )
                    )
            );

            chatMessage.setCreatedAt(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "created_at"
                            )
                    )
            );

            list.add(chatMessage);
        }

        cursor.close();

        return list;
    }


    // ==========================================
    // XÓA 1 CUỘC TRÒ CHUYỆN
    // ==========================================

    public void deleteConversation(
            long conversationId
    ) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        db.delete(
                DatabaseHelper.TABLE_CHAT_CONVERSATION,
                "id = ?",
                new String[]{
                        String.valueOf(conversationId)
                }
        );
    }


    // ==========================================
    // LẤY THỜI GIAN HIỆN TẠI
    // ==========================================

    private String getCurrentTime() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                );

        return dateFormat.format(
                new Date()
        );
    }
}