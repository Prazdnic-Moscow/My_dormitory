package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class GuideActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // TODO: ЗАПРОС НА БЭКЕНД - Получить информацию о гайдах
        // URL: http://10.0.2.2:3000/guide
        // Метод: GET
        // Response: {"study_room": {...}, "gym": {...}}
        
        // ЗАКОММЕНТИРОВАНО: Запрос информации о гайдах с бэка
        // ApiService apiService = new ApiService();
        // apiService.getGuideInfo(new ApiService.ApiCallback() {
        //     @Override
        //     public void onSuccess(String response) {
        //         // Обновить информацию о гайдах
        //     }
        //     @Override
        //     public void onError(String error) {
        //         Toast.makeText(this, "Ошибка загрузки гайдов: " + error, Toast.LENGTH_SHORT).show();
        //     }
        // });

        // Настройка Toolbar
        setupToolbar();

        // Настройка обработчиков кликов
        setupClickListeners();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.guide_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        try {
            // Обработчик для "Учебная комната"
            LinearLayout studyRoomLayout = findViewById(R.id.guideLayout);
            if (studyRoomLayout != null) {
                studyRoomLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(this, StudyRoomActivity.class);
                    startActivity(intent);
                });
            }

            // Обработчик для "Спортзал"
            LinearLayout gymLayout = findViewById(R.id.gymLayout);
            if (gymLayout != null) {
                gymLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(this, GymActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
