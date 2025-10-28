package com.example.mydormitory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AvitostanActivity extends AppCompatActivity implements AnnouncementAdapter.OnAnnouncementClickListener, AddAnnouncementDialog.OnAnnouncementAddedListener {

    private Toolbar toolbar;
    private LinearLayout categoryTabsLayout;
    private RecyclerView announcementsRecyclerView;
    private AnnouncementAdapter announcementAdapter;
    private ImageButton addButton;
    private TextView sectionTitle;

    private Announcement.Category selectedCategory = Announcement.Category.GIVE_AWAY;
    private List<Announcement> allAnnouncements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_avitostan);

            // Настройка Toolbar
            setupToolbar();

            // Настройка компонентов
            setupComponents();

            // Настройка обработчиков
            setupClickListeners();

            // Загрузка тестовых данных
            loadSampleAnnouncements();

            // Обновление отображения
            updateCategoryTabs();
            updateAnnouncements();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при запуске AvitostanActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.avitostan_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupComponents() {
        categoryTabsLayout = findViewById(R.id.categoryTabsLayout);
        announcementsRecyclerView = findViewById(R.id.announcementsRecyclerView);
        addButton = findViewById(R.id.addButton);
        sectionTitle = findViewById(R.id.sectionTitle);

        // Настройка RecyclerView
        announcementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcementAdapter = new AnnouncementAdapter(new ArrayList<>(), this);
        announcementsRecyclerView.setAdapter(announcementAdapter);
    }

    private void setupClickListeners() {
        // Обработчики для табов категорий
        setupCategoryTabs();

        // Обработчик кнопки добавления
        addButton.setOnClickListener(v -> showAddAnnouncementDialog());
    }

    private void setupCategoryTabs() {
        for (int i = 0; i < categoryTabsLayout.getChildCount(); i++) {
            final int categoryIndex = i;
            View tab = categoryTabsLayout.getChildAt(i);
            if (tab instanceof Button) {
                Button button = (Button) tab;
                button.setOnClickListener(v -> selectCategory(categoryIndex));
            }
        }
    }

    private void selectCategory(int categoryIndex) {
        Announcement.Category[] categories = Announcement.Category.values();
        if (categoryIndex < categories.length) {
            selectedCategory = categories[categoryIndex];
            updateCategoryTabs();
            updateAnnouncements();
        }
    }

    private void updateCategoryTabs() {
        Announcement.Category[] categories = Announcement.Category.values();
        
        for (int i = 0; i < categoryTabsLayout.getChildCount() && i < categories.length; i++) {
            View tab = categoryTabsLayout.getChildAt(i);
            if (tab instanceof Button) {
                Button button = (Button) tab;
                Announcement.Category category = categories[i];
                // Активная категория
                button.setBackgroundResource(R.drawable.button);
                button.setBackgroundColor(getResources().getColor(category.getColorResource()));
                button.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void updateAnnouncements() {
        List<Announcement> filteredAnnouncements = new ArrayList<>();
        
        // Фильтруем объявления по выбранной категории
        for (Announcement announcement : allAnnouncements) {
            if (announcement.getCategory() == selectedCategory) {
                filteredAnnouncements.add(announcement);
            }
        }
        
        announcementAdapter.updateAnnouncements(filteredAnnouncements);
    }

    private void loadSampleAnnouncements() {
        // Добавляем тестовые объявления как на фото
        
        // Отдам
        allAnnouncements.add(new Announcement(
            "Отдам книги по программированию",
            "JavaScript, Python, б/у, в хорошем состоянии",
            "Комната 305",
            null,
            Announcement.Category.GIVE_AWAY,
            "Сегодня"
        ));
        
        allAnnouncements.add(new Announcement(
            "Отдам электрический чайник",
            "Работает, но требует чистки",
            "Комната 217",
            null,
            Announcement.Category.GIVE_AWAY,
            "Вчера"
        ));
        
        // Потеряшки
        allAnnouncements.add(new Announcement(
            "Найден ключ от комнаты",
            "Найден в холле 3 этажа. Опознать по брелку",
            "Комната 310",
            null,
            Announcement.Category.LOST_FOUND,
            "Сегодня"
        ));
        
        // Возьму
        allAnnouncements.add(new Announcement(
            "Нужен велосипед на лето",
            "Ищу недорогой велосипед в хорошем состоянии",
            null,
            "@student123",
            Announcement.Category.TAKE,
            "2 дня назад"
        ));
    }

    private void showAddAnnouncementDialog() {
        AddAnnouncementDialog dialog = AddAnnouncementDialog.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        dialog.show(fragmentManager, "AddAnnouncementDialog");
    }

    @Override
    public void onAnnouncementClick(Announcement announcement) {
        Toast.makeText(this, "Объявление: " + announcement.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Показать детали объявления
    }

    @Override
    public void onAnnouncementAdded(Announcement announcement) {
        // Добавляем новое объявление в список
        allAnnouncements.add(0, announcement); // Добавляем в начало списка
        
        // Обновляем отображение
        updateAnnouncements();
        
        Toast.makeText(this, "Объявление добавлено!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
