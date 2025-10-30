package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class documentsActivity extends AppCompatActivity
{
    ImageButton menuButton, addDocumentButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents);
        menuButton = findViewById(R.id.menuButton);
        addDocumentButton = findViewById(R.id.addDocumentButton);

        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (documentsActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        addDocumentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(documentsActivity.this, "Добавление документа!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}