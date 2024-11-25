package com.example.gk;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivityDangNhapDangKi extends AppCompatActivity {
    private EditText etTaiKhoan,etMatKhau;
    private Button btnDangNhap,btnDangKi;
    private CheckBox cHienmk;
    private databaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dang_nhap_dang_ki);
        addControlls();
        addEvens();
    }
    private void addControlls() {
        etTaiKhoan=(EditText) findViewById(R.id.etTaiKhoan);
        etMatKhau=(EditText) findViewById(R.id.etMatKhau);
        btnDangNhap=(Button) findViewById(R.id.btnDangNhap);
        btnDangKi=(Button) findViewById(R.id.btnDangKi);
        cHienmk=(CheckBox) findViewById(R.id.cHienmk);
        databaseHelper = new databaseHelper(this);

    }

    private void addEvens() {

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etTaiKhoan.getText().toString().trim();
                String password = etMatKhau.getText().toString().trim();
                int userId = databaseHelper.getUserId(username);
                // Kiểm tra thông tin đăng nhập
                if (databaseHelper.checkUserCredentials(username, password)) {
                    // Đăng nhập thành công, chuyển đến MainActivity
                    Intent intent = new Intent(MainActivityDangNhapDangKi.this, MainActivity.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);  // userId là ID người dùng mà bạn nhận được từ cơ sở dữ liệu
                    editor.apply();

                    startActivity(intent);
                    finish(); // Để đóng màn hình đăng nhập
                } else {
                    Toast.makeText(MainActivityDangNhapDangKi.this, "Tên người dùng hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open MainActivityDangKi when "Đăng Kí" button is clicked
                Intent intent = new Intent(MainActivityDangNhapDangKi.this, MainActivityDangKi.class);
                startActivity(intent);
            }
        });

        cHienmk.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etMatKhau.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etMatKhau.setSelection(etMatKhau.getText().length());
        });


    }



}