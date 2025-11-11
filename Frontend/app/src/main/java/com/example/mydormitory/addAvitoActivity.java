package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class addAvitoActivity extends AppCompatActivity
{
    Button publishButton;
    ImageButton backToAvitoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_avito);
        publishButton = findViewById(R.id.publishButton);
        backToAvitoButton = findViewById(R.id.backToAvitoButton);


        backToAvitoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (addAvitoActivity.this, avitoStanActivity.class);
                startActivity(intent);
            }
        });
    }
}