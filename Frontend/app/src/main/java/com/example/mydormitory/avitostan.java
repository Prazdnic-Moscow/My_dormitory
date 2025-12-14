package com.example.mydormitory;
import java.util.List;

// Guide.java
public class avitostan {
    private int id;
    private String type;
    private String body;
    private int room;
    private String date;
    private List<String> avitoPath;

    public avitostan(int id, String type, String body, int room, String date, List<String> avitoPath) {
        this.id = id;
        this.type = type;
        this.body = body;
        this.room = room;
        this.date = date;
        this.avitoPath = avitoPath;
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getBody() { return body; }
    public int getRoom() { return room; }
    public String getDate() { return date; }
    public List<String> getAvitoPath() { return avitoPath; }
}
