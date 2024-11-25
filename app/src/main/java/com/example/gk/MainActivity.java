package com.example.gk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button  btnBCPT, btnDash, btnNganSach,
            btnNhacNho, btnDanhMuc, btnQLChiTieu, btnQLThuNhap;
    private TextView txtName,txtTaiChinh,txtSoDu,txtTongChiTieu;
    private databaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUser_id();
        addControls();
        addEvents();
        loadThuNhap();
        loadChiTieu();
        loadSoDu();
    }
    private void loadUser_id(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        TextView txtName = findViewById(R.id.txtName); // Đảm bảo R.id.txtName là ID của TextView trong layout
        txtName.setText("Xin Chào , User ID : " + userId);
    }
    private void addControls() {
        txtName = findViewById(R.id.txtName);

        btnBCPT = findViewById(R.id.btnBCPT);

        btnNganSach = findViewById(R.id.btnNganSach);
        btnNhacNho = findViewById(R.id.btnNhacNho);
        btnDanhMuc = findViewById(R.id.btnDanhMuc);
        btnQLChiTieu = findViewById(R.id.btnQLChiTieu);

        txtTaiChinh=findViewById(R.id.txtTaiChinh);
        txtSoDu=findViewById(R.id.txtSoDu);
        txtTongChiTieu=findViewById(R.id.txtTongChiTieu);
        databaseHelper = new databaseHelper(this);
    }
    private void loadThuNhap() {
        double totalIncome = databaseHelper.getTotalIncomeFromTransactions();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtTaiChinh.setText(formatter.format(totalIncome));
    }
    private void loadChiTieu() {
        double totalExpenses = databaseHelper.getTotalExpensesFromTransactions();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtTongChiTieu.setText(formatter.format(totalExpenses));
    }
    private void loadSoDu() {
        double totalIncome = databaseHelper.getTotalIncomeFromTransactions();
        double totalExpenses = databaseHelper.getTotalExpensesFromTransactions();
        double soDu = totalIncome - totalExpenses;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtSoDu.setText(formatter.format(soDu));
    }
    private void addEvents() {
        btnBCPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityBaoCaoVaPhanTich.class);
                startActivity(intent);
            }
        });

        btnNganSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityThietLapNganSach.class);
                startActivity(intent);
            }
        });

        btnNhacNho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityNhacNhoVaThongBao.class);
                startActivity(intent);
            }
        });

        btnDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityQLDanhMuc.class);

                startActivity(intent);
            }
        });

        btnQLChiTieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityChiTieu.class);

                startActivity(intent);
            }
        });


    }
}