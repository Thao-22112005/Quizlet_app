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
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.models.VocabularySet;

import java.util.List;

public class VocabularySetAdapter
        extends RecyclerView.Adapter<VocabularySetAdapter.ViewHolder> {

    private Context context;
    private List<VocabularySet> vocabularySetList;

    private VocabularySetDAO vocabularySetDAO;

    public VocabularySetAdapter(
            Context context,
            List<VocabularySet> vocabularySetList) {

        this.context = context;
        this.vocabularySetList = vocabularySetList;

        vocabularySetDAO =
                new VocabularySetDAO(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
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

        VocabularySet vocabularySet =
                vocabularySetList.get(position);


        // TÊN BỘ TỪ

        holder.tvTitle.setText(
                vocabularySet.getTitle()
        );


        // MÔ TẢ

        if (vocabularySet.getDescription() != null &&
                !vocabularySet.getDescription().isEmpty()) {

            holder.tvDescription.setText(
                    vocabularySet.getDescription()
            );

        } else {

            holder.tvDescription.setText(
                    "Chưa có mô tả"
            );
        }


        // ẢNH BÌA

        String coverImage =
                vocabularySet.getCoverImage();

        if (coverImage != null &&
                !coverImage.isEmpty()) {

            try {

                holder.imgCover.setImageURI(
                        Uri.parse(coverImage)
                );

            } catch (Exception e) {

                holder.imgCover.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

        } else {

            holder.imgCover.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }


        // MORE

        holder.btnMore.setOnClickListener(v -> {

            showMoreDialog(vocabularySet);
        });
    }


    // HIỂN THỊ MENU MORE

    private void showMoreDialog(
            VocabularySet vocabularySet) {

        String[] options = {
                "Sửa bộ từ",
                "Xóa bộ từ"
        };


        new AlertDialog.Builder(context)
                .setTitle("Tùy chọn")
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
                                        vocabularySet.getId()
                                );

                                intent.putExtra(
                                        "previous_nav_item",
                                        R.id.nav_library
                                );

                                context.startActivity(intent);


                            } else {

                                // XÓA

                                showDeleteDialog(
                                        vocabularySet
                                );
                            }
                        }
                )
                .show();
    }


    // XÁC NHẬN XÓA

    private void showDeleteDialog(
            VocabularySet vocabularySet) {

        new AlertDialog.Builder(context)
                .setTitle("Xóa bộ từ")
                .setMessage(
                        "Bạn có chắc muốn xóa bộ từ \"" +
                                vocabularySet.getTitle() +
                                "\" không?"
                )
                .setNegativeButton(
                        "Hủy",
                        null
                )
                .setPositiveButton(
                        "Xóa",
                        (dialog, which) -> {

                            int result =
                                    vocabularySetDAO.delete(
                                            vocabularySet.getId()
                                    );

                            if (result > 0) {

                                int position =
                                        vocabularySetList.indexOf(
                                                vocabularySet
                                        );

                                if (position != -1) {

                                    vocabularySetList.remove(
                                            position
                                    );

                                    notifyItemRemoved(
                                            position
                                    );
                                }

                                Toast.makeText(
                                        context,
                                        "Xóa bộ từ thành công",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        context,
                                        "Xóa bộ từ thất bại",
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

            btnMore =
                    itemView.findViewById(
                            R.id.btnMore
                    );
        }
    }
}