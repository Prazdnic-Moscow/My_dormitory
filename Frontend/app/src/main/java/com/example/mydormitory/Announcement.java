package com.example.mydormitory;

public class Announcement {
    public enum Category {
        GIVE_AWAY("Отдам", R.color.blue),
        TAKE("Возьму", R.color.blue),
        LOST_FOUND("Потеряшки", R.color.blue);

        private final String displayName;
        private final int colorResource;

        Category(String displayName, int colorResource) {
            this.displayName = displayName;
            this.colorResource = colorResource;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColorResource() {
            return colorResource;
        }
    }

    private String title;
    private String description;
    private String location;
    private String contact;
    private Category category;
    private String timestamp;
    private String id;

    public Announcement(String title, String description, String location, String contact, Category category, String timestamp) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.category = category;
        this.timestamp = timestamp;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
