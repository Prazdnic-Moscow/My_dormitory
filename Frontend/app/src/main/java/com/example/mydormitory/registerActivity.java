package com.example.mydormitory;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class registerActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 2;
    private List<Uri> selectedFiles = new ArrayList<>();
    private LinearLayout filesContainer;
    private EditText registrationLogin, registrationPassword, registrationName, registrationLastName, registrationSurname;
    private Button buttonBack, registrationButtonSelectDocument, buttonRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        registrationLogin = findViewById(R.id.registrationLogin);
        registrationPassword = findViewById(R.id.registrationPassword);
        registrationName = findViewById(R.id.registrationName);
        registrationLastName = findViewById(R.id.registrationLastName);
        registrationSurname = findViewById(R.id.registrationSurname);
        buttonBack = findViewById(R.id.buttonBack);
        registrationButtonSelectDocument = findViewById(R.id.registrationButtonSelectDocument);
        buttonRegistration = findViewById(R.id.buttonRegistration);
        filesContainer = findViewById(R.id.filesContainer);

        // Кнопка назад
        buttonBack.setOnClickListener(v ->
        {
            startActivity(new Intent(registerActivity.this, loginActivity.class));
            finish();
        });

        // Кнопка выбора файлов
        registrationButtonSelectDocument.setOnClickListener(v ->
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        // Кнопка регистрации
        buttonRegistration.setOnClickListener(v ->
        {
            // Проверка всех полей прямо здесь
            String login = registrationLogin.getText().toString();
            String password = registrationPassword.getText().toString();
            String name = registrationName.getText().toString();
            String lastName = registrationLastName.getText().toString();
            String surname = registrationSurname.getText().toString();

            if (login.isEmpty() || password.isEmpty() || name.isEmpty() || lastName.isEmpty() || surname.isEmpty()) {
                Toast.makeText(registerActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedFiles.isEmpty()) {
                Toast.makeText(registerActivity.this, "Выберите хотя бы один файл!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Загружаем файлы на /file/user и получаем пути
                        List<String> uploadedPaths = new ArrayList<>();
                        for (Uri fileUri : selectedFiles) {
                            String filePath = uploadFileToServer(fileUri);
                            uploadedPaths.add(filePath);
                        }

                        // Отправляем JSON с регистрацией на /registration
                        sendRegistration(login, password, name, lastName, surname, uploadedPaths);

                        runOnUiThread(() -> {
                            Toast.makeText(registerActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(registerActivity.this, loginActivity.class));
                            finish();
                        });
                    } catch (final Exception e) {
                        runOnUiThread(() -> Toast.makeText(registerActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            }).start();
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
                    Toast.makeText(registerActivity.this, "Можно выбрать только 3 файла", Toast.LENGTH_SHORT).show();
                }
            } else if (data.getData() != null && selectedFiles.size() < 3) {
                handleFileSelection(data.getData());
            }
        }
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
            filesContainer.removeView(fileLayout);
            selectedFiles.remove(fileUri);
        });

        // Добавляем элементы в row
        fileLayout.addView(fileNameView);
        fileLayout.addView(deleteButton);

        // Добавляем row в контейнер
        filesContainer.addView(fileLayout);
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

    private String uploadFileToServer(Uri fileUri) throws Exception {
        String uploadUrl = "http://10.0.2.2:3000/file/user";
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(fileUri);

        String mimeType = resolver.getType(fileUri);
        if (mimeType == null) mimeType = "application/octet-stream";

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
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("file_path");
    }

    private void sendRegistration(String login, String password, String name, String lastName, String surname, List<String> filePaths) throws Exception {
        String registerUrl = "http://10.0.2.2:3000/register";

        JSONObject json = new JSONObject();
        json.put("phone_number", login);
        json.put("password", password);
        json.put("name", name);
        json.put("last_name", lastName);
        json.put("surname", surname);

        JSONArray filesArray = new JSONArray();
        for (String path : filePaths) filesArray.put(path);
        json.put("document_path", filesArray);

        HttpURLConnection connection = (HttpURLConnection) new URL(registerUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(json.toString().getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            throw new Exception("Ошибка регистрации: " + responseCode);
        }
    }
}
