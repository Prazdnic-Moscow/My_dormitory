package com.example.mydormitory;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class addDocumentsActivity extends AppCompatActivity
{
    private static final int PICK_FILE_REQUEST = 3;
    private List<Uri> selectedFiles = new ArrayList<>();
    Button publishDocumentButton;
    ImageButton backToDocumentsButton;
    EditText headersEditTextDocuments;
    LinearLayout addDocumentsLinearLayout, filesContainerForDocuments;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_documents);
        publishDocumentButton = findViewById(R.id.publishDocumentButton);
        backToDocumentsButton = findViewById(R.id.backToDocumentsButton);
        headersEditTextDocuments = findViewById(R.id.headersEditTextDocuments);
        addDocumentsLinearLayout = findViewById(R.id.addDocumentsLinearLayout);
        filesContainerForDocuments = findViewById(R.id.filesContainerForDocuments);

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


        backToDocumentsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (addDocumentsActivity.this, documentsActivity.class);
                startActivity(intent);
            }
        });

        addDocumentsLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_FILE_REQUEST);
            }
        });

        publishDocumentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String header = headersEditTextDocuments.getText().toString();

                if(header.isEmpty())
                {
                    Toast.makeText(addDocumentsActivity.this, "Нужно заполнить все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedFiles.isEmpty()) {
                    Toast.makeText(addDocumentsActivity.this, "Выберите хотя бы один файл!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Запускаем в отдельном потоке чтобы не блокировать UI
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            // Загружаем файлы на /file/user и получаем пути
                            List<String> photoPaths = new ArrayList<>();
                            for (Uri fileUri : selectedFiles) {
                                String filePath = utils.uploadFileToServer( addDocumentsActivity.this, fileUri, "file", "file");
                                photoPaths.add(filePath);
                            }

                            // 2. Отправляем данные о ремонте
                            sendDocumentsData(header, photoPaths, accessToken, refreshToken);

                            // Показываем успех
                            runOnUiThread(() -> {
                                Toast.makeText(addDocumentsActivity.this, "Успешно отправлено!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(addDocumentsActivity.this, documentsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });

                        }
                        catch (Exception e)
                        {
                            runOnUiThread(() -> {
                                Toast.makeText(addDocumentsActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                int filesToAdd = Math.min(count, 3 - selectedFiles.size());
                for (int i = 0; i < filesToAdd; i++) {
                    handleFileSelection(data.getClipData().getItemAt(i).getUri());
                }
                if (count > 3) {
                    Toast.makeText(addDocumentsActivity.this, "Можно выбрать только 3 файла", Toast.LENGTH_SHORT).show();
                }
            } else if (data.getData() != null && selectedFiles.size() < 3) {
                handleFileSelection(data.getData());
            }
        }
    }

    private void sendDocumentsData(String header,
                                   List<String> photos,
                                   String accessToken,
                                   String refreshToken) throws Exception
    {
        String registerUrl = "http://10.0.2.2:3000/file";

        JSONObject json = new JSONObject();
        json.put("body", header);
        JSONArray filesArray = new JSONArray();
        for (String path : photos) filesArray.put(path);
        json.put("files_path", filesArray);

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
            if (utils.refreshAccessToken(addDocumentsActivity.this, refreshToken))
            {
                // читаем новые токены из SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String newAccess = prefs.getString("access_token", null);
                String newRefresh = prefs.getString("refresh_token", null);

                // повторяем исходный запрос с новым токеном
                sendDocumentsData(header, photos, newAccess, newRefresh);
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
                        Toast.makeText(addDocumentsActivity.this, "Сессия истекла. Войдите снова", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(addDocumentsActivity.this, loginActivity.class);
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

    private void handleFileSelection(Uri fileUri) {
        if (selectedFiles.contains(fileUri)) return;
        selectedFiles.add(fileUri);

        LinearLayout fileLayout = new LinearLayout(this);
        fileLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fileLayout.setLayoutParams(layoutParams);
        TextView fileNameView = new TextView(this);
        fileNameView.setText(getFileNameFromUri(fileUri));
        fileNameView.setTextSize(16);
        fileNameView.setTextColor(getResources().getColor(android.R.color.black));
        fileNameView.setSingleLine(true);
        fileNameView.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f);
        textParams.setMargins(8, 0, 0, 0);
        fileNameView.setLayoutParams(textParams);

        // Кнопка удаления
        ImageButton deleteButton = new ImageButton(this);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(80, 80);
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(android.R.drawable.ic_delete);
        deleteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        deleteButton.setOnClickListener(v -> {
            filesContainerForDocuments.removeView(fileLayout);
            selectedFiles.remove(fileUri);
        });

        // Добавляем элементы в row
        fileLayout.addView(fileNameView);
        fileLayout.addView(deleteButton);

        // Добавляем row в контейнер
        filesContainerForDocuments.addView(fileLayout);
    }

    private String getFileNameFromUri(Uri uri) {
        String result = "Файл";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String path = uri.getPath();
            if (path != null && path.contains("/"))
                result = path.substring(path.lastIndexOf("/") + 1);
        }
        return result;
    }
}