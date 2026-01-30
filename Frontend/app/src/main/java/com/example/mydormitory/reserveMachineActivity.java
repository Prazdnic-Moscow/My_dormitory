package com.example.mydormitory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class reserveMachineActivity extends AppCompatActivity {

    ImageButton menuButton, addWashMachineButton;
    Spinner machines_categories_view;
    private List<Machine> machines = new ArrayList<>();
    private List<Button> dateButtonsList = new ArrayList<>();
    private Button selectedDateButton = null;
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

        machines_categories_view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && machines.size() >= position) {
                    // Получаем выбранную машину
                    Machine selectedMachine = machines.get(position - 1);
                    Toast.makeText(reserveMachineActivity.this, "Выбрана: " + selectedMachine.getName(), Toast.LENGTH_SHORT).show();
                    showDatesForSelectedMachine();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не выбрано
            }
        });


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

    private void showDatesForSelectedMachine() {
        LinearLayout dateContainer = findViewById(R.id.dateContainer);
        dateContainer.removeAllViews();
        dateButtonsList.clear(); // Очищаем список
        selectedDateButton = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM\nEEE", new Locale("ru", "RU"));
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            Button button = new Button(this);
            button.setText(sdf.format(calendar.getTime()));
            button.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            button.setMinimumWidth(dpToPx(80));

            // Устанавливаем исходные цвета
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setTextColor(Color.BLACK);

            final String date = sdf.format(calendar.getTime());

            button.setOnClickListener(v -> {
                // Если уже была выбрана кнопка, сбрасываем её
                if (selectedDateButton != null && selectedDateButton != button) {
                    selectedDateButton.setBackgroundColor(Color.TRANSPARENT);
                    selectedDateButton.setTextColor(Color.BLACK);
                }

                // Устанавливаем новый выбор
                button.setBackgroundColor(Color.BLUE);
                button.setTextColor(Color.WHITE);
                selectedDateButton = button;
                Toast.makeText(this, "Выбрана дата: " + date, Toast.LENGTH_SHORT).show();
            });

            dateContainer.addView(button);
            dateButtonsList.add(button);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void loadMachinesFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String response = sendGetRequest(accessToken, refreshToken);
                    if (response != null) {
                        final List<Machine> loadedMachines = parseMachines(response);

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

    private List<Machine> parseMachines(String jsonResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, new TypeReference<>() {});
    }

    private void updateSpinnerWithMachines(List<Machine> machines) {
        List<String> machineNames = new ArrayList<>();
        machineNames.add("Выберите стиральную машину");

        for (Machine m : machines) {
            machineNames.add(m.getName());
        }
        spinnerAdapter.clear();
        spinnerAdapter.addAll(machineNames);
        spinnerAdapter.notifyDataSetChanged();
    }
}