package com.example.mydormitory;
import java.util.List;

// Guide.java
public class Documents {
    private int id;
    private String body;
    private String date;
    private List<String> documentsPath;

    public Documents(int id, String body, String date, List<String> documentsPath) {
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
