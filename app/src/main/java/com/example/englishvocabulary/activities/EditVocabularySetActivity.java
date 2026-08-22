package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.MainActivity;
import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.VocabularySetDAO;
import com.example.englishvocabulary.models.VocabularySet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditVocabularySetActivity extends AppCompatActivity {

    private TextInputLayout tilTitle, tilDescription, tilTopic, tilLevel;

    private TextInputEditText edtTitle, edtDescription, edtTopic, edtLevel;

    private ImageView imgCover;

    private Button btnChooseCover;
    private Button btnUpdate;

    private ImageButton btnBack;


    // DATABASE

    private VocabularySetDAO vocabularySetDAO;


    // FIREBASE

    private FirebaseAuth mAuth;


    // ẢNH BÌA

    private Uri selectedImageUri;


    // ID BỘ TỪ

    private int vocabularySetId = -1;


    // ID MENU TRƯỚC ĐÓ

    private int previousNavItemId;


    // ĐƯỜNG DẪN ẢNH BÌA HIỆN TẠI

    private String currentCoverImagePath;


    // Mở thư viện chọn ảnh

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            selectedImageUri = uri;

                            // Hiển thị ảnh đã chọn

                            imgCover.setImageURI(uri);
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_edit_vocabulary_set
        );


        // Lấy ID bộ từ

        vocabularySetId =
                getIntent().getIntExtra(
                        "vocabulary_set_id",
                        -1
                );


        // Lấy ID menu trước đó

        previousNavItemId =
                getIntent().getIntExtra(
                        "previous_nav_item",
                        R.id.nav_library
                );


        // KIỂM TRA ID

        if (vocabularySetId == -1) {

            Toast.makeText(
                    this,
                    "Không tìm thấy bộ từ",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // FIREBASE

        mAuth =
                FirebaseAuth.getInstance();


        // DATABASE

        vocabularySetDAO =
                new VocabularySetDAO(this);


        // ÁNH XẠ VIEW

        tilTitle =
                findViewById(R.id.tilTitle);

        tilDescription =
                findViewById(R.id.tilDescription);

        tilTopic =
                findViewById(R.id.tilTopic);

        tilLevel =
                findViewById(R.id.tilLevel);


        edtTitle =
                findViewById(R.id.edtTitle);

        edtDescription =
                findViewById(R.id.edtDescription);

        edtTopic =
                findViewById(R.id.edtTopic);

        edtLevel =
                findViewById(R.id.edtLevel);


        imgCover =
                findViewById(R.id.imgCover);

        btnChooseCover =
                findViewById(R.id.btnChooseCover);

        btnUpdate =
                findViewById(R.id.btnUpdate);

        btnBack =
                findViewById(R.id.btnBack);


        // LẤY DỮ LIỆU CŨ

        loadVocabularySet();


        // CHỌN ẢNH BÌA

        btnChooseCover.setOnClickListener(v -> {

            imagePicker.launch("image/*");

        });

        edtTitle.requestFocus();


        // CẬP NHẬT BỘ TỪ

        btnUpdate.setOnClickListener(v -> {

            updateVocabularySet();

        });


        // QUAY LẠI

        btnBack.setOnClickListener(v -> {

            goBack();

        });
    }


    // LẤY BỘ TỪ CŨ
    private void loadVocabularySet() {

        VocabularySet vocabularySet =
                vocabularySetDAO.getVocabularySetById(
                        vocabularySetId
                );


        // Không tìm thấy bộ từ

        if (vocabularySet == null) {

            Toast.makeText(
                    this,
                    "Không tìm thấy bộ từ",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // Hiển thị dữ liệu cũ

        currentCoverImagePath =
                vocabularySet.getCoverImage();

        edtTitle.setText(
                vocabularySet.getTitle()
        );

        edtDescription.setText(
                vocabularySet.getDescription()
        );

        edtTopic.setText(
                vocabularySet.getTopic()
        );

        edtLevel.setText(
                vocabularySet.getLevel()
        );

        // HIỂN THỊ ẢNH BÌA

        String coverImage =
                vocabularySet.getCoverImage();

        if (coverImage != null &&
                !coverImage.isEmpty()) {

            File imageFile =
                    new File(coverImage);

            if (imageFile.exists()) {

                imgCover.setImageURI(
                        Uri.fromFile(imageFile)
                );
            }
        }
    }


    private void updateVocabularySet() {

        // Lấy dữ liệu từ giao diện

        String title =
                edtTitle.getText().toString().trim();

        String description =
                edtDescription.getText().toString().trim();

        String topic =
                edtTopic.getText().toString().trim();

        String level =
                edtLevel.getText().toString().trim();


        // KIỂM TRA TÊN BỘ TỪ

        if (TextUtils.isEmpty(title)) {

            tilTitle.setError(
                    "Vui lòng nhập tên bộ từ"
            );

            edtTitle.requestFocus();

            return;

        } else {

            tilTitle.setError(null);
        }


        // KIỂM TRA ĐĂNG NHẬP FIREBASE

        FirebaseUser firebaseUser =
                mAuth.getCurrentUser();


        if (firebaseUser == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập trước",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // LẤY FIREBASE UID

        String userUid =
                firebaseUser.getUid();


        // THỜI GIAN HIỆN TẠI

        String currentTime =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());


        // ẢNH BÌA

        String coverImage =
                currentCoverImagePath;

        if (selectedImageUri != null) {

            // Xóa ảnh cũ để tránh rác bộ nhớ

            if (currentCoverImagePath != null &&
                    !currentCoverImagePath.isEmpty()) {

                File oldFile =
                        new File(currentCoverImagePath);

                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            coverImage =
                    saveImageToInternalStorage(
                            selectedImageUri
                    );
        }


        // TẠO OBJECT VOCABULARY SET

        VocabularySet vocabularySet =
                new VocabularySet();


        vocabularySet.setId(
                vocabularySetId
        );

        vocabularySet.setUserUid(
                userUid
        );

        vocabularySet.setTitle(
                title
        );

        vocabularySet.setDescription(
                description
        );

        vocabularySet.setTopic(
                topic
        );

        vocabularySet.setLevel(
                level
        );

        vocabularySet.setCoverImage(
                coverImage
        );

        vocabularySet.setUpdatedAt(
                currentTime
        );


        // CẬP NHẬT DATABASE

        int result =
                vocabularySetDAO.update(
                        vocabularySet
                );


        // KIỂM TRA KẾT QUẢ

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Cập nhật bộ từ thành công!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Cập nhật bộ từ thất bại!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // QUAY LẠI
    private void goBack() {

        Intent intent =
                new Intent(
                        EditVocabularySetActivity.this,
                        MainActivity.class
                );


        // Gửi ID menu trước đó về MainActivity

        intent.putExtra(
                "previous_nav_item",
                previousNavItemId
        );


        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );


        startActivity(intent);

        finish();
    }


    // LƯU ẢNH VÀO BỘ NHỚ CỦA APP

    private String saveImageToInternalStorage(Uri imageUri) {

        try {

            InputStream inputStream =
                    getContentResolver().openInputStream(
                            imageUri
                    );

            if (inputStream == null) {
                return "";
            }


            String fileName =
                    "cover_" +
                            System.currentTimeMillis() +
                            ".jpg";


            File file =
                    new File(
                            getFilesDir(),
                            fileName
                    );


            FileOutputStream outputStream =
                    new FileOutputStream(file);


            byte[] buffer =
                    new byte[1024];

            int length;


            while ((length = inputStream.read(buffer)) > 0) {

                outputStream.write(
                        buffer,
                        0,
                        length
                );
            }


            outputStream.close();

            inputStream.close();


            return file.getAbsolutePath();

        } catch (Exception e) {

            e.printStackTrace();

            return "";
        }
    }
}