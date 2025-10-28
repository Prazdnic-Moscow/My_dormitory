package com.example.mydormitory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Класс для хранения информации о новостях приложения "Мое Общежитие"
 */
public class News {
    
    private String id;
    private String actionDate; // Промежуток действия или дата действия
    private String title; // Заголовок новости
    private String description; // Описание новости
    private String author; // Автор новости
    private long publishDate; // Дата публикации в миллисекундах
    
    public News(String id, String actionDate, String title, String description, String author, long publishDate) {
        this.id = id;
        this.actionDate = actionDate;
        this.title = title;
        this.description = description;
        this.author = author;
        this.publishDate = publishDate;
    }
    
    // Геттеры
    public String getId() { return id; }
    public String getActionDate() { return actionDate; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAuthor() { return author; }
    public long getPublishDate() { return publishDate; }
    
    // Сеттеры
    public void setId(String id) { this.id = id; }
    public void setActionDate(String actionDate) { this.actionDate = actionDate; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublishDate(long publishDate) { this.publishDate = publishDate; }
    
    // Вспомогательные методы
    public String getFormattedPublishDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        return sdf.format(new Date(publishDate));
    }
    
    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", actionDate='" + actionDate + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishDate=" + getFormattedPublishDate() +
                '}';
    }
}
