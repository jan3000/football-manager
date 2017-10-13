package de.footballmanager.backend.domain.util.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "club")
public class ClubInitializer {

    private String name;
    private int strength;
    private int capital;
    private StadiumInitializer stadiumInitializer;


    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "strength")
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    @XmlElement(name = "capital")
    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    @XmlElement(name = "stadium")
    public StadiumInitializer getStadiumInitializer() {
        return stadiumInitializer;
    }

    public void setStadiumInitializer(StadiumInitializer stadiumInitializer) {
        this.stadiumInitializer = stadiumInitializer;
    }
}
