package com.example.englishvocabulary.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.adapters.ChatHistoryAdapter;
import com.example.englishvocabulary.database.ChatDAO;
import com.example.englishvocabulary.models.ChatConversation;
import com.example.englishvocabulary.models.ChatMessage;
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

import java.util.List;

public class AIChatFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private EditText edtMessage;
    private TextView tvHello;

    private ImageButton btnSend;
    private ImageButton btnBack;
    private ImageButton btnMenu;

    private LinearLayout chatContainer;
    private ScrollView scrollChat;

    // Drawer
    private DrawerLayout drawerLayout;
    private TextView btnNewChat;
    private RecyclerView rvChatHistory;

    private com.example.englishvocabulary.adapters.ChatHistoryAdapter chatHistoryAdapter;

    private int previousNavItemId = R.id.nav_home;

    private GenerativeModelFutures model;

    // =========================
    // CHAT DATABASE
    // =========================

    private ChatDAO chatDAO;

    // ID cuộc trò chuyện hiện tại
    private long currentConversationId = -1;

    // UID user hiện tại
    private String currentUserUid;


    public AIChatFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        Bundle args = getArguments();

        if (args != null) {
            previousNavItemId = args.getInt(
                    "previous_nav_item",
                    R.id.nav_home
            );
        }


        View view = inflater.inflate(
                R.layout.fragment_a_i_chat,
                container,
                false
        );


        // =========================
        // ÁNH XẠ VIEW CHAT
        // =========================

        tvHello = view.findViewById(R.id.tvHello);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);
        btnMenu = view.findViewById(R.id.btnMenu);

        chatContainer = view.findViewById(R.id.chatContainer);
        scrollChat = view.findViewById(R.id.scrollChat);


        // =========================
        // ÁNH XẠ DRAWER
        // =========================

        drawerLayout = view.findViewById(R.id.drawerLayout);

        btnNewChat = view.findViewById(R.id.btnNewChat);

        rvChatHistory = view.findViewById(
                R.id.rvChatHistory
        );


        // =========================
        // FIREBASE AUTH
        // =========================

        firebaseAuth = FirebaseAuth.getInstance();


        // =========================
        // CHAT DAO
        // =========================

        chatDAO = new ChatDAO(requireContext());


        // =========================
        // RECYCLERVIEW LỊCH SỬ
        // =========================

        rvChatHistory.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );


        // =========================
        // LẤY USER HIỆN TẠI
        // =========================

        FirebaseUser user =
                firebaseAuth.getCurrentUser();


        if (user != null) {

            currentUserUid = user.getUid();

            String userName =
                    user.getDisplayName();

            if (userName == null ||
                    userName.trim().isEmpty()) {

                userName = "bạn";
            }


            tvHello.setText(
                    "Xin chào! " +
                            userName.trim() +
                            "\n" +
                            "Tôi có thể giúp bạn giải thích từ vựng, " +
                            "ngữ pháp, đặt câu, tạo bài tập... bất cứ điều gì " +
                            "liên quan đến việc học tiếng Anh."
            );

        } else {

            Toast.makeText(
                    requireContext(),
                    "Bạn chưa đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();
        }


        GenerativeModel ai =
                FirebaseAI
                        .getInstance(
                                GenerativeBackend.googleAI()
                        )
                        .generativeModel(
                                "gemini-3.6-flash"
                        );

        model =
                GenerativeModelFutures.from(ai);
        btnSend.setOnClickListener(
                v -> sendMessage()
        );



        btnMenu.setOnClickListener(v -> {

            loadChatHistory();

            drawerLayout.openDrawer(
                    GravityCompat.END
            );
        });


        // =========================
        // NÚT CHAT MỚI
        // =========================

        btnNewChat.setOnClickListener(v -> {

            currentConversationId = -1;

            // Xóa các tin nhắn cũ
            // Giữ lại lời chào đầu tiên
            if (chatContainer.getChildCount() > 1) {

                chatContainer.removeViews(
                        1,
                        chatContainer.getChildCount() - 1
                );
            }

            drawerLayout.closeDrawer(
                    GravityCompat.END
            );

            edtMessage.requestFocus();
        });


        // =========================
        // NÚT QUAY LẠI
        // =========================

        btnBack.setOnClickListener(v -> {

            if (isAdded()) {

                getParentFragmentManager()
                        .popBackStack();


                com.google.android.material
                        .bottomnavigation
                        .BottomNavigationView bottomNav =

                        requireActivity()
                                .findViewById(
                                        R.id.bottomNavigation
                                );


                if (bottomNav != null) {

                    bottomNav.setSelectedItemId(
                            previousNavItemId
                    );
                }
            }
        });


        return view;
    }


    // ==========================================
    // LOAD LỊCH SỬ CHAT THEO USER UID
    // ==========================================

    private void loadChatHistory() {

        if (currentUserUid == null ||
                currentUserUid.trim().isEmpty()) {

            return;
        }


        List<ChatConversation> conversationList =
                chatDAO.getConversations(
                        currentUserUid
                );


        chatHistoryAdapter =
                new com.example.englishvocabulary.adapters.ChatHistoryAdapter(
                        conversationList,
                        conversation -> {

                            openConversation(
                                    conversation
                            );

                            drawerLayout.closeDrawer(
                                    GravityCompat.END
                            );
                        }
                );


        rvChatHistory.setAdapter(
                chatHistoryAdapter
        );
    }


    // ==========================================
    // MỞ MỘT CUỘC TRÒ CHUYỆN CŨ
    // ==========================================

    private void openConversation(
            ChatConversation conversation
    ) {

        // Lưu ID cuộc trò chuyện hiện tại
        currentConversationId =
                conversation.getId();


        // Xóa toàn bộ tin nhắn cũ
        // Giữ lại lời chào ban đầu
        if (chatContainer.getChildCount() > 1) {

            chatContainer.removeViews(
                    1,
                    chatContainer.getChildCount() - 1
            );
        }


        // Lấy tin nhắn của cuộc trò chuyện
        List<ChatMessage> messageList =
                chatDAO.getMessages(
                        currentConversationId
                );


        // Hiển thị lại tin nhắn
        for (ChatMessage message : messageList) {

            if ("user".equals(
                    message.getSender()
            )) {

                addUserMessage(
                        message.getMessage()
                );

            } else {

                addAIMessage(
                        message.getMessage()
                );
            }
        }


        scrollToBottom();
    }


    // ==========================================
    // GỬI TIN NHẮN
    // ==========================================

    private void sendMessage() {

        String question =
                edtMessage
                        .getText()
                        .toString()
                        .trim();


        // Kiểm tra rỗng
        if (question.isEmpty()) {

            edtMessage.setError(
                    "Vui lòng nhập câu hỏi"
            );

            return;
        }


        // Kiểm tra đăng nhập
        if (currentUserUid == null ||
                currentUserUid.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Bạn chưa đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================
        // TẠO CUỘC TRÒ CHUYỆN ĐẦU TIÊN
        // =====================================

        if (currentConversationId == -1) {

            String title = question;

            if (title.length() > 40) {

                title =
                        title.substring(0, 40)
                                + "...";
            }


            currentConversationId =
                    chatDAO.createConversation(
                            currentUserUid,
                            title
                    );


            if (currentConversationId == -1) {

                Toast.makeText(
                        requireContext(),
                        "Không thể tạo cuộc trò chuyện",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }


        // Hiển thị câu hỏi
        addUserMessage(question);


        // Lưu câu hỏi
        chatDAO.addMessage(
                currentConversationId,
                "user",
                question
        );


        // Xóa ô nhập
        edtMessage.setText("");


        // Ẩn bàn phím
        InputMethodManager imm =
                (InputMethodManager)
                        requireContext()
                                .getSystemService(
                                        Context.INPUT_METHOD_SERVICE
                                );


        if (imm != null) {

            imm.hideSoftInputFromWindow(
                    edtMessage.getWindowToken(),
                    0
            );
        }


        // Hiển thị loading
        TextView loadingMessage =
                addAIMessage(
                        "AI đang suy nghĩ..."
                );


        // Tạo prompt
        String prompt =
                "Bạn là trợ lý học tiếng Anh trong ứng dụng QuizletApp. " +

                        "Hãy trả lời bằng tiếng Việt, giải thích dễ hiểu cho học sinh. " +

                        "Nếu người dùng hỏi về từ vựng, hãy cung cấp: " +
                        "nghĩa tiếng Việt, cách phát âm, từ loại và một ví dụ tiếng Anh. " +

                        "Nếu người dùng hỏi về ngữ pháp, hãy giải thích ngắn gọn và " +
                        "cho ví dụ.\n\n" +

                        "Câu hỏi của người dùng: " +

                        question;


        Content content =
                new Content.Builder()
                        .addText(prompt)
                        .build();


        // Gửi Gemini
        ListenableFuture<GenerateContentResponse>
                response =
                model.generateContent(content);


        Futures.addCallback(

                response,

                new FutureCallback<GenerateContentResponse>() {


                    @Override
                    public void onSuccess(
                            GenerateContentResponse result
                    ) {

                        if (!isAdded()) {
                            return;
                        }


                        String answer =
                                result.getText();


                        if (answer == null ||
                                answer.trim().isEmpty()) {

                            answer =
                                    "Xin lỗi, AI chưa có câu trả lời.";
                        }


                        // Xóa loading
                        chatContainer.removeView(
                                loadingMessage
                        );


                        // Hiển thị câu trả lời
                        addAIMessage(answer);


                        // Lưu câu trả lời AI
                        chatDAO.addMessage(
                                currentConversationId,
                                "ai",
                                answer
                        );
                    }


                    @Override
                    public void onFailure(
                            Throwable t
                    ) {

                        if (!isAdded()) {
                            return;
                        }


                        chatContainer.removeView(
                                loadingMessage
                        );


                        String errorMessage =
                                "Xin lỗi, đã xảy ra lỗi khi kết nối với AI.";


                        addAIMessage(
                                errorMessage
                        );
                    }
                },

                MoreExecutors.directExecutor()
        );
    }


    // ==========================================
    // HIỂN THỊ TIN NHẮN USER
    // ==========================================

    private void addUserMessage(
            String message
    ) {

        TextView textView =
                new TextView(
                        requireContext()
                );


        textView.setText(message);

        textView.setTextSize(14);

        textView.setTextColor(
                Color.WHITE
        );

        textView.setPadding(
                20,
                20,
                20,
                20
        );


        android.graphics.drawable
                .GradientDrawable background =

                new android.graphics.drawable
                        .GradientDrawable();


        background.setColor(
                Color.parseColor("#304FFE")
        );

        background.setCornerRadius(30);

        textView.setBackground(background);


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(

                        ViewGroup.LayoutParams.WRAP_CONTENT,

                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        params.gravity = Gravity.END;


        params.setMargins(
                60,
                4,
                4,
                12
        );


        chatContainer.addView(
                textView,
                params
        );


        scrollToBottom();
    }


    // ==========================================
    // HIỂN THỊ TIN NHẮN AI
    // ==========================================

    private TextView addAIMessage(
            String message
    ) {

        TextView textView =
                new TextView(
                        requireContext()
                );


        textView.setText(message);

        textView.setTextSize(14);

        textView.setTextColor(
                Color.rgb(
                        40,
                        40,
                        40
                )
        );


        textView.setPadding(
                20,
                20,
                20,
                20
        );


        android.graphics.drawable
                .GradientDrawable background =

                new android.graphics.drawable
                        .GradientDrawable();


        background.setColor(
                Color.rgb(
                        241,
                        245,
                        249
                )
        );

        background.setCornerRadius(30);

        textView.setBackground(background);


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(

                        ViewGroup.LayoutParams.WRAP_CONTENT,

                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        params.gravity = Gravity.START;


        params.setMargins(
                4,
                4,
                60,
                12
        );


        chatContainer.addView(
                textView,
                params
        );


        scrollToBottom();


        return textView;
    }


    // ==========================================
    // CUỘN XUỐNG CUỐI CHAT
    // ==========================================

    private void scrollToBottom() {

        scrollChat.post(
                () -> scrollChat.fullScroll(
                        View.FOCUS_DOWN
                )
        );
    }
}