package com.example.mydormitory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class loginActivity extends AppCompatActivity
{
    private EditText login, password;
    private Button buttonForgotPassword, buttonLogin, buttonRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // ВАЖНО: Инициализация ДО использования!
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegistration = findViewById(R.id.buttonRegistration);

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userLogin = login.getText().toString();
                String userPassword = password.getText().toString();
                // Простая проверка и переход
                if (userLogin.isEmpty() || userPassword.isEmpty())
                {
                    Toast.makeText(loginActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Переход на главный экран
                    Intent intent = new Intent(loginActivity.this, newsActivity.class);
                    startActivity(intent);
                    finish(); // Закрыть экран входа
                }
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(loginActivity.this, "Восстановление пароля", Toast.LENGTH_LONG).show();
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Переход на активность регистрации
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });
    }
}