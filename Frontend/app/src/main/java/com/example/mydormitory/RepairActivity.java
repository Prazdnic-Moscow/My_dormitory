package com.example.mydormitory;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class RepairActivity extends AppCompatActivity implements RepairRequestDialog.OnRepairRequestSubmittedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_repair);

            // Настройка Toolbar
            setupToolbar();

            // Настройка обработчиков кликов
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при запуске RepairActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.repair_title));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }

                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка настройки toolbar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        try {
            // Обработчик для "Сантехник"
            LinearLayout plumberLayout = findViewById(R.id.plumberLayout);
            if (plumberLayout != null) {
                plumberLayout.setOnClickListener(v -> showRepairRequestDialog("Сантехник"));
            }

            // Обработчик для "Плотник"
            LinearLayout carpenterLayout = findViewById(R.id.carpenterLayout);
            if (carpenterLayout != null) {
                carpenterLayout.setOnClickListener(v -> showRepairRequestDialog("Плотник"));
            }

            // Обработчик для "Электрик"
            LinearLayout electricianLayout = findViewById(R.id.electricianLayout);
            if (electricianLayout != null) {
                electricianLayout.setOnClickListener(v -> showRepairRequestDialog("Электрик"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка настройки обработчиков: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRepairRequestDialog(String serviceType) {
        try {
            RepairRequestDialog dialog = RepairRequestDialog.newInstance(serviceType);
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager, "RepairRequestDialog");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка открытия диалога: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRepairRequestSubmitted(String serviceType, String roomNumber, String description, List<String> photos) {
        // TODO: ОТПРАВИТЬ ЗАПРОС НА БЭКЕНД
        // URL: http://10.0.2.2:3000/repair-request
        // Метод: POST
        // Headers: Content-Type: application/json
        // Body: {
        //   "service_type": "Сантехник/Плотник/Электрик",
        //   "room_number": "123",
        //   "description": "Описание проблемы",
        //   "photos": ["base64_photo1", "base64_photo2"],
        //   "user_id": "current_user_id"
        // }
        
        // ЗАКОММЕНТИРОВАНО: Реальная отправка заявки на сервер
        // ApiService apiService = new ApiService();
        // apiService.submitRepairRequest(serviceType, roomNumber, description, photos, new ApiService.ApiCallback() {
        //     @Override
        //     public void onSuccess(String response) {
        //         // Обработка успешной отправки заявки
        //     }
        //     @Override
        //     public void onError(String error) {
        //         // Обработка ошибки отправки заявки
        //     }
        // });
        
        Toast.makeText(this, "Заявка на " + serviceType + " отправлена!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
