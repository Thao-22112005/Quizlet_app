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

    private OnWordActionListener listener;
    private List<Word> words;
    private Context context;

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
    public WordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordAdapter.ViewHolder(view);
    }

    // --- GẮN DỮ LIỆU VÀO VIEW ---
    // Gọi mỗi khi 1 item cần hiển thị (scroll)
    @Override
    public void onBindViewHolder(@NonNull WordAdapter.ViewHolder holder, int position) {
        Word word = words.get(position);
        holder.tvEnglish.setText(word.getEnglish());
        holder.tvMeaning.setText(word.getMeaning());

        if(word.getPronunciation() != null && !word.getPronunciation().isEmpty()) {
            holder.tvPronunciation.setText(word.getPronunciation());
            holder.tvPronunciation.setVisibility(View.VISIBLE);
        } else {
            holder.tvPronunciation.setText("");
            holder.tvPronunciation.setVisibility(View.GONE);
        }

        // Lấy chữ cái đầu
        String firstLetter = word.getEnglish().substring(0, 1);

        holder.tvLetter.setText(firstLetter.toUpperCase());

        int colorIndex = position % COLORS.length;
        // Tạo hình tròn
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(COLORS[colorIndex][0]);
//        circle.setStroke(2, COLORS[colorIndex][1]);// cân nhắc thêm
        holder.iconBg.setBackground(circle);
        holder.tvLetter.setTextColor(COLORS[colorIndex][1]);

        holder.itemView.setOnClickListener(view -> {
            showPopupMenu(view, word);
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    private void showPopupMenu(View view, Word word){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add(0, 1, 0, "Sửa từ");
        popupMenu.getMenu().add(0, 2, 1, "Xoá từ");

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId() == 1){
                listener.onEdit(word);
                return true;
            } else if(menuItem.getItemId() == 2){
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

        TextView tvEnglish;
        TextView tvPronunciation;
        TextView tvMeaning;
        TextView tvLetter;
        View iconBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglish = itemView.findViewById(R.id.tvEnglish);
            tvPronunciation = itemView.findViewById(R.id.tvPronunciation);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            iconBg = itemView.findViewById(R.id.iconBg);
        }
    }

    public void updateList(List<Word> words){
        this.words = words;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        words.remove(position);
        notifyItemRemoved(position);
    }

    public Word getItem(int position){
        words.get(position);
        notifyItemChanged(position);
        return words.get(position);
    }

}
