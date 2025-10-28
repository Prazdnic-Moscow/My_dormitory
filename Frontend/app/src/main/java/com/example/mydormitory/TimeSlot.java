package com.example.mydormitory;

public class TimeSlot {
    private String time;
    private boolean isAvailable;
    private boolean isSelected;
    private int machineNumber;

    public TimeSlot(String time, boolean isAvailable, int machineNumber) {
        this.time = time;
        this.isAvailable = isAvailable;
        this.machineNumber = machineNumber;
        this.isSelected = false;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(int machineNumber) {
        this.machineNumber = machineNumber;
    }
}
