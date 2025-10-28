package com.example.mydormitory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextPhone;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextMiddleName;
    private Button buttonSelectDocument;
    private Button buttonRegister;
    private TextView textViewSelectedFile;
    private TextView textViewBackToLogin;
    
    private Uri selectedPdfUri;
    private static final int PICK_PDF_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация views
        initViews();
        
        // Установка обработчиков событий
        setupClickListeners();
        
        // Настройка форматирования номера телефона
        setupPhoneFormatting();
    }

    private void initViews() {
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextMiddleName = findViewById(R.id.editTextMiddleName);
        buttonSelectDocument = findViewById(R.id.buttonSelectDocument);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewSelectedFile = findViewById(R.id.textViewSelectedFile);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);
    }

    private void setupClickListeners() {
        buttonSelectDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdfDocument();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        textViewBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Возвращаемся к странице входа
            }
        });
    }

    private void setupPhoneFormatting() {
        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString();
                
                // Убираем все символы кроме цифр
                String digitsOnly = phone.replaceAll("[^\\d]", "");
                
                // Если номер не начинается с 7, добавляем +7
                if (!phone.startsWith("+7") && !phone.startsWith("7")) {
                    if (digitsOnly.length() > 0) {
                        String formatted = "+7" + digitsOnly;
                        if (!formatted.equals(phone)) {
                            editTextPhone.setText(formatted);
                            editTextPhone.setSelection(formatted.length());
                        }
                    }
                } else if (phone.startsWith("7") && !phone.startsWith("+7")) {
                    // Если начинается с 7, но без +
                    String formatted = "+" + phone;
                    if (!formatted.equals(phone)) {
                        editTextPhone.setText(formatted);
                        editTextPhone.setSelection(formatted.length());
                    }
                }
                
                // Ограничиваем длину до 12 символов (+7XXXXXXXXXX)
                if (phone.length() > 12) {
                    String truncated = phone.substring(0, 12);
                    if (!truncated.equals(phone)) {
                        editTextPhone.setText(truncated);
                        editTextPhone.setSelection(truncated.length());
                    }
                }
            }
        });
    }

    private void selectPdfDocument() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        try {
            startActivityForResult(Intent.createChooser(intent, "Выберите PDF документ"), PICK_PDF_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Установите файловый менеджер", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedPdfUri = data.getData();
            String fileName = getFileName(selectedPdfUri);
            textViewSelectedFile.setText("Выбран файл: " + fileName);
            textViewSelectedFile.setVisibility(View.VISIBLE);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void performRegistration() {
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String middleName = editTextMiddleName.getText().toString().trim();

        // Валидация полей
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Введите номер телефона");
            editTextPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Введите пароль");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("Введите имя");
            editTextFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Введите фамилию");
            editTextLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(middleName)) {
            editTextMiddleName.setError("Введите отчество");
            editTextMiddleName.requestFocus();
            return;
        }

        // Проверка формата российского номера телефона (+7XXXXXXXXXX)
        if (!phone.matches("^\\+7\\d{10}$")) {
            editTextPhone.setError("Введите корректный номер телефона (+7XXXXXXXXXX)");
            editTextPhone.requestFocus();
            return;
        }

        // Проверка длины пароля
        if (password.length() < 6) {
            editTextPassword.setError("Пароль должен содержать минимум 6 символов");
            editTextPassword.requestFocus();
            return;
        }

        // Проверка наличия документа
//        if (selectedPdfUri == null) {
//            Toast.makeText(this, "Выберите PDF документ", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Создание объекта пользователя
        User user = new User(phone, password, firstName, lastName, middleName, selectedPdfUri);

        // Отправка данных на сервер (закомментировано)
        sendRegistrationDataToServer(user);
    }

    private void sendRegistrationDataToServer(User user) {
        // TODO: ЗАПРОС НА БЭКЕНД - Регистрация пользователя
        // URL: http://10.0.2.2:3000/register
        // Метод: POST
        // Headers: Content-Type: application/json
        // Body: {
        //   "phone_number": user.getPhone(),
        //   "password": user.getPassword(),
        //   "first_name": user.getFirstName(),
        //   "last_name": user.getLastName(), 
        //   "middle_name": user.getMiddleName(),
        //   "document": "base64_encoded_pdf_file"
        // }
        
        // ЗАКОММЕНТИРОВАНО: Запрос регистрации на бэкенд
        // ApiService apiService = new ApiService();
        // apiService.register(user, new ApiService.ApiCallback() {
        //     @Override
        //     public void onSuccess(String response) {
        //         Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
        //         Intent intent = new Intent(this, MainActivity.class);
        //         startActivity(intent);
        //         finish();
        //     }
        //     @Override
        //     public void onError(String error) {
        //         Toast.makeText(this, "Ошибка регистрации: " + error, Toast.LENGTH_SHORT).show();
        //     }
        // });
        
        // Пока что просто показываем сообщение об успешной регистрации
        Toast.makeText(this, "Регистрация успешно завершена", Toast.LENGTH_LONG).show();
        
        // Переходим на главную страницу (новости)
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Закрываем RegisterActivity
    }
}
