package com.example.englishvocabulary.activities;

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

    private ImageButton btnBack;
    private ImageView imgCover;
    private Button btnChooseCover;
    private Button btnCreate;

    private TextInputLayout tilTitle;

    private TextInputEditText edtTitle;
    private TextInputEditText edtDescription;
    private TextInputEditText edtTopic;
    private TextInputEditText edtLevel;

    private VocabularySetDAO vocabularySetDAO;
    private FirebaseAuth mAuth;

    private Uri selectedImageUri;


    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            selectedImageUri = uri;

                            imgCover.setImageURI(
                                    selectedImageUri
                            );
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_create_vocabulary_set
        );


        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();


        // SQLite
        vocabularySetDAO =
                new VocabularySetDAO(this);


        // Ánh xạ view
        initViews();

        edtTitle.requestFocus();


        // Sự kiện
        initEvents();
    }

    private void initViews() {

        btnBack =
                findViewById(R.id.btnBack);

        btnChooseCover =
                findViewById(R.id.btnChooseCover);

        btnCreate =
                findViewById(R.id.btnCreate);

        imgCover =
                findViewById(R.id.imgCover);


        tilTitle =
                findViewById(R.id.tilTitle);


        edtTitle =
                findViewById(R.id.edtTitle);

        edtDescription =
                findViewById(R.id.edtDescription);

        edtTopic =
                findViewById(R.id.edtTopic);

        edtLevel =
                findViewById(R.id.edtLevel);
    }

    private void initEvents() {

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());


        // Chọn ảnh bìa
        btnChooseCover.setOnClickListener(v ->
                imagePicker.launch("image/*")
        );


        // Tạo bộ từ
        btnCreate.setOnClickListener(v ->
                createVocabularySet()
        );
    }


    private void createVocabularySet() {

        String title =
                edtTitle.getText()
                        .toString()
                        .trim();

        String description =
                edtDescription.getText()
                        .toString()
                        .trim();

        String topic =
                edtTopic.getText()
                        .toString()
                        .trim();

        String level =
                edtLevel.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(title)) {

            tilTitle.setError(
                    "Vui lòng nhập tên bộ từ"
            );

            edtTitle.requestFocus();

            return;
        }

        tilTitle.setError(null);

        FirebaseUser user =
                mAuth.getCurrentUser();


        if (user == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (vocabularySetDAO.isTitleExists(
                user.getUid(),
                title
        )) {

            tilTitle.setError(
                    "Tên bộ từ này đã tồn tại"
            );

            edtTitle.requestFocus();

            return;
        }


        tilTitle.setError(null);


        String currentTime =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

        String coverImage = "";

        if (selectedImageUri != null) {

            coverImage =
                    saveImageToInternalStorage(
                            selectedImageUri
                    );
        }

        VocabularySet set =
                new VocabularySet();


        set.setUserUid(
                user.getUid()
        );

        set.setTitle(
                title
        );

        set.setDescription(
                description
        );

        set.setTopic(
                topic
        );

        set.setLevel(
                level
        );

        set.setCoverImage(
                coverImage
        );

        set.setCreatedAt(
                currentTime
        );

        set.setUpdatedAt(
                currentTime
        );

        long result =
                vocabularySetDAO.insert(set);


        if (result != -1) {

            Toast.makeText(
                    this,
                    "Tạo bộ từ thành công!",
                    Toast.LENGTH_SHORT
            ).show();


            // Quay lại màn hình trước
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Lỗi khi lưu bộ từ",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String saveImageToInternalStorage(
            Uri uri
    ) {

        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {

            // Mở ảnh
            inputStream =
                    getContentResolver()
                            .openInputStream(uri);


            if (inputStream == null) {

                return "";
            }


            // Tạo tên file
            String fileName =
                    "cover_" +
                            System.currentTimeMillis() +
                            ".jpg";


            // Thư mục internal storage của app
            File file =
                    new File(
                            getFilesDir(),
                            fileName
                    );


            // Tạo output stream
            outputStream =
                    new FileOutputStream(file);


            // Bộ nhớ đệm
            byte[] buffer =
                    new byte[4096];

            int length;


            // Copy ảnh
            while (
                    (length =
                            inputStream.read(buffer)) > 0
            ) {

                outputStream.write(
                        buffer,
                        0,
                        length
                );
            }


            outputStream.flush();


            // Trả về đường dẫn file
            return file.getAbsolutePath();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Không thể lưu ảnh bìa",
                    Toast.LENGTH_SHORT
            ).show();

            return "";

        } finally {

            // Đóng input
            if (inputStream != null) {

                try {

                    inputStream.close();

                } catch (Exception ignored) {
                }
            }


            // Đóng output
            if (outputStream != null) {

                try {

                    outputStream.close();

                } catch (Exception ignored) {
                }
            }
        }
    }
}