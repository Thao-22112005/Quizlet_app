package com.example.englishvocabulary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.models.ChatConversation;

import java.util.List;

public class ChatHistoryAdapter
        extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {

    private final List<ChatConversation> conversationList;

    private final OnConversationClickListener listener;


    public interface OnConversationClickListener {

        void onConversationClick(
                ChatConversation conversation
        );
    }


    public ChatHistoryAdapter(
            List<ChatConversation> conversationList,
            OnConversationClickListener listener
    ) {

        this.conversationList =
                conversationList;

        this.listener =
                listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_chat_history,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        ChatConversation conversation =
                conversationList.get(position);


        holder.tvConversationTitle.setText(
                conversation.getTitle()
        );


        holder.tvConversationTime.setText(
                conversation.getUpdatedAt()
        );


        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onConversationClick(
                        conversation
                );
            }
        });
    }


    @Override
    public int getItemCount() {

        return conversationList.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvConversationTitle;
        TextView tvConversationTime;


        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            tvConversationTitle =
                    itemView.findViewById(
                            R.id.tvConversationTitle
                    );


            tvConversationTime =
                    itemView.findViewById(
                            R.id.tvConversationTime
                    );
        }
    }
}