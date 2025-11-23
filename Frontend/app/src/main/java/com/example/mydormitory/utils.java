package com.example.mydormitory;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class utils {
    public static String uploadFileToServer(Context context, Uri fileUri, String folder, String fileType) throws Exception {
        String uploadUrl = "http://10.0.2.2:3000/file/" + folder;
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream = resolver.openInputStream(fileUri);

        // Определяем MIME тип
        String mimeType = resolver.getType(fileUri);
        if (mimeType == null) {
            // Устанавливаем MIME тип по умолчанию в зависимости от типа файла
            if ("photo".equals(fileType)) {
                mimeType = "image/jpeg";
            } else {
                mimeType = "application/octet-stream";
            }
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", mimeType);

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            throw new Exception("Ошибка загрузки файла: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("file_path");
    }

    public static Bitmap downloadImageFromServer(String imagePath) throws Exception {
        String downloadUrl = "http://10.0.2.2:3000/file" + imagePath;
        Log.d("IMAGE_DEBUG", "🔄 Loading image: " + downloadUrl);

        HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
        connection.setRequestMethod("GET");

        try {
            int responseCode = connection.getResponseCode();
            Log.d("IMAGE_DEBUG", "📊 Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();

                // Читаем все данные в массив байтов
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                byte[] imageData = byteArrayOutputStream.toByteArray();
                Log.d("IMAGE_DEBUG", "📦 Received " + imageData.length + " bytes");

                // Пробуем создать Bitmap из байтов
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                inputStream.close();
                byteArrayOutputStream.close();

                if (bitmap == null) {
                    Log.e("IMAGE_DEBUG", "❌ BitmapFactory returned null - invalid image data");
                    throw new Exception("Invalid image data received");
                }

                Log.d("IMAGE_DEBUG", "✅ Successfully created bitmap: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                return bitmap;
            } else {
                throw new Exception("HTTP error: " + responseCode);
            }
        } catch (Exception e) {
            Log.e("IMAGE_DEBUG", "💥 Exception: " + e.getMessage());
            throw e;
        } finally {
            connection.disconnect();
        }
    }


    public static boolean refreshAccessToken(Context context, String refreshToken)
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

                // сохраняем новые токены - используем переданный context
                SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                prefs.edit()
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