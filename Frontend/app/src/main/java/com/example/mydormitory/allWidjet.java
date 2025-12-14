package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class allWidjet extends AppCompatActivity
{
    private ImageButton openNewsButton, openDocumentButton, openMachineButton, openAvitostanButton, openGuideButton, openRepairButton, exitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_widjet);
        openNewsButton = findViewById(R.id.openNewsButton);
        openDocumentButton = findViewById(R.id.openDocumentButton);
        openMachineButton = findViewById(R.id.openMachineButton);
        openAvitostanButton = findViewById(R.id.openAvitostanButton);
        openGuideButton = findViewById(R.id.openGuideButton);
        openRepairButton = findViewById(R.id.openRepairButton);
        exitButton = findViewById(R.id.exitButton);

        openNewsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, newsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, loginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        openDocumentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, documentsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        openMachineButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, reserveMachineActivity.class);
                startActivity(intent);
                finish();
            }
        });

        openAvitostanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, avitostanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        openGuideButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, guideActivity.class);
                startActivity(intent);
                finish();
            }
        });

        openRepairButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (allWidjet.this, repairActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
