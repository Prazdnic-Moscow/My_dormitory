package com.example.mydormitory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class registerActivity extends loginActivity
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
            }
        });

        registrationButtonSelectDocument.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });



    }
}
