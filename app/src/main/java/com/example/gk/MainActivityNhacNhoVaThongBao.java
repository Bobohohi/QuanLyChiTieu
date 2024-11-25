package com.example.gk;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.Calendar;
import java.util.List;

public class MainActivityNhacNhoVaThongBao extends AppCompatActivity {
    private Button btnBackToHome;
    private Button btnLuuNhacNho;
    private EditText etNhapTien;
    private EditText etNhapNgay;
    private Spinner cbbDanhMuc;
    private databaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nhac_nho_va_thong_bao);
        addControls();
        setupListeners();
        addEvents();
        loadCategoriesToSpinner();

    }

    private void addEvents() {
        btnLuuNhacNho.setOnClickListener(view -> {
            saveNotification();
        });
        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityNhacNhoVaThongBao.this, MainActivity.class);
            startActivity(intent);
        });
    }
    private void saveNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "ID người dùng không tìm thấy, vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            return;
        }

        String amount = etNhapTien.getText().toString().trim();
        String date = etNhapNgay.getText().toString().trim();
        String category = cbbDanhMuc.getSelectedItem().toString();

        if (amount.isEmpty() || date.isEmpty() || category.equals("Không có danh mục")) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_LONG).show();
            return;
        }

        String message = "Nhắc nhở: Bạn có khoản chi tiêu sắp tới là " + amount + " vào ngày " + date + " cho danh mục " + category + ".";
        boolean success = databaseHelper.addNotification(userId, message);
        if (success) {
            Toast.makeText(this, "Lưu thông báo thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityNhacNhoVaThongBao.this, MainActivity.class);
            intent.putExtra("notification_status", "success");
            intent.putExtra("notification_message", "Notification saved: " + message);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Lưu thông báo thất bại.", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupListeners() {
        etNhapNgay.setOnClickListener(view -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    etNhapNgay.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                }, year, month, day);
        datePickerDialog.show();
    }
    private void loadCategoriesToSpinner() {
        // Lấy danh sách các danh mục "Chi tiêu" từ database
        List<String> categories = databaseHelper.getCategoryNamesWithTypeChiTieu();

        // Nếu không có danh mục nào, thêm thông báo mặc định
        if (categories.isEmpty()) {
            categories.add("Không có danh mục");
        }

        // Tạo ArrayAdapter và gắn vào Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbbDanhMuc.setAdapter(adapter);
    }
    private void addControls() {
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnLuuNhacNho = findViewById(R.id.btnLuuNhacNho);
        etNhapTien = findViewById(R.id.etNhapTien);
        etNhapNgay = findViewById(R.id.etNhapNgay);
        cbbDanhMuc = findViewById(R.id.cbbDanhMuc);
        databaseHelper = new databaseHelper(this);
    }
}