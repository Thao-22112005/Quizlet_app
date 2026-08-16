package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText edtName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private TextInputEditText edtConfirmPassword;

    private TextView tvLogin;

    private Button btnRegister;

    // Firebase Authentication
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // ==========================================
        // KHỞI TẠO FIREBASE
        // ==========================================

        mAuth = FirebaseAuth.getInstance();


        // ==========================================
        // ÁNH XẠ VIEW
        // ==========================================

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // ==========================================
        // NÚT ĐĂNG KÝ
        // ==========================================

        btnRegister.setOnClickListener(v -> registerAccount());
    }


    // =====================================================
    // ĐĂNG KÝ TÀI KHOẢN
    // =====================================================

    private void registerAccount() {

        String name =
                edtName.getText().toString().trim();

        String email =
                edtEmail.getText().toString().trim();

        String password =
                edtPassword.getText().toString().trim();

        String confirmPassword =
                edtConfirmPassword.getText().toString().trim();


        boolean isValid = true;


        // =================================================
        // KIỂM TRA HỌ TÊN
        // =================================================

        if (TextUtils.isEmpty(name)) {

            tilName.setError("Vui lòng nhập họ và tên");

            isValid = false;

        } else {

            tilName.setErrorEnabled(false);
        }


        // =================================================
        // KIỂM TRA EMAIL
        // =================================================

        if (TextUtils.isEmpty(email)) {

            tilEmail.setError("Vui lòng nhập email");

            isValid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            tilEmail.setError("Email không hợp lệ");

            isValid = false;

        } else {

            tilEmail.setErrorEnabled(false);
        }


        // =================================================
        // KIỂM TRA MẬT KHẨU
        // =================================================

        if (TextUtils.isEmpty(password)) {

            tilPassword.setError(
                    "Vui lòng nhập mật khẩu"
            );

            isValid = false;

        } else if (password.length() < 6) {

            tilPassword.setError(
                    "Mật khẩu phải có ít nhất 6 ký tự"
            );

            isValid = false;

        } else {

            tilPassword.setErrorEnabled(false);
        }


        // =================================================
        // KIỂM TRA XÁC NHẬN MẬT KHẨU
        // =================================================

        if (TextUtils.isEmpty(confirmPassword)) {

            tilConfirmPassword.setError(
                    "Vui lòng xác nhận mật khẩu"
            );

            isValid = false;

        } else if (!password.equals(confirmPassword)) {

            tilConfirmPassword.setError(
                    "Mật khẩu xác nhận không khớp"
            );

            isValid = false;

        } else {

            tilConfirmPassword.setErrorEnabled(false);
        }


        // =================================================
        // NẾU DỮ LIỆU KHÔNG HỢP LỆ
        // =================================================

        if (!isValid) {
            return;
        }


        // =================================================
        // KHÓA NÚT ĐĂNG KÝ
        // =================================================

        btnRegister.setEnabled(false);


        // =================================================
        // TẠO TÀI KHOẢN FIREBASE
        // =================================================

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {

                        btnRegister.setEnabled(true);

                        String message =
                                "Đăng ký thất bại";

                        if (task.getException() != null) {

                            message += ": "
                                    + task.getException().getMessage();
                        }

                        Toast.makeText(
                                RegisterActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }


                    // =================================================
                    // LẤY USER VỪA TẠO
                    // =================================================

                    FirebaseUser user =
                            mAuth.getCurrentUser();

                    if (user == null) {

                        btnRegister.setEnabled(true);

                        Toast.makeText(
                                RegisterActivity.this,
                                "Không thể lấy thông tin tài khoản.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    // =================================================
                    // LƯU DISPLAY NAME VÀO FIREBASE
                    // =================================================

                    UserProfileChangeRequest profile =
                            new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();


                    user.updateProfile(profile)
                            .addOnCompleteListener(profileTask -> {


                                // =================================================
                                // GỬI EMAIL XÁC MINH
                                // =================================================

                                user.sendEmailVerification()
                                        .addOnCompleteListener(
                                                verifyTask -> {

                                                    if (verifyTask.isSuccessful()) {

                                                        Toast.makeText(
                                                                RegisterActivity.this,
                                                                "Đăng ký thành công! Hãy kiểm tra Gmail để xác minh tài khoản.",
                                                                Toast.LENGTH_LONG
                                                        ).show();


                                                        // Đăng xuất
                                                        // Người dùng phải xác minh
                                                        // email trước khi đăng nhập
                                                        mAuth.signOut();

                                                        finish();

                                                    } else {

                                                        btnRegister.setEnabled(true);

                                                        Toast.makeText(
                                                                RegisterActivity.this,
                                                                "Tạo tài khoản thành công nhưng không thể gửi email xác minh.",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }
                                                }
                                        );
                            });
                });
    }
}