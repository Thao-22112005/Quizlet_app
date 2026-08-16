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

public class CreateVocabularySetActivity extends AppCompatActivity {

    private TextInputLayout tilTitle, tilDescription, tilTopic, tilLevel;

    private TextInputEditText edtTitle, edtDescription, edtTopic, edtLevel;

    private ImageView imgCover;
    private Button btnChooseCover;
    private ImageButton btnBack;
    private Button btnCreate;

    // DATABASE
    private VocabularySetDAO vocabularySetDAO;

    // FIREBASE
    private FirebaseAuth mAuth;

    // ẢNH BÌA
    private Uri selectedImageUri;

    private int previousNavItemId;


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

        setContentView(R.layout.activity_create_vocabulary_set);


        // Lấy ID menu trước đó
        previousNavItemId = getIntent().getIntExtra(
                "previous_nav_item",
                R.id.nav_home
        );


        // FIREBASE
        mAuth = FirebaseAuth.getInstance();


        // DATABASE
        vocabularySetDAO =
                new VocabularySetDAO(this);


        // ÁNH XẠ VIEW

        tilTitle = findViewById(R.id.tilTitle);
        tilDescription = findViewById(R.id.tilDescription);
        tilTopic = findViewById(R.id.tilTopic);
        tilLevel = findViewById(R.id.tilLevel);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtTopic = findViewById(R.id.edtTopic);
        edtLevel = findViewById(R.id.edtLevel);

        imgCover = findViewById(R.id.imgCover);
        btnChooseCover = findViewById(R.id.btnChooseCover);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);


        // CHỌN ẢNH BÌA

        btnChooseCover.setOnClickListener(v -> {

            imagePicker.launch("image/*");

        });


        // TẠO BỘ TỪ

        btnCreate.setOnClickListener(v -> {

            createVocabularySet();

        });


        // QUAY LẠI

        btnBack.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CreateVocabularySetActivity.this,
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
        });
    }


    // =====================================================
    // TẠO BỘ TỪ
    // =====================================================

    private void createVocabularySet() {

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

        // ẢNH BÌA

        String coverImage = "";

        if (selectedImageUri != null) {

            coverImage =
                    saveImageToInternalStorage(
                            selectedImageUri
                    );
        }


        // TẠO OBJECT VOCABULARY SET

        VocabularySet vocabularySet =
                new VocabularySet();

        vocabularySet.setUserUid(userUid);

        vocabularySet.setTitle(title);

        vocabularySet.setDescription(description);

        vocabularySet.setTopic(topic);

        vocabularySet.setLevel(level);

        vocabularySet.setCoverImage(coverImage);

        vocabularySet.setCreatedAt(currentTime);

        vocabularySet.setUpdatedAt(currentTime);


        // LƯU DATABASE

        long id =
                vocabularySetDAO.insert(
                        vocabularySet
                );


        // KIỂM TRA KẾT QUẢ

        if (id != -1) {

            Toast.makeText(
                    this,
                    "Tạo bộ từ thành công!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Tạo bộ từ thất bại!",
                    Toast.LENGTH_SHORT
            ).show();
        }
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


            // Tạo tên file

            String fileName =
                    "cover_" +
                            System.currentTimeMillis() +
                            ".jpg";


            File file =
                    new File(
                            getFilesDir(),
                            fileName
                    );


            // Ghi ảnh vào bộ nhớ app

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


            // Trả về đường dẫn file

            return file.getAbsolutePath();

        } catch (Exception e) {

            e.printStackTrace();

            return "";
        }
    }
}