package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;

    private TextInputEditText edtEmail;

    private Button btnSendReset;

    private TextView tvBack;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);


        // ==========================================
        // FIREBASE
        // ==========================================

        mAuth = FirebaseAuth.getInstance();


        // ==========================================
        // ÁNH XẠ VIEW
        // ==========================================

        tilEmail = findViewById(R.id.tilEmail);

        edtEmail = findViewById(R.id.edtEmail);

        btnSendReset = findViewById(R.id.btnSendReset);

        tvBack = findViewById(R.id.tvBack);


        // ==========================================
        // GỬI LINK RESET
        // ==========================================

        btnSendReset.setOnClickListener(
                v -> sendResetEmail()
        );


        // ==========================================
        // QUAY LẠI LOGIN
        // ==========================================

        tvBack.setOnClickListener(v -> finish());
    }


    // =====================================================
    // GỬI EMAIL ĐẶT LẠI MẬT KHẨU
    // =====================================================

    private void sendResetEmail() {

        String email =
                edtEmail.getText().toString().trim();


        // ==========================================
        // KIỂM TRA EMAIL
        // ==========================================

        if (TextUtils.isEmpty(email)) {

            tilEmail.setError(
                    "Vui lòng nhập email"
            );

            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            tilEmail.setError(
                    "Email không hợp lệ"
            );

            return;
        }


        tilEmail.setErrorEnabled(false);


        // ==========================================
        // KHÓA NÚT
        // ==========================================

        btnSendReset.setEnabled(false);


        // ==========================================
        // FIREBASE GỬI EMAIL
        // ==========================================

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    btnSendReset.setEnabled(true);


                    if (task.isSuccessful()) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Đã gửi link đặt lại mật khẩu. Hãy kiểm tra Gmail và đặt lại mật khẩu của bạn.",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent =
                                new Intent(
                                        ForgotPasswordActivity.this,
                                        LoginActivity.class
                                );

                        startActivity(intent);

                    } else {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Email chưa được đăng ký hoặc không hợp lệ.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}