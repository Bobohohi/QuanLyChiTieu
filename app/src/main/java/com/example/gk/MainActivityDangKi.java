package com.example.gk;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivityDangKi extends AppCompatActivity {

    private EditText etTen, etEmail, etMatKhau, etNhapLai;
    private CheckBox cHienmk;
    private Button btnDangKi,btnBack;
    private databaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dang_ki);
        addControlls();
        addEvens();
    }

    private void addControlls() {
        etTen = findViewById(R.id.etTen);
        etEmail = findViewById(R.id.etEmail);
        etMatKhau = findViewById(R.id.etMatKhau_DK);
        etNhapLai = findViewById(R.id.etNhapLai);
        cHienmk = findViewById(R.id.cHienmk);
        btnDangKi = findViewById(R.id.btnDangKi_DK);
        btnBack=findViewById(R.id.btnBackToDN);
        databaseHelper = new databaseHelper(this);
    }

    private void addEvens() {
        cHienmk.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etMatKhau.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etNhapLai.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etNhapLai.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etMatKhau.setSelection(etMatKhau.getText().length());
            etNhapLai.setSelection(etNhapLai.getText().length());
        });

        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etTen.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etMatKhau.getText().toString().trim();
                String confirmPassword = etNhapLai.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(MainActivityDangKi.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!confirmPassword.equals(password)) {
                    Toast.makeText(MainActivityDangKi.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                } else if (databaseHelper.checkUserCredentials(username, password)) {
                    Toast.makeText(MainActivityDangKi.this, "Tên người dùng đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    // Thêm người dùng mới vào cơ sở dữ liệu
                    boolean insert = databaseHelper.addUser(username, email, password);
                    if (insert) {
                        Toast.makeText(MainActivityDangKi.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình đăng nhập
                    } else {
                        Toast.makeText(MainActivityDangKi.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open MainActivityDangKi when "Đăng Kí" button is clicked
                Intent intent = new Intent(MainActivityDangKi.this, MainActivityDangNhapDangKi.class);
                startActivity(intent);
            }
        });


    }
}