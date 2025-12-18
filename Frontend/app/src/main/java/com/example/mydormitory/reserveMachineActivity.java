package com.example.mydormitory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydormitory.model.machine;
import com.example.mydormitory.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class reserveMachineActivity extends AppCompatActivity {

    ImageButton menuButton, addWashMachineButton;
    Spinner machines_categories_view;
    private List<machine> machines = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private String accessToken;
    private String refreshToken;
    private static final String API_URL = "http://10.0.2.2:3000/washmachine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_machine);

        // Получаем токены из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        refreshToken = prefs.getString("refresh_token", null);

        // Проверяем авторизацию
        if (accessToken == null) {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }

        menuButton = findViewById(R.id.menuButton);
        addWashMachineButton = findViewById(R.id.addWashMachineButton);
        machines_categories_view = findViewById(R.id.machines_categories_view);

        spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        machines_categories_view.setAdapter(spinnerAdapter);
        // Загружаем данные с сервера
        loadMachinesFromServer();

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reserveMachineActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        addWashMachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reserveMachineActivity.this, addWashMachineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadMachinesFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String response = sendGetRequest(accessToken, refreshToken);
                    if (response != null) {
                        final List<machine> loadedMachines = parseMachines(response);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Сохраняем в наш массив
                                machines.clear();
                                machines.addAll(loadedMachines);

                                // Обновляем спиннер
                                updateSpinnerWithMachines(loadedMachines);

                                Toast.makeText(reserveMachineActivity.this,
                                        "Загружено машин: " + loadedMachines.size(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(reserveMachineActivity.this,
                                    "Ошибка загрузки: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private String sendGetRequest(String accessToken, String refreshToken) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // Токен устарел → делаем запрос на бек /refresh
            connection.disconnect();
            if (utils.refreshAccessToken(reserveMachineActivity.this, refreshToken))
            {
                // Читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);

                // Обновляем переменные класса
                reserveMachineActivity.this.accessToken = newAccess;
                reserveMachineActivity.this.refreshToken = newRefresh;

                // Повторяем исходный запрос с новым токеном
                return sendGetRequest(newAccess, newRefresh);
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("access_token");
                        editor.remove("refresh_token");
                        editor.apply();
                        Toast.makeText(reserveMachineActivity.this,
                                "Сессия истекла. Войдите снова", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(reserveMachineActivity.this, loginActivity.class);
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

    private List<machine> parseMachines(String jsonResponse) throws JSONException {
        List<machine> machineList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            int id = obj.getInt("id");
            String name = obj.getString("name");

            machineList.add(new machine(id, name));
        }

        return machineList;
    }

    private void updateSpinnerWithMachines(List<machine> machines) {
        List<String> machineNames = new ArrayList<>();
        machineNames.add("Выберите стиральную машину");

        for (machine m : machines) {
            machineNames.add(m.getName());
        }
        spinnerAdapter.clear();
        spinnerAdapter.addAll(machineNames);
        spinnerAdapter.notifyDataSetChanged();
    }

    // Метод для удаления машины
    public void deleteMachineById(int machineId) {
        for (int i = 0; i < machines.size(); i++) {
            if (machines.get(i).getId() == machineId) {
                machines.remove(i);
                updateSpinnerWithMachines(machines);
                break;
            }
        }
    }
}