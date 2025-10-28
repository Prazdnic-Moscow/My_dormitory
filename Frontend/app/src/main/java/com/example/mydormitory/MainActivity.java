package com.example.mydormitory;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerViewNews;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            
            Toast.makeText(this, "MainActivity запущена", Toast.LENGTH_SHORT).show();
            
            // Настройка Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Мое Общежитие");
            }
            
            // Настройка бокового меню
            setupNavigationDrawer();
            
            // Настройка списка новостей
            setupNewsList();
            
            // Загрузка тестовых данных
            loadSampleNews();
            
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при запуске: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupNavigationDrawer() {
        try {
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            
            // Используем toolbar для ActionBarDrawerToggle
            Toolbar toolbar = findViewById(R.id.toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, 
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            
            navigationView.setNavigationItemSelectedListener(this);
            
            // Устанавливаем "Новости" как активный пункт по умолчанию
            navigationView.setCheckedItem(R.id.nav_news);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка настройки меню: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupNewsList() {
        try {
            recyclerViewNews = findViewById(R.id.recyclerViewNews);
            recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));
            
            newsAdapter = new NewsAdapter(new ArrayList<>());
            recyclerViewNews.setAdapter(newsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка настройки списка новостей: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadSampleNews() {
        try {
            List<News> newsList = new ArrayList<>();
            
            // TODO: ЗАПРОС НА БЭКЕНД - Получить новости из БД
            // URL: http://10.0.2.2:3000/news
            // Метод: GET
            // Headers: Authorization: Bearer {token}
            // Response: [{"id": "1", "title": "...", "description": "...", ...}]
            
            // ЗАКОММЕНТИРОВАНО: Запрос новостей с бэка
            // ApiService apiService = new ApiService();
            // apiService.getNews(new ApiService.ApiCallback() {
            //     @Override
            //     public void onSuccess(String response) {
            //         List<News> news = parseNewsFromResponse(response);
            //         if (newsAdapter != null) {
            //             newsAdapter.updateNewsList(news);
            //         }
            //     }
            //     @Override
            //     public void onError(String error) {
            //         Toast.makeText(this, "Ошибка загрузки новостей: " + error, Toast.LENGTH_SHORT).show();
            //     }
            // });
            
            // Тестовые данные новостей
            newsList.add(new News(
                    "1",
                    "15 июня 09:00-17:00",
                    "Отключение горячей воды",
                    "15 июня с 9:00 до 17:00 будет отключена горячая вода в связи с плановыми работами.",
                    "Администрация",
                    System.currentTimeMillis() - 86400000 * 5 // 5 дней назад
            ));
            
            newsList.add(new News(
                    "2",
                    "17 июня 19:00-20:30",
                    "Собрание жильцов",
                    "17 июня в 19:00 состоится общее собрание жильцов общежития в холле 1 этажа.",
                    "Совет общежития",
                    System.currentTimeMillis() - 86400000 * 7 // 7 дней назад
            ));
            
            newsList.add(new News(
                    "3",
                    "с 20 июня 00:00 до 25 июня 00:00",
                    "Ремонт лифтов",
                    "С 20 по 25 июня будет проводиться плановый ремонт лифтов. Просим учитывать это при планировании времени.",
                    "Техническая служба",
                    System.currentTimeMillis() - 86400000 * 10 // 10 дней назад
            ));
            
            if (newsAdapter != null) {
                newsAdapter.updateNewsList(newsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка загрузки новостей: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_news) {
            // Уже на странице новостей
        } else if (id == R.id.nav_documents) {
            Intent intent = new Intent(this, DocumentsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_laundry) {
            Intent intent = new Intent(this, LaundryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_avito) {
            Intent intent = new Intent(this, AvitostanActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_guide) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_repair) {
            Intent intent = new Intent(this, RepairActivity.class);
            startActivity(intent);
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}