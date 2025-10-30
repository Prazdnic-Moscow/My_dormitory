package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class avitoStanActivity extends AppCompatActivity
{
    Button give, take, lost;
    ImageButton menuButton, addAvitoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avitostan);
        give = findViewById(R.id.give);
        take = findViewById(R.id.take);
        lost = findViewById(R.id.lost);
        menuButton = findViewById(R.id.menuButton);
        addAvitoButton = findViewById(R.id.addAvitoButton);

        give.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(avitoStanActivity.this, "Отдать открыли!",Toast.LENGTH_SHORT).show();
            }
        });

        take.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(avitoStanActivity.this, "Брать открыли!",Toast.LENGTH_SHORT).show();
            }
        });

        lost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(avitoStanActivity.this, "Потеряшки открыли!",Toast.LENGTH_SHORT).show();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(avitoStanActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        addAvitoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(avitoStanActivity.this, "Добавление открыли!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
