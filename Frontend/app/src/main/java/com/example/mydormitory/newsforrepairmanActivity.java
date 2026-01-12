package com.example.mydormitory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class newsforrepairmanActivity extends AppCompatActivity
{
    private RecyclerView newsRecyclerView;
    private newsforrepairmanAdapter newsAdapter;
    private List<newsforrepairman> newsList = new ArrayList<>();
    private static final String API_URL = "http://10.0.2.2:3000/news/";
    private static final int NEWS_LIMIT = 50; // лимит новостей
    private String accessToken;
    private String refreshToken;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsforrepairman);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        refreshToken = prefs.getString("refresh_token", null);
        userType = prefs.getString("type", null);

        if (accessToken == null)
        {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            // Пользователь не авторизован
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }

        newsRecyclerView = findViewById(R.id.newsListForRepairman);

        // Настройка RecyclerView
        newsAdapter = new newsforrepairmanAdapter(newsList);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        // Загрузка данных с API
        loadNewsFromApi();
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