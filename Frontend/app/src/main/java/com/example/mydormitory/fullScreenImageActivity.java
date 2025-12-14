package com.example.mydormitory;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class fullScreenImageActivity extends AppCompatActivity {

    private ImageView fullScreenImage;
    private List<String> imagePaths;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullScreenImage = findViewById(R.id.fullScreenImage);

        // Получаем ОДИН путь к изображению
        String imagePath = getIntent().getStringExtra("image_path");

        if (imagePath == null) {
            finish();
            return;
        }

        // Загружаем одно изображение
        loadImage(imagePath);

        fullScreenImage.setOnClickListener(v -> finish());
    }

    private void loadImage(String imagePath) {
        new Thread(() -> {
            try {
                Bitmap bitmap = utils.downloadImageFromServer(imagePath);
                if (bitmap != null) {
                    fullScreenImage.post(() -> {
                        fullScreenImage.setImageBitmap(bitmap);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}