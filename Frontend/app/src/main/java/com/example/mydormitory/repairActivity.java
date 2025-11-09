package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class repairActivity extends AppCompatActivity
{
    ImageButton menuButton;
    LinearLayout plumberLayout, carpenterLayout, electricianLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair);
        menuButton = findViewById(R.id.menuButton);
        plumberLayout = findViewById(R.id.plumberLayout);
        carpenterLayout = findViewById(R.id.carpenterLayout);
        electricianLayout = findViewById(R.id.electricianLayout);


        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (repairActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });
    }
    public void onClick(View v)
    {
        Intent intent = new Intent (repairActivity.this, addRepairActivity.class);
        startActivity(intent);
    }
}