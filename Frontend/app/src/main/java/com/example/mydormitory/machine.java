package com.example.mydormitory.model;
public class machine {
    private int id;
    private String name;

    public machine(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    @Override
    public String toString() {return name;}
}
