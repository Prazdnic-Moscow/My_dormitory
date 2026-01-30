package com.example.mydormitory;

public class reserveWashMachine {
    private int id;
    private int UserId;
    private int MachineId;
    private String date;
    private String startTime;
    private float duration;

    public reserveWashMachine(int id, int userId, int machineId, String date, String startTime, float duration) {
        this.id = id;
        UserId = userId;
        MachineId = machineId;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getMachineId() {
        return MachineId;
    }

    public void setMachineId(int machineId) {
        MachineId = machineId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}
