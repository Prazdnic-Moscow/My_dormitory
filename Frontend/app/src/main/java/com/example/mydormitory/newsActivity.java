package com.example.mydormitory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        String refreshToken = prefs.getString("refresh_token", null);

        if (accessToken != null)
        {
            // Можно использовать токен в запросах
            Log.d("TOKEN", "Access: " + accessToken);
        }
        else
        {
            // Пользователь не авторизован
            startActivity(new Intent(this, loginActivity.class));
            finish();
        }

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