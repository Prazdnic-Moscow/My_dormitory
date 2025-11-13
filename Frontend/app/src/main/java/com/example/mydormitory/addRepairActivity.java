package com.example.mydormitory;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class addRepairActivity extends AppCompatActivity
{
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Uri> selectedImages = new ArrayList<>();
    Button publishButton;
    ImageButton backToRepairButton;
    EditText detailsEditTextRepair, roomsEditTextRepair;
    LinearLayout addPhotoLinearLayout, imagesContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        String repairType = getIntent().getStringExtra("repair_type");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_repair);
        publishButton = findViewById(R.id.publishButton);
        backToRepairButton = findViewById(R.id.backToRepairButton);
        detailsEditTextRepair = findViewById(R.id.detailsEditTextRepair);
        roomsEditTextRepair = findViewById(R.id.roomsEditTextRepair);
        addPhotoLinearLayout = findViewById(R.id.addPhotoLinearLayout);
        imagesContainer = findViewById(R.id.imagesContainer);
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

        backToRepairButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (addRepairActivity.this, repairActivity.class);
                startActivity(intent);
            }
        });

        addPhotoLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String details = detailsEditTextRepair.getText().toString();
                String room = roomsEditTextRepair.getText().toString();

                if(details.isEmpty() || room.isEmpty())
                {
                    Toast.makeText(addRepairActivity.this, "Нужно заполнить все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                int rooms = Integer.parseInt(room);

                // Запускаем в отдельном потоке чтобы не блокировать UI
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            // 1. Отправляем фото и получаем их пути
                            List<String> photoPaths = new ArrayList<>();
                            for (Uri imageUri : selectedImages)
                            {
                                String path = uploadPhoto(imageUri);
                                photoPaths.add(path);
                            }

                            // 2. Отправляем данные о ремонте
                            sendRepairData(details, rooms, photoPaths, repairType, accessToken, refreshToken);

                            // Показываем успех
                            runOnUiThread(() -> {
                                Toast.makeText(addRepairActivity.this, "Успешно отправлено!", Toast.LENGTH_SHORT).show();
                                finish();
                            });

                        }
                        catch (Exception e)
                        {
                            runOnUiThread(() -> {
                                Toast.makeText(addRepairActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            if (data.getClipData() != null)
            {
                int count = data.getClipData().getItemCount();
                int photosToAdd = Math.min(count, 3 - selectedImages.size());

                for (int i = 0; i < photosToAdd; i++)
                {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageUri);
                    addImageToContainer(imageUri);
                }
            }
            else if (data.getData() != null && selectedImages.size() < 3)
            {
                // Выбрано одно фото, но только если у нас меньше 3
                Uri imageUri = data.getData();
                selectedImages.add(imageUri);
                addImageToContainer(imageUri);
            }
        }
    }

    private void addImageToContainer(Uri imageUri)
    {
        RelativeLayout imageLayout = new RelativeLayout(this);
        imageLayout.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));
        imageLayout.setPadding(8, 8, 8, 8);

        // Само фото
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(250, 250));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);

        ImageButton deleteButton = new ImageButton(this);
        RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(60, 60);
        deleteParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(android.R.drawable.ic_delete);
        deleteButton.setPadding(8, 8, 8, 8);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                imagesContainer.removeView(imageLayout);
                selectedImages.remove(imageUri);
            }
        });

        imageLayout.addView(imageView);
        imageLayout.addView(deleteButton);
        imagesContainer.addView(imageLayout);
    }

    private String uploadPhoto(Uri imageUri) throws Exception
    {
        String uploadUrl = "http://10.0.2.2:3000/file/repair";
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(imageUri);

        // Определяем MIME
        String mimeType = resolver.getType(imageUri);
        if (mimeType == null) mimeType = "image/jpeg";

        HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", mimeType);

        // Отправляем просто бинарные данные
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();

        // Читаем ответ
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_OK)
        {
            throw new Exception("Ошибка загрузки фото: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("file_path");
    }

    private void sendRepairData(String details,
                                int rooms, List<String> photos,
                                String repairType,
                                String accessToken,
                                String refreshToken) throws Exception
    {
        String apiUrl = "http://10.0.2.2:3000/repair";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("body", details);
        jsonBody.put("room", rooms);
        jsonBody.put("type", repairType);
        jsonBody.put("repair_paths", new JSONArray(photos));

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
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
            if (refreshAccessToken(refreshToken))
            {
                // читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);

                // повторяем исходный запрос с новым токеном
                sendRepairData(details, rooms, photos, repairType, newAccess, newRefresh);
                return;
            }
            else
            {
                throw new Exception("Токен устарел и обновить не удалось");
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

    private boolean refreshAccessToken(String refreshToken)
    {
        try
        {
            String refreshUrl = "http://10.0.2.2:3000/refresh";

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("refresh_token", refreshToken);

            HttpURLConnection conn = (HttpURLConnection) new URL(refreshUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8")))
            {
                writer.write(jsonBody.toString());
            }

            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK)
            {
                String response = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))
                        .lines().reduce("", (acc, line) -> acc + line);

                JSONObject json = new JSONObject(response);
                String newAccess = json.getString("access_token");
                String newRefresh = json.getString("refresh_token");

                // сохраняем новые токены
                getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("access_token", newAccess)
                        .putString("refresh_token", newRefresh)
                        .apply();

                conn.disconnect();
                return true;
            }

            conn.disconnect();
            return false;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
