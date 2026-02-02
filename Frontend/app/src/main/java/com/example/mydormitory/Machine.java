package com.example.mydormitory;

import java.util.List;

public class Machine {
    private int id;
    private String name;
    private List<ReserveWashMachine> reservations;

    public Machine(int id, String name, List<ReserveWashMachine> reservations) {
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

    public List<ReserveWashMachine> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReserveWashMachine> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Machine{" + "name='" + name + '\'' + '}';
    }
}
