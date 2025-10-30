package com.example.mydormitory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class registerActivity extends AppCompatActivity
{
    private EditText registrationLogin, registrationPassword, registrationName, registrationLastName, registrationSurname;
    private Button buttonBack, registrationButtonSelectDocument, buttonRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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


        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Переход на активность входа
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registrationButtonSelectDocument.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(registerActivity.this, "Выбор документа", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String login = registrationLogin.getText().toString();
                String password = registrationPassword.getText().toString();
                String name = registrationName.getText().toString();
                String lastName = registrationLastName.getText().toString();
                String surname = registrationSurname.getText().toString();
                if (login.isEmpty() || password.isEmpty() || name.isEmpty() || lastName.isEmpty() || surname.isEmpty())
                {
                    return;
                }
                // Успешная регистрация
                Toast.makeText(registerActivity.this, "Регистрация успешна!\n" + "Логин: " + login + "\nПароль" + password, Toast.LENGTH_LONG).show();

                // Переход на главный экран
                Intent intent = new Intent(registerActivity.this, newsActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
