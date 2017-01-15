package de.footballmanager.backend.domain.util.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stadium")
public class StadiumInitializer {

    private String stadiumName;
    private int stance;
    private int roofedStance;
    private int seats;
    private int roofedSeats;

    @XmlElement(name = "name")
    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }
    @XmlElement(name = "stance")
    public int getStance() {
        return stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }
    @XmlElement(name = "roofedStance")
    public int getRoofedStance() {
        return roofedStance;
    }

    public void setRoofedStance(int roofedStance) {
        this.roofedStance = roofedStance;
    }
    @XmlElement(name = "seats")
    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
    @XmlElement(name = "roofedSeats")
    public int getRoofedSeats() {
        return roofedSeats;
    }

    public void setRoofedSeats(int roofedSeats) {
        this.roofedSeats = roofedSeats;
    }
}
