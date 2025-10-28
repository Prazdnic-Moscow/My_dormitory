package com.example.mydormitory;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StudyRoomActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);

        // Настройка Toolbar
        setupToolbar();
        
        // TODO: ДОБАВИТЬ API ЗАПРОСЫ ДЛЯ ЧИТАЛЬНОГО ЗАЛА
        // URL: http://10.0.2.2:3000/study-room-booking
        // Метод: POST
        // Headers: Content-Type: application/json
        // Body: {
        //   "room_number": 1,
        //   "date": "2024-01-15",
        //   "time_slot": "10:00-12:00",
        //   "user_id": "current_user_id"
        // }
        // 
        // Также нужны запросы для:
        // - Получения доступных комнат: GET /study-rooms
        // - Получения доступных временных слотов: GET /study-room-availability?room=1&date=2024-01-15
        // - Отмены бронирования: DELETE /study-room-booking/{booking_id}
        // - Получения активных бронирований: GET /user-study-room-bookings
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.study_room));
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
