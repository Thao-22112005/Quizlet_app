package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.MainActivity;
import com.example.englishvocabulary.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;

    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // ==========================================
        // FIREBASE
        // ==========================================

        mAuth = FirebaseAuth.getInstance();


        // ==========================================
        // ÁNH XẠ VIEW
        // ==========================================

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setPaintFlags(
                tvForgotPassword.getPaintFlags()
                        | Paint.UNDERLINE_TEXT_FLAG
        );

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        // ==========================================
        // ĐĂNG NHẬP
        // ==========================================

        btnLogin.setOnClickListener(v -> loginAccount());


        // ==========================================
        // CHUYỂN SANG ĐĂNG KÝ
        // ==========================================

        tvRegister.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    );

            startActivity(intent);
        });
    }


    // =====================================================
    // ĐĂNG NHẬP
    // =====================================================

    private void loginAccount() {
        //fake login
        edtEmail.setText("a");
        edtPassword.setText("a");

        String email =
                edtEmail.getText().toString().trim();

        String password =
                edtPassword.getText().toString().trim();

        //fake login
        if(email.equals("a") && password.equals("a")){
            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );
            startActivity(intent);
            finish();
            return;
        }


        boolean isValid = true;


        // ==========================================
        // KIỂM TRA EMAIL
        // ==========================================

        if (TextUtils.isEmpty(email)) {

            tilEmail.setError("Vui lòng nhập email");

            isValid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            tilEmail.setError("Email không hợp lệ");

            isValid = false;

        } else {

            tilEmail.setErrorEnabled(false);
        }


        // ==========================================
        // KIỂM TRA MẬT KHẨU
        // ==========================================

        if (TextUtils.isEmpty(password)) {

            tilPassword.setError(
                    "Vui lòng nhập mật khẩu"
            );

            isValid = false;

        } else {

            tilPassword.setErrorEnabled(false);
        }


        // ==========================================
        // DỪNG NẾU DỮ LIỆU KHÔNG HỢP LỆ
        // ==========================================

        if (!isValid) {
            return;
        }


        // ==========================================
        // KHÓA NÚT
        // ==========================================

        btnLogin.setEnabled(false);


        // ==========================================
        // FIREBASE ĐĂNG NHẬP
        // ==========================================

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                LoginActivity.this,
                                "Email hoặc mật khẩu không hợp lệ.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    // ==========================================
                    // LẤY USER
                    // ==========================================

                    FirebaseUser user =
                            mAuth.getCurrentUser();


                    if (user == null) {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                LoginActivity.this,
                                "Không thể lấy thông tin tài khoản.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    // ==========================================
                    // KIỂM TRA EMAIL ĐÃ XÁC MINH CHƯA
                    // ==========================================

                    if (!user.isEmailVerified()) {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                LoginActivity.this,
                                "Vui lòng xác minh email trước khi đăng nhập.",
                                Toast.LENGTH_LONG
                        ).show();

                        // Đăng xuất vì chưa xác minh
                        mAuth.signOut();

                        return;
                    }


                    // ==========================================
                    // ĐĂNG NHẬP THÀNH CÔNG
                    // ==========================================

                    Toast.makeText(
                            LoginActivity.this,
                            "Đăng nhập thành công!",
                            Toast.LENGTH_SHORT
                    ).show();


                    // ==========================================
                    // CHUYỂN SANG MAIN ACTIVITY
                    // ==========================================

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );

                    startActivity(intent);

                    finish();
                });
    }
}