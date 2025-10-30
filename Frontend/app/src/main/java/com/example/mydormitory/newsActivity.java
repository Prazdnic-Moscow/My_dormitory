package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class newsActivity extends AppCompatActivity
{
    ImageButton menuButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        menuButton = findViewById(R.id.menuButton);

        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (newsActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });


    }
}