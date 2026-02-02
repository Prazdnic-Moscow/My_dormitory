package com.example.mydormitory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsForRepairManActivity extends AppCompatActivity implements NewsForRepairManAdapter.OnRepairButtonClickListener
{
    private RecyclerView newsRecyclerView;
    private NewsForRepairManAdapter newsAdapter;
    private List<newsforrepairman> newsList = new ArrayList<>();
    private static final String API_URL = "http://10.0.2.2:3000/news/";
    private static final String UPDATE_STATUS_URL = "http://10.0.2.2:3000/activaterepair";
    private static final int NEWS_LIMIT = 50; // лимит новостей
    private String accessToken;
    private String refreshToken;
    private String userType;
    private int currentUserId;

    ImageButton allWidjetForRepairBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsforrepairman);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        refreshToken = prefs.getString("refresh_token", null);
        userType = prefs.getString("type", null);
        currentUserId = utils.getUserIdFromToken(this, accessToken, refreshToken);
        allWidjetForRepairBtn = findViewById(R.id.allWidjetForRepairBtn);

        if (accessToken == null)
        {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            // Пользователь не авторизован
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }

        allWidjetForRepairBtn.setOnClickListener(v -> {
            Intent intent = new Intent (NewsForRepairManActivity.this, allWidjetForRepairman.class);
            startActivity(intent);
            finish();
        });

        newsRecyclerView = findViewById(R.id.newsListForRepairman);

        // Настройка RecyclerView
        newsAdapter = new NewsForRepairManAdapter(newsList);
        // Устанавливаем listener для адаптера
        newsAdapter.setOnRepairButtonClickListener(this);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        // Загрузка данных с API
        loadNewsFromApi();
    }

    @Override
    public void onRepairButtonClick(int position, newsforrepairman news) {
        boolean newStatus = !news.getActivity();

        if (newStatus) {
            // Обновляем на сервере
            updateRepairStatusOnServer(news.getId(), accessToken, newStatus, position, currentUserId);

            Toast.makeText(this, "Заказ взят!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateRepairStatusOnServer(int repairId, String access, boolean newStatus, int position, int userId) {
        final int safePosition = position;
        new Thread(() -> {
            try {
                sendPatchRequest(UPDATE_STATUS_URL, access, repairId, newStatus, userId);

                runOnUiThread(() -> {
                    // Проверяем позицию как в MyRepairsActivity
                    if (safePosition >= 0 && safePosition < newsList.size()) {
                        newsList.remove(safePosition);
                        newsAdapter.notifyItemRemoved(safePosition);
                        Toast.makeText(NewsForRepairManActivity.this, "Заказ взят и перемещён в «Мои заказы»", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        loadNewsFromApi();
                    }
                });

            }
            catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(NewsForRepairManActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                loadNewsFromApi();
            }
        }).start();
    }

    private void sendPatchRequest(String urlString, String accessToken, int repairId, boolean newStatus, int userId) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("repair_id", repairId);
        jsonBody.put("activity", newStatus);
        jsonBody.put("user_id", userId);
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8")))
        {
            writer.write(jsonBody.toString());
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
        {
            //токен устарел → делаем запрос на бек /refresh
            connection.disconnect();
            if (utils.refreshAccessToken(NewsForRepairManActivity.this, refreshToken))
            {
                // читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);
                NewsForRepairManActivity.this.accessToken = newAccess;
                NewsForRepairManActivity.this.refreshToken = newRefresh;
                // повторяем исходный запрос с новым токеном
                sendPatchRequest(urlString, newAccess, repairId, newStatus, userId);
                return;
            }
            else
            {
                // Сессия истекла
                runOnUiThread(() -> {
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("access_token");
                    editor.remove("refresh_token");
                    editor.apply();
                    Toast.makeText(NewsForRepairManActivity.this, "Сессия истекла. Войдите снова", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewsForRepairManActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
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

    private void loadNewsFromApi() {
        new Thread(() -> {
            try {
                String response = sendGetRequest(accessToken, refreshToken, NEWS_LIMIT, userType);
                JSONArray jsonArray = new JSONArray(response);
                List<newsforrepairman> allNews =
                        utils.parseNewsFromJson(jsonArray);

                List<newsforrepairman> freeNews = new ArrayList<>();

                for (newsforrepairman n : allNews) {
                    if (!n.getActivity()) {
                        freeNews.add(n);
                    }
                }

                runOnUiThread(() -> {
                    newsList.clear();
                    newsList.addAll(freeNews);
                    newsAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(NewsForRepairManActivity.this, "Ошибка загрузки Новостей: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private String sendGetRequest(String accessToken, String refreshToken, int limit, String userType) throws Exception {
        // Добавляем лимит в URL
        String urlWithLimit = API_URL + limit + "/" + userType;
        HttpURLConnection connection = (HttpURLConnection) new URL(urlWithLimit).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            connection.disconnect();
            if (utils.refreshAccessToken(NewsForRepairManActivity.this, refreshToken))
            {
                // Читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);

                // Обновляем переменные класса
                NewsForRepairManActivity.this.accessToken = newAccess;
                NewsForRepairManActivity.this.refreshToken = newRefresh;

                // Повторяем исходный запрос с новым токеном
                return sendGetRequest(newAccess, newRefresh, limit, userType);
            }
            else
            {
                // Сессия истекла
                runOnUiThread(() -> {
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("access_token");
                    editor.remove("refresh_token");
                    editor.apply();

                    Toast.makeText(NewsForRepairManActivity.this,
                            "Сессия истекла. Войдите снова",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewsForRepairManActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
                return null;
            }
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            throw new Exception("Ошибка получения данных: " + errorResponse.toString());
        }

        // Читаем успешный ответ
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();

        return response.toString();
    }
}