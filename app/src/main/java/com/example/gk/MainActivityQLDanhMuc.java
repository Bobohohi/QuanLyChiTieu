package com.example.gk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivityQLDanhMuc extends AppCompatActivity {
    private Button btnBackToHome, btnThemDanhMuc, btnXoaDanhMuc, btnSuaDanhMuc, btnXemDanhSach;
    private EditText etTenDanhMuc;
    private Spinner cbbPhanLoai, cbbXemPhanLoai;
    private ListView lvQLDanhMuc;
    private databaseHelper databaseHelper;
    private int selectedCategoryId = -1; // -1 để chỉ ra rằng không có danh mục nào được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qldanh_muc);

        addControls();
        addEvents();
        loadSpinnerPhanLoai();
        loadSpinnerXemPhanLoai();
    }

    private void loadCategories() {
        List<String> categories = databaseHelper.getCategoriesByType(cbbXemPhanLoai.getSelectedItem().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        lvQLDanhMuc.setAdapter(adapter);
    }
    private void loadSpinnerXemPhanLoai() {
        // Tạo một danh sách tĩnh với các giá trị "Thu nhập" và "Chi tiêu"
        List<String> categories = new ArrayList<>();
        categories.add("Thu nhập");
        categories.add("Chi tiêu");

        // Tạo ArrayAdapter để đưa danh sách lên Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn Adapter vào Spinner
        cbbXemPhanLoai.setAdapter(adapter);
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
    private void addEvents() {
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open MainActivityDangKi when "Đăng Kí" button is clicked
                Intent intent = new Intent(MainActivityQLDanhMuc.this, MainActivity.class);

                startActivity(intent);
            }
        });
        btnThemDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etTenDanhMuc.getText().toString();
                String type = cbbPhanLoai.getSelectedItem().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1);

                if (userId != -1){
                boolean isAdded = databaseHelper.addCategory(name, type, userId);
                if (isAdded) {
                    Toast.makeText(MainActivityQLDanhMuc.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                    loadCategories();
                }
                } else {
                    Toast.makeText(MainActivityQLDanhMuc.this, "Thêm danh mục thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvQLDanhMuc.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            selectedCategoryId = databaseHelper.getCategoryIdByName(selectedItem); // Lấy ID từ tên danh mục
            Toast.makeText(this, "Đã chọn: " + selectedItem, Toast.LENGTH_SHORT).show();
        });

        btnXoaDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategoryId != -1) {
                    boolean isDeleted = databaseHelper.deleteCategory(selectedCategoryId);
                    if (isDeleted) {
                        Toast.makeText(MainActivityQLDanhMuc.this, "Xóa danh mục thành công!", Toast.LENGTH_SHORT).show();
                        loadCategories();
                        selectedCategoryId = -1; // Reset sau khi xóa
                    } else {
                        Toast.makeText(MainActivityQLDanhMuc.this, "Xóa danh mục thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivityQLDanhMuc.this, "Vui lòng chọn danh mục để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvQLDanhMuc.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            selectedCategoryId = databaseHelper.getCategoryIdByName(selectedItem); // Lấy ID của danh mục
            String categoryType = databaseHelper.getCategoryTypeById(selectedCategoryId); // Lấy loại danh mục (nếu có)

            // Hiển thị tên và loại lên `EditText` và `Spinner`
            etTenDanhMuc.setText(selectedItem);
            if (categoryType != null) {
                int spinnerPosition = ((ArrayAdapter<String>) cbbPhanLoai.getAdapter()).getPosition(categoryType);
                cbbPhanLoai.setSelection(spinnerPosition);
            }
            Toast.makeText(this, "Đã chọn: " + selectedItem, Toast.LENGTH_SHORT).show();
        });
        btnSuaDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategoryId != -1) {
                    String newName = etTenDanhMuc.getText().toString();
                    String newType = cbbPhanLoai.getSelectedItem().toString();

                    boolean isUpdated = databaseHelper.updateCategory(selectedCategoryId, newName, newType);
                    if (isUpdated) {
                        Toast.makeText(MainActivityQLDanhMuc.this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show();
                        loadCategories();
                        selectedCategoryId = -1; // Reset sau khi cập nhật
                        etTenDanhMuc.setText(""); // Xóa thông tin trên `EditText`
                    } else {
                        Toast.makeText(MainActivityQLDanhMuc.this, "Cập nhật danh mục thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivityQLDanhMuc.this, "Vui lòng chọn danh mục để sửa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnXemDanhSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String xemphanloai = cbbXemPhanLoai.getSelectedItem().toString().trim();
                loadCategories();
            }
        });

    }

    private void addControls() {
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnThemDanhMuc = findViewById(R.id.btnThemDanhMuc);
        btnXoaDanhMuc = findViewById(R.id.btnXoaDanhMuc);
        btnSuaDanhMuc = findViewById(R.id.btnSuaDanhMuc);
        btnXemDanhSach = findViewById(R.id.btnXemDanhSach);
        databaseHelper = new databaseHelper(this);
        etTenDanhMuc = findViewById(R.id.etTenDanhMuc);
        cbbPhanLoai = findViewById(R.id.cbbMucChiTieu);
        cbbXemPhanLoai = findViewById(R.id.cbbXemPhanLoai);
        lvQLDanhMuc = findViewById(R.id.lvQLDanhMuc);
    }
}