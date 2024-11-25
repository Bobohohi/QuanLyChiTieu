package com.example.gk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivityBaoCaoVaPhanTich extends AppCompatActivity {
    private Button btnBackToHome,btnXem;
    private databaseHelper databaseHelper;
    private ListView lvBaoCao;
    private Spinner cbbDanhMuc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bao_cao_va_phan_tich);
        addControls();
        addEvents();
        loadSpinnerPhanLoai();
        loadTransaction(cbbDanhMuc.getSelectedItem().toString());
    }
    private void loadSpinnerPhanLoai() {
        // Tạo một danh sách tĩnh với các giá trị "Thu nhập" và "Chi tiêu"
        List<String> categories = new ArrayList<>();
        categories.add("Thu nhập");
        categories.add("Chi tiêu");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbbDanhMuc.setAdapter(adapter);
    }
    private void addControls() {
        btnBackToHome=findViewById(R.id.btnBackToHome);
        databaseHelper = new databaseHelper(this);
        lvBaoCao = findViewById(R.id.lvBaoCao);
        cbbDanhMuc=findViewById(R.id.cbbDanhMuc);
        btnXem=findViewById(R.id.btnXem);
    }
    private void loadTransaction(String type) {
        List<String> transactions = databaseHelper.getTransactionsByType(type); // Triển khai phương thức này trong databaseHelper
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactions);
        lvBaoCao.setAdapter(adapter);
    }
    private void addEvents() {
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open MainActivityDangKi when "Đăng Kí" button is clicked
                Intent intent = new Intent(MainActivityBaoCaoVaPhanTich.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnXem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = cbbDanhMuc.getSelectedItem().toString().trim();
                loadTransaction(selectedType);
            }
        });
    }
}