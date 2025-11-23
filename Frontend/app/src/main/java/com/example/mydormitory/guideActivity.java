package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class guideActivity extends AppCompatActivity
{
    ImageButton menuButton, addGuideButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        menuButton = findViewById(R.id.menuButton);
        addGuideButton = findViewById(R.id.addGuideButton);

        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (guideActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        addGuideButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (guideActivity.this, addGuideActivity.class);
                startActivity(intent);
            }
        });


    }
}
