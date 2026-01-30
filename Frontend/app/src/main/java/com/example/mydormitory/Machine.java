package com.example.mydormitory;

import java.util.List;

public class Machine {
    private int id;
    private String name;
    private List<reserveWashMachine> reservations;

    public Machine(int id, String name, List<reserveWashMachine> reservations) {
        this.id = id;
        this.name = name;
        this.reservations = reservations;
    }

    public Machine() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<reserveWashMachine> getReservations() {
        return reservations;
    }

    public void setReservations(List<reserveWashMachine> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Machine{" + "name='" + name + '\'' + '}';
    }
}
