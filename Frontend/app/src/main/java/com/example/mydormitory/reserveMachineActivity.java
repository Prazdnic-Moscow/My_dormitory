package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class reserveMachineActivity extends AppCompatActivity
{
    Button machine1, machine2, machine3;
    ImageButton menuButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_machine);
        machine1 = findViewById(R.id.machine1);
        machine2 = findViewById(R.id.machine2);
        machine3 = findViewById(R.id.machine3);
        menuButton = findViewById(R.id.menuButton);

        machine1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (reserveMachineActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        machine2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (reserveMachineActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        machine3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (reserveMachineActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent (reserveMachineActivity.this, allWidjet.class);
                startActivity(intent);
            }
        });
    }
}
