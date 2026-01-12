package com.example.mydormitory;
import java.util.List;

// Guide.java
public class newsforrepairman {
    private int id;
    private String type;
    private String body;
    private String date;
    private int room;
    private boolean activity;
    private List<String> newsPath;

    public newsforrepairman(int id, String type, String body, String date, int room, boolean activity, List<String> newsPath) {
        this.id = id;
        this.type = type;
        this.body = body;
        this.date = date;
        this.room = room;
        this.activity = activity;
        this.newsPath = newsPath;
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getBody() { return body; }
    public int getRoom() { return room; }
    public String getDate() { return date; }
    public boolean getActivity() { return activity; }
    public List<String> getNewsPath() { return newsPath; }
}
