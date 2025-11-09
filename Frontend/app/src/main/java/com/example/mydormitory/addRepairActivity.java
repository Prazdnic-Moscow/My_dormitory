package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class addRepairActivity extends AppCompatActivity
{
    Button publishButton;
    ImageButton backToRepairButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_repair);
        publishButton = findViewById(R.id.publishButton);
        backToRepairButton = findViewById(R.id.backToRepairButton);


        backToRepairButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (addRepairActivity.this, repairActivity.class);
                startActivity(intent);
            }
        });
    }
}