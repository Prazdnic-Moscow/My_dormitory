package com.example.mydormitory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class newsforrepairmanActivity extends AppCompatActivity implements newsforrepairmanAdapter.OnRepairButtonClickListener
{
    private RecyclerView newsRecyclerView;
    private newsforrepairmanAdapter newsAdapter;
    private List<newsforrepairman> newsList = new ArrayList<>();
    private static final String API_URL = "http://10.0.2.2:3000/news/";
    private static final String UPDATE_STATUS_URL = "http://10.0.2.2:3000/activaterepair";
    private static final int NEWS_LIMIT = 50; // лимит новостей
    private String accessToken;
    private String refreshToken;
    private String userType;

    ImageButton exitFromRepairman;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsforrepairman);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        refreshToken = prefs.getString("refresh_token", null);
        userType = prefs.getString("type", null);
        exitFromRepairman = findViewById(R.id.exitFromRepairman);

        if (accessToken == null)
        {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            // Пользователь не авторизован
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }

        exitFromRepairman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("access_token");
                editor.remove("refresh_token");
                editor.apply();
                Intent intent = new Intent (newsforrepairmanActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        newsRecyclerView = findViewById(R.id.newsListForRepairman);

        // Настройка RecyclerView
        newsAdapter = new newsforrepairmanAdapter(newsList);
        // Устанавливаем listener для адаптера
        newsAdapter.setOnRepairButtonClickListener(this);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        // Загрузка данных с API
        loadNewsFromApi();
    }

    @Override
    public void onRepairButtonClick(int position, newsforrepairman news) {
        // Инвертируем статус активности
        boolean newStatus = !news.getActivity();
        updateRepairStatusOnServer(news.getId(), newStatus, position);
    }

    private void updateRepairStatusOnServer(int repairId, boolean newStatus, int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    sendPatchRequest(UPDATE_STATUS_URL, repairId, newStatus);
                    // Если сервер успешно обновил статус
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsforrepairman updatedNews = newsList.get(position);
                            updatedNews.setActivity(newStatus);
                            // Обновляем UI
                            newsAdapter.notifyItemChanged(position);
                            Toast.makeText(newsforrepairmanActivity.this, "Статус заказа обновлен", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (Exception e)
                {
                    runOnUiThread(() -> {
                        Toast.makeText(newsforrepairmanActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void sendPatchRequest(String urlString, int repairId, boolean newStatus) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", repairId);
        jsonBody.put("activity", newStatus);
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
            if (utils.refreshAccessToken(newsforrepairmanActivity.this, refreshToken))
            {
                // читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);
                newsforrepairmanActivity.this.accessToken = newAccess;
                newsforrepairmanActivity.this.refreshToken = newRefresh;
                // повторяем исходный запрос с новым токеном
                sendPatchRequest(urlString, repairId, newStatus);
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
                        Toast.makeText(newsforrepairmanActivity.this, "Сессия истекла. Войдите снова", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(newsforrepairmanActivity.this, loginActivity.class);
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

    private void loadNewsFromApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = sendGetRequest(accessToken, refreshToken, NEWS_LIMIT, userType);
                    JSONArray jsonArray = new JSONArray(response);
                    final List<newsforrepairman> news = parseNewsFromJson(jsonArray);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsList.clear();
                            newsList.addAll(news);
                            newsAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(newsforrepairmanActivity.this, "Ошибка загрузки Новостей: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
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
            if (utils.refreshAccessToken(newsforrepairmanActivity.this, refreshToken))
            {
                // Читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);

                // Обновляем переменные класса
                newsforrepairmanActivity.this.accessToken = newAccess;
                newsforrepairmanActivity.this.refreshToken = newRefresh;

                // Повторяем исходный запрос с новым токеном
                return sendGetRequest(newAccess, newRefresh, limit, userType);
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

                        Toast.makeText(newsforrepairmanActivity.this,
                                "Сессия истекла. Войдите снова",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(newsforrepairmanActivity.this, loginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
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

    private List<newsforrepairman> parseNewsFromJson(JSONArray jsonArray) throws JSONException {
        List<newsforrepairman> news = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject guideJson = jsonArray.getJSONObject(i);

            int id = guideJson.getInt("id");
            String type = guideJson.getString("type");
            String body = guideJson.getString("body");
            String date = guideJson.getString("date");
            int room = guideJson.getInt("room");
            boolean activity = guideJson.getBoolean("activity");

            // Парсим массив изображений
            List<String> imagePaths = new ArrayList<>();
            if (guideJson.has("news_path")) {
                JSONArray imagesArray = guideJson.getJSONArray("news_path");
                for (int j = 0; j < imagesArray.length(); j++) {
                    imagePaths.add(imagesArray.getString(j));
                }
            }

            news.add(new newsforrepairman(id, type, body, date, room, activity, imagePaths));
        }

        return news;
    }
}