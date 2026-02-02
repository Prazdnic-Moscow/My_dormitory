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

public class MyRepairsActivity extends AppCompatActivity implements NewsForRepairManAdapter.OnRepairButtonClickListener
{
    private RecyclerView recyclerView;
    private NewsForRepairManAdapter adapter;
    private ImageButton allWidjetForRepairBtn;
    private List<newsforrepairman> repairs = new ArrayList<>();

    private static final String MY_REPAIRS_URL = "http://10.0.2.2:3000/myrepair";
    private static final String UPDATE_STATUS_URL = "http://10.0.2.2:3000/activaterepair";
    private String accessToken;
    private String refreshToken;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mynewsforrepairman);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        accessToken = prefs.getString("access_token", null);
        refreshToken = prefs.getString("refresh_token", null);
        currentUserId = utils.getUserIdFromToken(this, accessToken, refreshToken);

        if (accessToken == null) {
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }
        allWidjetForRepairBtn = findViewById(R.id.allWidjetForRepairBtn);
        recyclerView = findViewById(R.id.newsListForRepairman);
        adapter = new NewsForRepairManAdapter(repairs);
        adapter.setOnRepairButtonClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        allWidjetForRepairBtn.setOnClickListener(v -> {
            Intent intent = new Intent (MyRepairsActivity.this, allWidjetForRepairman.class);
            startActivity(intent);
            finish();
        });
        loadMyRepairs();
    }

    @Override
    public void onRepairButtonClick(int position, newsforrepairman news) {
        cancelRepair(news.getId(), position);
    }

    private void cancelRepair(int repairId, int position) {
        final int finalPosition = position;
        new Thread(() -> {
            try {
                sendPatchRequest(repairId, false, currentUserId);

                runOnUiThread(() -> {
                    if (finalPosition >= 0 && finalPosition < repairs.size()) {
                        repairs.remove(finalPosition);
                        adapter.notifyItemRemoved(finalPosition);
                        Toast.makeText(this, "Заказ отменён", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        loadMyRepairs();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Ошибка отмены", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

private void loadMyRepairs() {
    new Thread(() -> {
        try {
            String response = sendGetRequest();
            JSONArray array = new JSONArray(response);
            List<newsforrepairman> list = utils.parseNewsFromJson(array);

            List<newsforrepairman> myRepairs = new ArrayList<>();

            for (newsforrepairman n : list) {
                if (n.getActivity() && n.getRepairmanId() == currentUserId) {
                    myRepairs.add(n);
                }
            }

            runOnUiThread(() -> {
                repairs.clear();
                repairs.addAll(myRepairs);
                adapter.notifyDataSetChanged();
            });

        } catch (Exception e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show());
        }
    }).start();
}

private String sendGetRequest() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(MY_REPAIRS_URL).openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        if (conn.getResponseCode() == 401)
        {
            if(utils.refreshAccessToken(MyRepairsActivity.this, refreshToken)){
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);
                MyRepairsActivity.this.accessToken = newAccess;
                MyRepairsActivity.this.refreshToken = newRefresh;
                return sendGetRequest();
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
                    Toast.makeText(MyRepairsActivity.this, "Сессия истекла. Войдите снова", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MyRepairsActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    private void sendPatchRequest(int repairId, boolean activity, int repairManId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("repair_id", repairId);
        json.put("activity", activity);
        json.put("user_id", repairManId);

        HttpURLConnection conn = (HttpURLConnection) new URL(UPDATE_STATUS_URL).openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        writer.write(json.toString());
        writer.close();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed");
        }
    }
}