package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextPhone;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;
    private TextView textViewRegister;
    private ProgressBar progressBar;
    
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация views
        initViews();
        
        // Инициализация API сервиса
        apiService = new ApiService();
        
        // Установка обработчиков событий
        setupClickListeners();
        
        // Настройка форматирования номера телефона
        setupPhoneFormatting();
    }

    private void initViews() {
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewRegister = findViewById(R.id.textViewRegister);
        progressBar = findViewById(R.id.progressBar);
        
        // Скрываем прогресс-бар по умолчанию
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Реализовать восстановление пароля
                Toast.makeText(LoginActivity.this, "Функция восстановления пароля", Toast.LENGTH_SHORT).show();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupPhoneFormatting() {
        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString();
                
                // Убираем все символы кроме цифр
                String digitsOnly = phone.replaceAll("[^\\d]", "");
                
                // Если номер не начинается с 7, добавляем +7
                if (!phone.startsWith("+7") && !phone.startsWith("7")) {
                    if (digitsOnly.length() > 0) {
                        String formatted = "+7" + digitsOnly;
                        if (!formatted.equals(phone)) {
                            editTextPhone.setText(formatted);
                            editTextPhone.setSelection(formatted.length());
                        }
                    }
                } else if (phone.startsWith("7") && !phone.startsWith("+7")) {
                    // Если начинается с 7, но без +
                    String formatted = "+" + phone;
                    if (!formatted.equals(phone)) {
                        editTextPhone.setText(formatted);
                        editTextPhone.setSelection(formatted.length());
                    }
                }
                
                // Ограничиваем длину до 12 символов (+7XXXXXXXXXX)
                if (phone.length() > 12) {
                    String truncated = phone.substring(0, 12);
                    if (!truncated.equals(phone)) {
                        editTextPhone.setText(truncated);
                        editTextPhone.setSelection(truncated.length());
                    }
                }
            }
        });
    }

    private void performLogin() {
        try {
            String phone = editTextPhone.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Валидация полей
            if (TextUtils.isEmpty(phone)) {
                editTextPhone.setError("Введите номер телефона");
                editTextPhone.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Введите пароль");
                editTextPassword.requestFocus();
                return;
            }

            // Проверка формата российского номера телефона (+7XXXXXXXXXX)
            if (!phone.matches("^\\+7\\d{10}$")) {
                editTextPhone.setError("Введите корректный номер телефона (+7XXXXXXXXXX)");
                editTextPhone.requestFocus();
                return;
            }

            // Показываем прогресс-бар и блокируем кнопку
            showLoading(true);

            // Отправляем запрос к бекенду
            apiService.login(phone, password, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                        
                        // Переходим в главную активность
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Закрываем LoginActivity
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, "Ошибка входа: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            showLoading(false);
            Toast.makeText(this, "Ошибка при входе: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        buttonLogin.setEnabled(!show);
    }
}
