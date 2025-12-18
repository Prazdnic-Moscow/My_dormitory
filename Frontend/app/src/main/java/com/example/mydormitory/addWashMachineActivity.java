package com.example.mydormitory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class addWashMachineActivity extends AppCompatActivity
{
    Button publishWashMachineButton;
    ImageButton backToWashMachineButton;
    EditText headersEditTextMachine;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_wash_machine);
        publishWashMachineButton = findViewById(R.id.publishWashMachineButton);
        backToWashMachineButton = findViewById(R.id.backToWashMachineButton);
        headersEditTextMachine = findViewById(R.id.headersEditTextMachine);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        String refreshToken = prefs.getString("refresh_token", null);

        if (accessToken != null)
        {
            // Можно использовать токен в запросах
            Log.d("TOKEN", "Access: " + accessToken);
        }
        else
        {
            // Пользователь не авторизован
            startActivity(new Intent(this, loginActivity.class));
            finish();
        }

        backToWashMachineButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (addWashMachineActivity.this, reserveMachineActivity.class);
                startActivity(intent);
            }
        });

        publishWashMachineButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String header = headersEditTextMachine.getText().toString();

                if(header.isEmpty())
                {
                    Toast.makeText(addWashMachineActivity.this, "Нужно заполнить все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Запускаем в отдельном потоке чтобы не блокировать UI
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            // 2. Отправляем данные
                            sendDocumentsData(header, accessToken, refreshToken);

                            // Показываем успех
                            runOnUiThread(() -> {
                                Toast.makeText(addWashMachineActivity.this, "Успешно отправлено!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(addWashMachineActivity.this, reserveMachineActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                        }
                        catch (Exception e)
                        {
                            runOnUiThread(() -> {
                                Toast.makeText(addWashMachineActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();

            }
        });
    }
    private void sendDocumentsData(String header,
                                   String accessToken,
                                   String refreshToken) throws Exception
    {
        String registerUrl = "http://10.0.2.2:3000/washmachine";

        JSONObject json = new JSONObject();
        json.put("name", header);

        HttpURLConnection connection = (HttpURLConnection) new URL(registerUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(json.toString().getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
        {
            //токен устарел → делаем запрос на бек /refresh
            connection.disconnect();
            if (utils.refreshAccessToken(addWashMachineActivity.this, refreshToken))
            {
                // читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);

                // повторяем исходный запрос с новым токеном
                sendDocumentsData(header, newAccess, newRefresh);
                return;
            }
            else
            {
                // Сессия истекла
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("access_token");
                        editor.remove("refresh_token");
                        editor.apply();
                        Toast.makeText(addWashMachineActivity.this, "Сессия истекла. Войдите снова", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(addWashMachineActivity.this, loginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                return;
            }
        }

        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED)
        {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null)
            {
                errorResponse.append(line);
            }
            errorReader.close();
            throw new Exception("Ошибка отправки данных: " + errorResponse.toString());
        }
        connection.disconnect();
    }
}