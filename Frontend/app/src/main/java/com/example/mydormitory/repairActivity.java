package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class repairActivity extends AppCompatActivity
{
    ImageButton menuButton;
    LinearLayout electricianLayout, carpenterLayout, plumberLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair);
        menuButton = findViewById(R.id.menuButton);
        electricianLayout = findViewById(R.id.electricianLayout);
        carpenterLayout = findViewById(R.id.carpenterLayout);
        plumberLayout = findViewById(R.id.plumberLayout);


        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (repairActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        electricianLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (repairActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        carpenterLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (repairActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        plumberLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (repairActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });
    }
}