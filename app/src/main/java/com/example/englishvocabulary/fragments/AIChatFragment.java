package com.example.englishvocabulary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishvocabulary.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AIChatFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private EditText edtMessage;
    private TextView tvHello;
    private ImageButton btnSend;
    private LinearLayout chatContainer;
    private ScrollView scrollChat;

    private GenerativeModelFutures model;

    public AIChatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_a_i_chat,
                container,
                false
        );
        tvHello = view.findViewById(R.id.tvHello);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        chatContainer = view.findViewById(R.id.chatContainer);
        scrollChat = view.findViewById(R.id.scrollChat);
        firebaseAuth = FirebaseAuth.getInstance();

        // Khởi tạo Gemini
        GenerativeModel ai = FirebaseAI
                .getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-3.6-flash");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        tvHello.setText("Xin chào! " +user.getDisplayName().trim()+
                "\nTôi có thể giúp bạn giải thích từ vựng, ngữ pháp, đặt câu, tạo bài tập... bất cứ điều gì liên quan đến việc học tiếng Anh.");

        model = GenerativeModelFutures.from(ai);

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {

        String question = edtMessage.getText()
                .toString()
                .trim();

        if (question.isEmpty()) {
            edtMessage.setError("Vui lòng nhập câu hỏi");
            return;
        }

        // Hiển thị câu hỏi của người dùng
        addUserMessage(question);

        // Xóa ô nhập
        edtMessage.setText("");

        // Ẩn bàn phím
        InputMethodManager imm =
                (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    edtMessage.getWindowToken(),
                    0
            );
        }

        // Hiển thị "AI đang trả lời..."
        TextView loadingMessage = addAIMessage(
                "AI đang suy nghĩ..."
        );

        // Tạo prompt
        String prompt =
                "Bạn là trợ lý học tiếng Anh trong ứng dụng EnglishVocabulary. " +
                        "Hãy trả lời bằng tiếng Việt, giải thích dễ hiểu cho học sinh. " +
                        "Nếu người dùng hỏi về từ vựng, hãy cung cấp: nghĩa tiếng Việt, " +
                        "cách phát âm, từ loại và một ví dụ tiếng Anh. " +
                        "Nếu người dùng hỏi về ngữ pháp, hãy giải thích ngắn gọn và " +
                        "cho ví dụ.\n\n" +
                        "Câu hỏi của người dùng: " + question;

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        // Gửi tới Gemini
        ListenableFuture<GenerateContentResponse> response =
                model.generateContent(content);

        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {

                    @Override
                    public void onSuccess(
                            GenerateContentResponse result) {

                        String answer = result.getText();

                        if (answer == null || answer.isEmpty()) {
                            answer = "Xin lỗi, AI chưa có câu trả lời.";
                        }

                        // Xóa loading
                        chatContainer.removeView(loadingMessage);

                        // Hiển thị câu trả lời
                        addAIMessage(answer);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                        chatContainer.removeView(loadingMessage);

                        addAIMessage(
                                "Xin lỗi, đã xảy ra lỗi khi kết nối với AI.\n\n" +
                                        "Chi tiết: " + t.getMessage()
                        );
                    }
                },
                MoreExecutors.directExecutor()
        );
    }

    private void addUserMessage(String message) {

        TextView textView = new TextView(requireContext());

        textView.setText(message);
        textView.setTextSize(14);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(20, 20, 20, 20);

        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();

        background.setColor(Color.parseColor("#304FFE"));
        background.setCornerRadius(30);

        textView.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.gravity = android.view.Gravity.END;
        params.setMargins(60, 4, 4, 12);

        chatContainer.addView(textView, params);

        scrollToBottom();
    }

    private TextView addAIMessage(String message) {

        TextView textView = new TextView(requireContext());

        textView.setText(message);
        textView.setTextSize(14);
        textView.setTextColor(Color.rgb(40, 40, 40));
        textView.setPadding(20, 20, 20, 20);

        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();

        background.setColor(Color.rgb(241, 245, 249));
        background.setCornerRadius(30);

        textView.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.gravity = android.view.Gravity.START;
        params.setMargins(4, 4, 60, 12);

        chatContainer.addView(textView, params);

        scrollToBottom();

        return textView;
    }

    private void scrollToBottom() {

        scrollChat.post(() ->
                scrollChat.fullScroll(View.FOCUS_DOWN)
        );
    }
}