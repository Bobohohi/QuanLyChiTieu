package com.example.gk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivityThietLapNganSach extends AppCompatActivity {
    private Button btnBackToHome, btnSave;
    private Spinner cbbMucChiTieu;
    private EditText etNhapTien;
    private databaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_thiet_lap_ngan_sach);
        addControls();
        addEvents();
        loadSpinnerData();
    }
    private void loadSpinnerData() {
        List<String> categoryNames = databaseHelper.getCategoryNamesWithTypeChiTieu();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbbMucChiTieu.setAdapter(adapter);
    }


    private void addEvents() {
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityThietLapNganSach.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị từ EditText và Spinner
                String categoryName = cbbMucChiTieu.getSelectedItem().toString(); // Lấy tên danh mục đã chọn
                String amountText = etNhapTien.getText().toString(); // Lấy số tiền từ EditText

                if (!amountText.isEmpty()) {
                    double amount = Double.parseDouble(amountText); // Chuyển đổi số tiền sang kiểu double

                    // Lấy user_id từ SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("user_id", -1);  // Lấy user_id

                    if (userId != -1) {
                        // Lấy category_id từ tên danh mục
                        int categoryId = databaseHelper.getCategoryIdByName(categoryName);

                        // Kiểm tra nếu categoryId hợp lệ trước khi lưu vào database
                        if (categoryId != -1) {
                            // Lưu vào bảng Budgets
                            boolean isSaved = databaseHelper.addBudget(categoryId, amount, userId);

                            if (isSaved) {
                                // Hiển thị thông báo thành công
                                Toast.makeText(MainActivityThietLapNganSach.this, "Đã lưu ngân sách!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Hiển thị thông báo lỗi
                                Toast.makeText(MainActivityThietLapNganSach.this, "Lỗi khi lưu ngân sách", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivityThietLapNganSach.this, "Danh mục không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivityThietLapNganSach.this, "Chưa đăng nhập hoặc thông tin người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivityThietLapNganSach.this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addControls() {
        btnBackToHome=(Button) findViewById(R.id.btnBackToHome);
        btnSave = findViewById(R.id.btnSave);
        cbbMucChiTieu=findViewById(R.id.cbbMucChiTieu);
        etNhapTien=findViewById(R.id.etNhapTien);
        databaseHelper = new databaseHelper(this);
    }


}