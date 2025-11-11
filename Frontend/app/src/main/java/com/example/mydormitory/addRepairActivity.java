package com.example.mydormitory;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
                            sendRepairData(details, rooms, photoPaths, repairType);

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

    // Простой метод загрузки фото
    // Метод загрузки фото на сервер
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

    // Метод отправки данных о ремонте
    private void sendRepairData(String details, int rooms, List<String> photos, String repairType) throws Exception
    {
        String apiUrl = "http://10.0.2.2:3000/repair";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6ImFjY2VzcyJ9.eyJJZCI6NywiZXhwIjoxNzYyODkwNzAzLCJyb2xlcyI6WyJuZXdzX3JlYWQiLCJ1c2VyX3JlYWQiLCJ0dXRvcl9yZWFkIiwiZmlsZV9yZWFkIiwid2FzaF9tYWNoaW5lX3JlYWQiXSwic3ViIjoiODkxMzA0NzU5MDkifQ.shTwiBB8hTgXrsvcqW3MgkkbRHViZJMEXOQqc3M60cY"; // ← сюда реальный JWT

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("body", details);
        jsonBody.put("room", rooms);
        jsonBody.put("type", repairType);
        JSONArray jsonPhotos = new JSONArray(photos);
        jsonBody.put("repair_paths", jsonPhotos);

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setDoOutput(true);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        writer.write(jsonBody.toString());
        writer.flush();
        writer.close();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_OK)
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
