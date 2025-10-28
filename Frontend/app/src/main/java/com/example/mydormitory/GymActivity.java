package com.example.mydormitory;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class GymActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        // Настройка Toolbar
        setupToolbar();
        
        // TODO: ДОБАВИТЬ API ЗАПРОСЫ ДЛЯ СПОРТЗАЛА
        // URL: http://10.0.2.2:3000/gym-booking
        // Метод: POST
        // Headers: Content-Type: application/json
        // Body: {
        //   "date": "2024-01-15",
        //   "time_slot": "10:00-12:00",
        //   "user_id": "current_user_id"
        // }
        // 
        // Также нужны запросы для:
        // - Получения доступных временных слотов: GET /gym-availability?date=2024-01-15
        // - Отмены бронирования: DELETE /gym-booking/{booking_id}
        // - Получения активных бронирований: GET /user-gym-bookings
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.gym));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
