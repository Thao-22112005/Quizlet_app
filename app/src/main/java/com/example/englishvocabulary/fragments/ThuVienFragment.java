package com.example.englishvocabulary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;


public class ThuVienFragment extends Fragment {


    // =====================================================
    // VIEW
    // =====================================================

    private RecyclerView recyclerVocabularySet;

    private LinearLayout layoutEmpty;

    private TextView tvEmptyText;

    private TextInputEditText edtSearch;

    private ImageButton btnAddVocabulary;


    // =====================================================
    // DATABASE
    // =====================================================

    private VocabularySetDAO vocabularySetDAO;


    // =====================================================
    // FIREBASE
    // =====================================================

    private FirebaseAuth mAuth;


    // =====================================================
    // ADAPTER
    // =====================================================

    private VocabularySetAdapter adapter;


    // =====================================================
    // LIST
    // =====================================================

    // Danh sách đang hiển thị
    private final List<VocabularySet> vocabularySetList =
            new ArrayList<>();


    // Danh sách đầy đủ dùng để tìm kiếm
    private final List<VocabularySet> allVocabularySetList =
            new ArrayList<>();


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ThuVienFragment() {
        // Required empty public constructor
    }


    // =====================================================
    // ON CREATE VIEW
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view =
                inflater.inflate(
                        R.layout.fragment_thu_vien,
                        container,
                        false
                );


        // -------------------------------------------------
        // ÁNH XẠ VIEW
        // -------------------------------------------------

        initViews(view);


        // -------------------------------------------------
        // DATABASE
        // -------------------------------------------------

        vocabularySetDAO =
                new VocabularySetDAO(
                        requireContext()
                );


        // -------------------------------------------------
        // FIREBASE AUTH
        // -------------------------------------------------

        mAuth =
                FirebaseAuth.getInstance();


        // -------------------------------------------------
        // RECYCLER VIEW
        // -------------------------------------------------

        setupRecyclerView();


        // -------------------------------------------------
        // SỰ KIỆN
        // -------------------------------------------------

        setupListeners();


        // -------------------------------------------------
        // TÌM KIẾM
        // -------------------------------------------------

        setupSearch();


        // -------------------------------------------------
        // TẢI DỮ LIỆU
        // -------------------------------------------------

        loadVocabularySet();


        return view;
    }


    // =====================================================
    // ÁNH XẠ VIEW
    // =====================================================

    private void initViews(View view) {

        recyclerVocabularySet =
                view.findViewById(
                        R.id.recyclerVocabularySet
                );


        layoutEmpty =
                view.findViewById(
                        R.id.layoutEmpty
                );


        tvEmptyText =
                view.findViewById(
                        R.id.tvEmptyText
                );


        edtSearch =
                view.findViewById(
                        R.id.edtSearch
                );


        btnAddVocabulary =
                view.findViewById(
                        R.id.btnAddVocabulary
                );
    }


    // =====================================================
    // RECYCLER VIEW
    // =====================================================

    private void setupRecyclerView() {

        recyclerVocabularySet.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        adapter =
                new VocabularySetAdapter(
                        requireContext(),
                        vocabularySetList
                );


        recyclerVocabularySet.setAdapter(
                adapter
        );
    }


    // =====================================================
    // SỰ KIỆN
    // =====================================================

    private void setupListeners() {

        // Nút tạo bộ từ
        btnAddVocabulary.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    requireContext(),
                                    CreateVocabularySetActivity.class
                            );


                    // Gửi màn hình hiện tại
                    intent.putExtra(
                            "previous_nav_item",
                            R.id.nav_library
                    );


                    startActivity(intent);
                }
        );
    }


    // =====================================================
    // TÌM KIẾM
    // =====================================================

    private void setupSearch() {

        edtSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filterVocabularySet(
                                s.toString()
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }


    // =====================================================
    // TẢI DANH SÁCH BỘ TỪ
    // =====================================================

    private void loadVocabularySet() {

        // -------------------------------------------------
        // Kiểm tra Fragment còn hoạt động
        // -------------------------------------------------

        if (!isAdded()) {
            return;
        }


        // -------------------------------------------------
        // Lấy user Firebase
        // -------------------------------------------------

        FirebaseUser firebaseUser =
                mAuth.getCurrentUser();


        // -------------------------------------------------
        // Chưa đăng nhập
        // -------------------------------------------------

        if (firebaseUser == null) {

            allVocabularySetList.clear();

            vocabularySetList.clear();

            adapter.notifyDataSetChanged();


            updateUI(
                    true,
                    "Vui lòng đăng nhập để xem thư viện"
            );

            return;
        }


        // -------------------------------------------------
        // UID người dùng
        // -------------------------------------------------

        String userUid =
                firebaseUser.getUid();


        // -------------------------------------------------
        // LẤY DỮ LIỆU TỪ SQLITE
        // -------------------------------------------------

        List<VocabularySet> list =
                vocabularySetDAO.getByUserUid(
                        userUid
                );


        // -------------------------------------------------
        // LƯU DANH SÁCH GỐC
        // -------------------------------------------------

        allVocabularySetList.clear();

        if (list != null) {

            allVocabularySetList.addAll(
                    list
            );
        }


        // -------------------------------------------------
        // HIỂN THỊ DANH SÁCH
        // -------------------------------------------------

        vocabularySetList.clear();

        vocabularySetList.addAll(
                allVocabularySetList
        );


        // -------------------------------------------------
        // CẬP NHẬT ADAPTER
        // -------------------------------------------------

        adapter.notifyDataSetChanged();


        // -------------------------------------------------
        // CẬP NHẬT GIAO DIỆN
        // -------------------------------------------------

        if (vocabularySetList.isEmpty()) {

            updateUI(
                    true,
                    "Chưa có bộ từ nào. Nhấn + để tạo mới!"
            );

        } else {

            updateUI(
                    false,
                    ""
            );
        }
    }


    // =====================================================
    // TÌM KIẾM BỘ TỪ
    // =====================================================

    private void filterVocabularySet(
            String keyword
    ) {

        String search =
                keyword
                        .trim()
                        .toLowerCase();


        // Xóa danh sách hiện tại
        vocabularySetList.clear();


        // -------------------------------------------------
        // Không nhập từ khóa
        // -------------------------------------------------

        if (search.isEmpty()) {

            vocabularySetList.addAll(
                    allVocabularySetList
            );

        }

        // -------------------------------------------------
        // Có từ khóa
        // -------------------------------------------------

        else {

            for (
                    VocabularySet set :
                    allVocabularySetList
            ) {

                String title =
                        set.getTitle();


                if (
                        title != null
                                &&
                                title
                                        .toLowerCase()
                                        .contains(search)
                ) {

                    vocabularySetList.add(
                            set
                    );
                }
            }
        }


        // -------------------------------------------------
        // Cập nhật RecyclerView
        // -------------------------------------------------

        adapter.notifyDataSetChanged();


        // -------------------------------------------------
        // Cập nhật Empty State
        // -------------------------------------------------

        if (vocabularySetList.isEmpty()) {

            if (search.isEmpty()) {

                updateUI(
                        true,
                        "Chưa có bộ từ nào. Nhấn + để tạo mới!"
                );

            } else {

                updateUI(
                        true,
                        "Không tìm thấy kết quả phù hợp"
                );
            }

        } else {

            updateUI(
                    false,
                    ""
            );
        }
    }


    // =====================================================
    // CẬP NHẬT GIAO DIỆN
    // =====================================================

    private void updateUI(
            boolean isEmpty,
            String message
    ) {

        if (isEmpty) {

            layoutEmpty.setVisibility(
                    View.VISIBLE
            );

            recyclerVocabularySet.setVisibility(
                    View.GONE
            );

            tvEmptyText.setText(
                    message
            );

        } else {

            layoutEmpty.setVisibility(
                    View.GONE
            );

            recyclerVocabularySet.setVisibility(
                    View.VISIBLE
            );
        }
    }


    // =====================================================
    // KHI QUAY LẠI FRAGMENT
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();

        /*
         * Khi từ CreateVocabularySetActivity quay lại,
         * Fragment sẽ tải lại dữ liệu từ SQLite.
         *
         * Vì vậy bộ từ vừa tạo sẽ xuất hiện ngay.
         */

        if (mAuth != null) {

            loadVocabularySet();
        }
    }


    // =====================================================
    // HỦY VIEW
    // =====================================================

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        recyclerVocabularySet = null;
        layoutEmpty = null;
        tvEmptyText = null;
        edtSearch = null;
        btnAddVocabulary = null;
    }
}