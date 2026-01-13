package com.example.mydormitory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class loginActivity extends AppCompatActivity
{
    private EditText login, password;
    private Button buttonForgotPassword, buttonLogin, buttonRegistration;
    private String accessToken;
    private String refreshToken, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        refreshToken = prefs.getString("refresh_token", null);
        userType = prefs.getString("type", null);

        if (accessToken != null && userType.equals("Студент"))
        {
            // Пользователь авторизован
            startActivity(new Intent(this, newsActivity.class));
            finish();
            return;
        }
        if (accessToken != null && userType.equals("Ремонтник"))
        {
            // Пользователь авторизован
            startActivity(new Intent(this, newsforrepairmanActivity.class));
            finish();
            return;
        }

        // ВАЖНО: Инициализация ДО использования!
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegistration = findViewById(R.id.buttonRegistration);

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userLogin = login.getText().toString();
                String userPassword = password.getText().toString();
                // Простая проверка и переход
                if (userLogin.isEmpty() || userPassword.isEmpty())
                {
                    Toast.makeText(loginActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendLoginData(userLogin, userPassword);
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(loginActivity.this, "Восстановление пароля", Toast.LENGTH_LONG).show();
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Переход на активность регистрации
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendLoginData(String login, String password) {
        new Thread(() -> {
            try
            {
                String apiUrl = "http://10.0.2.2:3000/login";

                // Тело запроса
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("phone_number", login);
                jsonBody.put("password", password);

                // Настройка соединения
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Отправляем JSON на сервер
                try (OutputStream os = conn.getOutputStream())
                {
                    os.write(jsonBody.toString().getBytes("UTF-8"));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    // Читаем ответ целиком одной строкой
                    String response = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    ).lines().reduce("", (acc, line) -> acc + line);

                    // Парсим JSON-ответ
                    JSONObject json = new JSONObject(response);
                    String status = json.optString("status");
                    String access = json.optString("access_token");
                    String refresh = json.optString("refresh_token");
                    String type = json.optString("userType");

                    if ("success".equals(status))
                    {
                        runOnUiThread(() -> {
                            //Сохраняем токены
                            getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("access_token", access)
                                    .putString("refresh_token", refresh)
                                    .putString("type", type)
                                    .apply();

                            Toast.makeText(this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(this, "type = " + type, Toast.LENGTH_SHORT).show();
                            if (type.equals("Студент"))
                            {
                                // Переход на главный экран
                                startActivity(new Intent(this, allWidjet.class));
                                finish();
                            }
                            if (type.equals("Ремонтник"))
                            {
                                startActivity(new Intent(this, newsforrepairmanActivity.class));
                                finish();
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                        );
                    }
                }
                else
                {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Ошибка соединения: " + responseCode, Toast.LENGTH_LONG).show()
                    );
                }

                conn.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }



}