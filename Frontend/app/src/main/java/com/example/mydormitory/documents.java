package com.example.mydormitory;
import java.util.List;

// Guide.java
public class documents {
    private int id;
    private String body;
    private String date;
    private List<String> documentsPath;

    public documents(int id, String body, String date, List<String> documentsPath) {
        this.id = id;
        this.body = body;
        this.date = date;
        this.documentsPath = documentsPath;
    }

    // Getters
    public int getId() { return id; }
    public String getBody() { return body; }
    public String getDate() { return date; }
    public List<String> getDocumentsPath() { return documentsPath; }
}
