package de.footballmanager.backend.domain.infrastructure;

public class Stadium {

    private int stance;
    private int roofedStance;
    private int seats;
    private int roofedSeats;

    public int getCapacity() {
        return stance + seats;
    }

    public int getStance() {
        return stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public int getRoofedStance() {
        return roofedStance;
    }

    public void setRoofedStance(int roofedStance) {
        this.roofedStance = roofedStance;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getRoofedSeats() {
        return roofedSeats;
    }

    public void setRoofedSeats(int roofedSeats) {
        this.roofedSeats = roofedSeats;
    }
}
