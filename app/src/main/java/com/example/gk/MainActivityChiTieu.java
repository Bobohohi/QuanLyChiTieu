package com.example.gk;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivityChiTieu extends AppCompatActivity {
    private Button btnBackToHome, btnPhanLoai, btnThemCTTN;
    private EditText etNhapTien, etNhapNgay, etNhapGio, etNhapMT;
    private Spinner cbbPhanLoai, cbbDanhMuc;
    private databaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chi_tieu);

        int userId = getIntent().getIntExtra("user_id", -1);
        addControls();
        addEvents();
        setupListeners();
        loadSpinnerPhanLoai();
    }

    private void loadSpinnerPhanLoai() {
        // Tạo một danh sách tĩnh với các giá trị "Thu nhập" và "Chi tiêu"
        List<String> categories = new ArrayList<>();
        categories.add("Thu nhập");
        categories.add("Chi tiêu");

        // Tạo ArrayAdapter để đưa danh sách lên Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn Adapter vào Spinner
        cbbPhanLoai.setAdapter(adapter);
    }
    private void loadSpinnerDanhMuc(String type) {
        List<String> categories = databaseHelper.getCategoriesByType(type);

        // Kiểm tra nếu categories không rỗng để tránh lỗi
        if (categories != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cbbDanhMuc.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        etNhapNgay.setOnClickListener(view -> showDatePickerDialog());
        etNhapGio.setOnClickListener(view -> showTimePickerDialog());
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    etNhapGio.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }, hour, minute, true);
        timePickerDialog.show();
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

    private void addControls() {
        databaseHelper = new databaseHelper(this);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnPhanLoai = findViewById(R.id.btnMucChiTieu);
        btnThemCTTN = findViewById(R.id.btnThemCTTN);

        etNhapTien = findViewById(R.id.etNhapTien);
        etNhapNgay = findViewById(R.id.etNhapNgay);
        etNhapGio = findViewById(R.id.etNhapGio);
        etNhapMT = findViewById(R.id.etNhapMT);

        cbbPhanLoai = findViewById(R.id.cbbMucChiTieu);
        cbbDanhMuc = findViewById(R.id.cbbDanhMuc);

    }
    private void addEvents() {
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityChiTieu.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnPhanLoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phanloai = cbbPhanLoai.getSelectedItem().toString().trim();
                loadSpinnerDanhMuc(phanloai);
            }
        });
        btnThemCTTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountStr = etNhapTien.getText().toString().trim();
                String date = etNhapNgay.getText().toString().trim();
                String time = etNhapGio.getText().toString().trim();
                String description = etNhapMT.getText().toString().trim();
                String danhmuc = cbbDanhMuc.getSelectedItem().toString().trim();
                String mucchitieu=cbbPhanLoai.getSelectedItem().toString().trim();
                if (amountStr.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(MainActivityChiTieu.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Chuyển đổi amount sang kiểu double
                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Log.e("MainActivityChiTieu", "Số tiền không hợp lệ");
                    return; // Thoát nếu số tiền không hợp lệ
                }
                // Lấy user_id từ cơ sở dữ liệu (giả sử bạn đã có phương thức này)
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1);   // Ví dụ: sử dụng user_id tĩnh hoặc lấy từ session đăng nhập
                // Lấy category_id từ tên danh mục đã chọn
                int categoryId = databaseHelper.getCategoryIdByName(danhmuc);
                // Kiểm tra nếu categoryId không hợp lệ
                if (categoryId == -1) {
                    Log.e("MainActivityChiTieu", "Không tìm thấy danh mục");
                    return;
                }
                // Thêm giao dịch vào cơ sở dữ liệu
                boolean success = databaseHelper.addTransaction(amount, date, time, description, categoryId, userId);

                if (success) {
                    Toast.makeText(MainActivityChiTieu.this, "Giao dịch đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivityChiTieu.this, MainActivity.class);
                    intent.putExtra("transaction_added", true);
                    intent.putExtra("amount_added", amount);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("MainActivityChiTieu", "Thêm giao dịch thất bại với amount: " + amount + ", date: " + date + ", time: " + time + ", description: " + description + ", categoryId: " + categoryId + ", userId: " + userId);
                    Toast.makeText(MainActivityChiTieu.this, "Thêm giao dịch thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}