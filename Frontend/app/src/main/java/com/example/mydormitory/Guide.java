package com.example.mydormitory;
import java.util.List;

// Guide.java
public class Guide {
    private int id;
    private String header;
    private String body;
    private String date;
    private List<String> tutorPath;

    public Guide(int id, String header, String body, String date, List<String> tutorPath) {
        this.id = id;
        this.header = header;
        this.body = body;
        this.date = date;
        this.tutorPath = tutorPath;
    }

    // Getters
    public int getId() { return id; }
    public String getHeader() { return header; }
    public String getBody() { return body; }
    public String getDate() { return date; }
    public List<String> getTutorPath() { return tutorPath; }
}
