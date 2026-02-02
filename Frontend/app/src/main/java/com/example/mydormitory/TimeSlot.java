package com.example.mydormitory;

public class TimeSlot {
    private String time;
    private int index;
    private boolean booked;

    public TimeSlot(String time, int index) {
        this.time = time;
        this.index = index;
        this.booked = false;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}