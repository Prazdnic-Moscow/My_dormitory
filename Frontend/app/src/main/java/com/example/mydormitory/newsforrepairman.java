package com.example.mydormitory;
import java.util.List;

// Guide.java
public class newsforrepairman {
    private int id;
    private String type;
    private String body;
    private String date;
    private int room;
    private int user_id;
    private int repairman_id;
    private boolean activity;
    private List<String> newsPath;

    public newsforrepairman(int id, String type, String body, String date, int room, int user_id, int repairman_id, boolean activity, List<String> newsPath) {
        this.id = id;
        this.type = type;
        this.body = body;
        this.date = date;
        this.room = room;
        this.user_id = user_id;
        this.activity = activity;
        this.newsPath = newsPath;
        this.repairman_id = repairman_id;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    // Getters
    public int getId() { return id; }

    public String getType()
    {
        return Type.getTypeStrByType(type);
    }
    public String getBody() { return body; }
    public int getRoom() { return room; }
    public int getUserId() { return user_id; }
    public int getRepairmanId() { return repairman_id; }

    public String getDate() { return date; }
    public boolean getActivity() { return activity; }
    public List<String> getNewsPath() { return newsPath; }
}
