package com.example.englishvocabulary.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.activities.EditVocabularySetActivity;
import com.example.englishvocabulary.activities.VocabularySetDetailActivity;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.VocabularySet;

import java.io.File;
import java.util.List;

public class VocabularySetAdapter
        extends RecyclerView.Adapter<VocabularySetAdapter.ViewHolder> {

    private Context context;
    private List<VocabularySet> vocabularySetList;

    private VocabularySetDAO vocabularySetDAO;
    private WordDAO wordDAO;


    public VocabularySetAdapter(
            Context context,
            List<VocabularySet> vocabularySetList) {

        this.context = context;
        this.vocabularySetList = vocabularySetList;

        vocabularySetDAO =
                new VocabularySetDAO(context);

        wordDAO =
                new WordDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.item_vocabulary_set,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        VocabularySet set =
                vocabularySetList.get(position);

        holder.tvTitle.setText(
                set.getTitle()
        );

        if (set.getDescription() != null
                && !set.getDescription().isEmpty()) {

            holder.tvDescription.setText(
                    set.getDescription()
            );

        } else {

            holder.tvDescription.setText(
                    "Không có mô tả"
            );
        }

        if (set.getLevel() != null
                && !set.getLevel().isEmpty()) {

            holder.tvLevel.setText(
                    set.getLevel()
            );

        } else {

            holder.tvLevel.setText(
                    "Chưa đặt trình độ"
            );
        }

        int count =
                wordDAO
                        .getWordCountBySetId(set.getId());

        holder.tvWordCount.setText(
                count + " thuật ngữ"
        );

        String coverPath =
                set.getCoverImage();

        if (coverPath != null
                && !coverPath.isEmpty()) {

            File imgFile =
                    new File(coverPath);

            if (imgFile.exists()) {

                holder.imgCover.setImageURI(
                        Uri.fromFile(imgFile)
                );

            } else {

                holder.imgCover.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

        } else {

            holder.imgCover.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            VocabularySetDetailActivity.class
                    );


            // ID của bộ từ
            intent.putExtra(
                    "set_id",
                    set.getId()
            );


            // Tên bộ từ
            intent.putExtra(
                    "set_title",
                    set.getTitle()
            );


            context.startActivity(intent);
        });

        holder.btnMore.setOnClickListener(v -> {

            showMoreDialog(
                    set,
                    position
            );
        });
    }

    private void showMoreDialog(
            VocabularySet set,
            int position) {

        String[] options = {
                "Sửa bộ từ",
                "Xoá bộ từ"
        };


        new AlertDialog.Builder(context)

                .setTitle("Tuỳ chọn")

                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                Intent intent =
                                        new Intent(
                                                context,
                                                EditVocabularySetActivity.class
                                        );

                                intent.putExtra(
                                        "vocabulary_set_id",
                                        set.getId()
                                );

                                context.startActivity(
                                        intent
                                );
                            }

                            else {

                                showDeleteDialog(
                                        set,
                                        position
                                );
                            }
                        }
                )
                .show();
    }


    private void showDeleteDialog(
            VocabularySet set,
            int position) {

        new AlertDialog.Builder(context)

                .setTitle("Xoá bộ từ")

                .setMessage(
                        "Bạn có chắc chắn muốn xoá bộ từ \""
                                + set.getTitle()
                                + "\" không?"
                )

                .setNegativeButton(
                        "Huỷ",
                        null
                )

                .setPositiveButton(
                        "Xoá",
                        (dialog, which) -> {

                            if (
                                    vocabularySetDAO.delete(
                                            set.getId()
                                    ) > 0
                            ) {

                                vocabularySetList.remove(
                                        position
                                );

                                notifyItemRemoved(
                                        position
                                );

                                notifyItemRangeChanged(
                                        position,
                                        vocabularySetList.size()
                                );

                                Toast.makeText(
                                        context,
                                        "Đã xoá bộ từ",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        context,
                                        "Xoá thất bại",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }


    @Override
    public int getItemCount() {

        return vocabularySetList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgCover;

        TextView tvTitle;
        TextView tvDescription;
        TextView tvWordCount;
        TextView tvLevel;

        ImageButton btnMore;


        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            imgCover =
                    itemView.findViewById(
                            R.id.imgCover
                    );

            tvTitle =
                    itemView.findViewById(
                            R.id.tvTitle
                    );

            tvDescription =
                    itemView.findViewById(
                            R.id.tvDescription
                    );

            tvWordCount =
                    itemView.findViewById(
                            R.id.tvWordCount
                    );

            tvLevel =
                    itemView.findViewById(
                            R.id.tvLevel
                    );

            btnMore =
                    itemView.findViewById(
                            R.id.btnMore
                    );
        }
    }


    public void updateData(
            List<VocabularySet> newList) {

        this.vocabularySetList =
                newList;

        notifyDataSetChanged();
    }
}