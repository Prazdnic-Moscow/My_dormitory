package com.example.mydormitory;
import java.util.List;

// Guide.java
public class news {
    private int id;
    private String header;
    private String body;
    private String author;
    private String date;
    private String dateStart;
    private String dateEnd;
    private List<String> newsPath;

    public news(int id, String header, String body, String author, String date, String dateStart, String dateEnd, List<String> newsPath) {
        this.id = id;
        this.header = header;
        this.body = body;
        this.author = author;
        this.date = date;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.newsPath = newsPath;
    }

    // Getters
    public int getId() { return id; }
    public String getHeader() { return header; }
    public String getBody() { return body; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getDateStart() { return dateStart; }
    public String getDateEnd() { return dateEnd; }
    public List<String> getNewsPath() { return newsPath; }
}
