package com.example.englishvocabulary.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.activities.CreateVocabularySetActivity;
import com.example.englishvocabulary.adapters.VocabularySetAdapter;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.models.VocabularySet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.activities.VocabularySetDetailActivity;
import com.example.englishvocabulary.adapters.VocabularySetAdapter;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.models.VocabularySet;

import java.util.ArrayList;
import java.util.List;

public class ThuVienFragment extends Fragment {

    private RecyclerView recyclerVocabularySet;

    private TextView tvEmpty;

    private TextInputEditText edtSearch;

    private ImageButton btnAddVocabulary;

    // DATABASE
    private VocabularySetDAO vocabularySetDAO;

    // FIREBASE
    private FirebaseAuth mAuth;

    // ADAPTER
    private VocabularySetAdapter adapter;

    // DANH SÁCH BỘ TỪ
    private List<VocabularySet> vocabularySetList;

    private List<VocabularySet> allVocabularySetList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_thu_vien,
                container,
                false
        );


        // ÁNH XẠ VIEW

        recyclerVocabularySet =
                view.findViewById(
                        R.id.recyclerVocabularySet
                );

        tvEmpty =
                view.findViewById(
                        R.id.tvEmpty
                );

        edtSearch =
                view.findViewById(
                        R.id.edtSearch
                );

        btnAddVocabulary =
                view.findViewById(
                        R.id.btnAddVocabulary
                );


        // DATABASE

        vocabularySetDAO =
                new VocabularySetDAO(requireContext());


        // FIREBASE

        mAuth =
                FirebaseAuth.getInstance();


        // KHỞI TẠO DANH SÁCH

        vocabularySetList =
                new ArrayList<>();

        allVocabularySetList =
                new ArrayList<>();


        // RECYCLERVIEW

        recyclerVocabularySet.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter =
                new VocabularySetAdapter(
                        requireContext(),
                        vocabularySetList
                );

        recyclerVocabularySet.setAdapter(adapter);


        // NÚT TẠO BỘ TỪ

        btnAddVocabulary.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            CreateVocabularySetActivity.class
                    );

            intent.putExtra(
                    "previous_nav_item",
                    R.id.nav_library
            );

            startActivity(intent);
        });


        // TÌM KIẾM

        edtSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterVocabularySet(
                                s.toString()
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );


        // LẤY DỮ LIỆU

        loadVocabularySet();

    private RecyclerView rvVocabularySets;
    private LinearLayout layoutEmpty;
    private EditText edtSearch;

    private VocabularySetDAO vocabularySetDAO;
    private VocabularySetAdapter vocabularySetAdapter;
    private List<VocabularySet> allSets;

    public ThuVienFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_thu_vien, container, false);

        rvVocabularySets = view.findViewById(R.id.rvVocabularySets);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        edtSearch = view.findViewById(R.id.edtSearch);

        vocabularySetDAO = new VocabularySetDAO(getContext());

        setupRecyclerView();
        setupSearch();

        return view;
    }

    // KHI QUAY LẠI FRAGMENT
    @Override
    public void onResume() {

        super.onResume();

        loadVocabularySet();
    }

    // LẤY DANH SÁCH BỘ TỪ
    private void loadVocabularySet() {

        FirebaseUser firebaseUser =
                mAuth.getCurrentUser();


        // KIỂM TRA ĐĂNG NHẬP

        if (firebaseUser == null) {

            vocabularySetList.clear();

            allVocabularySetList.clear();

            adapter.notifyDataSetChanged();

            tvEmpty.setText(
                    "Vui lòng đăng nhập"
            );

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            recyclerVocabularySet.setVisibility(
                    View.GONE
            );

            return;
        }


        // LẤY FIREBASE UID

        String userUid =
                firebaseUser.getUid();


        // LẤY DANH SÁCH TỪ DATABASE

        List<VocabularySet> list =
                vocabularySetDAO.getByUserUid(
                        userUid
                );


        allVocabularySetList.clear();

        allVocabularySetList.addAll(list);


        vocabularySetList.clear();

        vocabularySetList.addAll(list);


        adapter.notifyDataSetChanged();


        // KIỂM TRA DANH SÁCH

        if (vocabularySetList.isEmpty()) {

            tvEmpty.setText(
                    "Chưa có bộ từ nào"
            );

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            recyclerVocabularySet.setVisibility(
                    View.GONE
            );

        } else {

            tvEmpty.setVisibility(
                    View.GONE
            );

            recyclerVocabularySet.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // TÌM KIẾM BỘ TỪ
    private void filterVocabularySet(String keyword) {

        String search =
                keyword.trim().toLowerCase();


        vocabularySetList.clear();


        // NẾU KHÔNG NHẬP TỪ KHÓA

        if (search.isEmpty()) {

            vocabularySetList.addAll(
                    allVocabularySetList
            );

        } else {

            // TÌM THEO TÊN BỘ TỪ

            for (VocabularySet vocabularySet :
                    allVocabularySetList) {

                String title =
                        vocabularySet.getTitle();


                if (title != null &&
                        title.toLowerCase()
                                .contains(search)) {

                    vocabularySetList.add(
                            vocabularySet
                    );
                }
            }
        }


        adapter.notifyDataSetChanged();


        // KIỂM TRA KẾT QUẢ TÌM KIẾM

        if (vocabularySetList.isEmpty()) {

            tvEmpty.setText(
                    "Không tìm thấy bộ từ"
            );

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            recyclerVocabularySet.setVisibility(
                    View.GONE
            );

        } else {

            tvEmpty.setVisibility(
                    View.GONE
            );

            recyclerVocabularySet.setVisibility(
                    View.VISIBLE
            );
        }
        super.onResume();
        loadData();
    }

    // =====================================================
    // SETUP RECYCLERVIEW
    // =====================================================

    private void setupRecyclerView() {
        allSets = new ArrayList<>();

        vocabularySetAdapter = new VocabularySetAdapter(
                getContext(),
                allSets,
                new VocabularySetAdapter.OnClickItemListener() {
                    @Override
                    public void onClickItem(VocabularySet vocabularySet) {
                        Intent intent = new Intent(
                                getContext(),
                                VocabularySetDetailActivity.class);
                        intent.putExtra("set_id", vocabularySet.getId());
                        intent.putExtra("set_title", vocabularySet.getTitle());
                        startActivity(intent);
                    }
                },
                new VocabularySetAdapter.OnSetClickListener() {
                    @Override
                    public void onEdit(VocabularySet set) {
                        showEditDialog(set);
                    }

                    @Override
                    public void onDelete(VocabularySet set) {
                        new com.google.android.material.dialog.MaterialAlertDialogBuilder(
                                getContext())
                                .setTitle("Xoá chủ đề")
                                .setMessage("Xoá \"" + set.getTitle()
                                        + "\" và tất cả từ bên trong?")
                                .setPositiveButton("Xoá", (d, w) -> {
                                    vocabularySetDAO.delete(set.getId());
                                    loadData();
                                })
                                .setNegativeButton("Huỷ", null)
                                .show();
                    }
                });

        rvVocabularySets.setLayoutManager(
                new LinearLayoutManager(getContext()));
        rvVocabularySets.setAdapter(vocabularySetAdapter);
    }

    // =====================================================
    // DIALOG SỬA CHỦ ĐỀ
    // =====================================================

    private void showEditDialog(VocabularySet set) {
        android.widget.EditText input = new android.widget.EditText(getContext());
        input.setText(set.getTitle());
        input.setSelectAllOnFocus(true);
        input.setPadding(48, 32, 48, 16);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(
                getContext())
                .setTitle("Sửa tên chủ đề")
                .setView(input)
                .setPositiveButton("Lưu", (d, w) -> {
                    String newTitle = input.getText().toString().trim();
                    if (!newTitle.isEmpty()) {
                        set.setTitle(newTitle);
                        vocabularySetDAO.update(set);
                        loadData();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    // =====================================================
    // SETUP TÌM KIẾM
    // =====================================================

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filterSets(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // =====================================================
    // LOAD DỮ LIỆU
    // =====================================================

    private void loadData() {
        allSets = vocabularySetDAO.getAll();

        if (allSets.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvVocabularySets.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvVocabularySets.setVisibility(View.VISIBLE);
            vocabularySetAdapter.updateData(allSets);
        }
    }

    // =====================================================
    // LỌC
    // =====================================================

    private void filterSets(String keyword) {
        if (keyword.isEmpty()) {
            vocabularySetAdapter.updateData(allSets);
            return;
        }

        List<VocabularySet> filtered = new ArrayList<>();
        for (VocabularySet set : allSets) {
            if (set.getTitle().toLowerCase()
                    .contains(keyword.toLowerCase())) {
                filtered.add(set);
            }
        }
        vocabularySetAdapter.updateData(filtered);
    }
}
