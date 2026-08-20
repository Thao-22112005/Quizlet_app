package com.example.englishvocabulary.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.models.Word;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    private final OnWordActionListener listener;
    private List<Word> words;
    private final Context context;

    public WordAdapter(Context context, List<Word> words, OnWordActionListener listener) {
        this.context = context;
        this.words = words;
        this.listener = listener;
    }

    private static final int[][] COLORS = {
        {Color.parseColor("#EEF2FF"), Color.parseColor("#4F46E5")},
        {Color.parseColor("#ECFDF5"), Color.parseColor("#059669")},
        {Color.parseColor("#FFF7ED"), Color.parseColor("#EA580C")},
        {Color.parseColor("#EFF6FF"), Color.parseColor("#2563EB")},
        {Color.parseColor("#FDF2F8"), Color.parseColor("#DB2777")},
        {Color.parseColor("#F5F3FF"), Color.parseColor("#7C3AED")},
        {Color.parseColor("#FFFBEB"), Color.parseColor("#D97706")},
        {Color.parseColor("#F0FDF4"), Color.parseColor("#16A34A")},
        {Color.parseColor("#FEF2F2"), Color.parseColor("#DC2626")},
        {Color.parseColor("#F0F9FF"), Color.parseColor("#0284C7")},
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = words.get(position);
        holder.tvEnglish.setText(word.getEnglish());
        holder.tvMeaning.setText(word.getMeaning());

        if (word.getPronunciation() != null && !word.getPronunciation().isEmpty()) {
            holder.tvPronunciation.setText(word.getPronunciation());
            holder.tvPronunciation.setVisibility(View.VISIBLE);
        } else {
            holder.tvPronunciation.setVisibility(View.GONE);
        }

        if (word.getLoaiTu() != null && !word.getLoaiTu().isEmpty()) {
            holder.tvLoaiTu.setText(word.getLoaiTu());
            holder.tvLoaiTu.setVisibility(View.VISIBLE);
        } else {
            holder.tvLoaiTu.setVisibility(View.GONE);
        }

        String firstLetter = word.getEnglish().isEmpty() ? "?" : word.getEnglish().substring(0, 1).toUpperCase();
        holder.tvLetter.setText(firstLetter);

        int colorIndex = position % COLORS.length;
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(COLORS[colorIndex][0]);
        holder.iconBg.setBackground(circle);
        holder.tvLetter.setTextColor(COLORS[colorIndex][1]);

        holder.itemView.setOnClickListener(view -> showPopupMenu(view, word));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    private void showPopupMenu(View view, Word word) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add(0, 1, 0, "Sửa từ");
        popupMenu.getMenu().add(0, 2, 1, "Xoá từ");

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == 1) {
                listener.onEdit(word);
                return true;
            } else if (menuItem.getItemId() == 2) {
                listener.onDelete(word);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public interface OnWordActionListener {
        void onEdit(Word word);
        void onDelete(Word word);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEnglish, tvPronunciation, tvMeaning, tvLetter, tvLoaiTu;
        View iconBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglish = itemView.findViewById(R.id.tvEnglish);
            tvPronunciation = itemView.findViewById(R.id.tvPronunciation);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            tvLoaiTu = itemView.findViewById(R.id.tvLoaiTu);
            iconBg = itemView.findViewById(R.id.iconBg);
        }
    }

    public void updateList(List<Word> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < words.size()) {
            words.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, words.size());
        }
    }

    public Word getItem(int position) {
        return words.get(position);
    }
}
