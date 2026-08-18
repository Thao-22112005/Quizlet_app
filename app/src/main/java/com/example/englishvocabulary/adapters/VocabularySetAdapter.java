package com.example.englishvocabulary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.VocabularySet;

import java.util.List;

public class VocabularySetAdapter extends RecyclerView.Adapter<VocabularySetAdapter.ViewHolder> {

    private Context context;
    private List<VocabularySet> vocabularySets;
    private OnClickItemListener onClickItemListener;
    private OnSetClickListener onSetClickListener;
    private WordDAO wordDAO;

    public VocabularySetAdapter(Context context, List<VocabularySet> vocabularySets, OnClickItemListener onClickItemListener, OnSetClickListener onSetClickListener) {
        this.context = context;
        this.vocabularySets = vocabularySets;
        this.onClickItemListener = onClickItemListener;
        this.onSetClickListener = onSetClickListener;
        this.wordDAO = new WordDAO(context);
    }

    @NonNull
    @Override
    public VocabularySetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularySetAdapter.ViewHolder holder, int position) {
        VocabularySet vocabularySet = vocabularySets.get(position);
        holder.tvSetTitle.setText(vocabularySet.getTitle());
        holder.tvWordCount.setText(wordDAO.getListBySetId(vocabularySet.getId()).size() + " từ");

        if (vocabularySet.getLevel() != null && !vocabularySet.getLevel().isEmpty()) {
            holder.tvLevel.setText(vocabularySet.getLevel());
            holder.tvLevel.setVisibility(View.VISIBLE);
        } else {
            holder.tvLevel.setVisibility(View.GONE);
        }
        if (vocabularySet.getDescription() != null && !vocabularySet.getDescription().isEmpty()) {
            holder.tvSetDescription.setText(vocabularySet.getDescription());
            holder.tvSetDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvSetDescription.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if(onClickItemListener != null) {
                onClickItemListener.onClickItem(vocabularySet);
            }
        });

        holder.btnMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnMore);
            popupMenu.getMenu().add(0, 1, 0, "Sửa chủ đề");
            popupMenu.getMenu().add(0, 2, 1, "Xóa chủ đề");
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == 1) {
                    if (onSetClickListener != null) {
                        onSetClickListener.onEdit(vocabularySet);
                        return true;
                    }
                } else if (menuItem.getItemId() == 2) {
                    if (onSetClickListener != null) {
                        onSetClickListener.onDelete(vocabularySet);
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });
    }
    @Override
    public int getItemCount() {
        return vocabularySets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSetTitle, tvWordCount, tvLevel, tvSetDescription;
        ImageButton btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetTitle = itemView.findViewById(R.id.tvSetTitle);
            tvWordCount = itemView.findViewById(R.id.tvWordCount);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvSetDescription = itemView.findViewById(R.id.tvSetDescription);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

    }

    public interface OnClickItemListener {
        void onClickItem(VocabularySet vocabularySet);
    }

    public interface OnSetClickListener {
        void onEdit(VocabularySet vocabularySet);
        void onDelete(VocabularySet vocabularySet);
    }

    public void updateData(List<VocabularySet> vocabularySets) {
        this.vocabularySets = vocabularySets;
        notifyDataSetChanged();
    }
}
