package de.footballmanager.backend.domain.club;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "team")
@XmlType(propOrder = {"name", "strength", "players"})
public class Team {

//    private Manager manager;
    private String name;
    private int strength;
    private List<Player> players = Lists.newArrayList();

    public Team() {
    }

    public Team(final String name) {
        super();
        this.name = name;
    }

    public Team(final String name, final int strength) {
        super();
        this.name = name;
        this.strength = strength;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(final List<Player> players) {
        this.players = players;
    }

    @XmlElement(name = "strength")
    public int getStrength() {
        return strength;
    }

    public void setStrength(final int strength) {
        this.strength = strength;
    }

//    public Manager getManager() {
//        return manager;
//    }
//
//    public void setManager(Manager manager) {
//        this.manager = manager;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Team{" +
                ", name='" + name + '\'' +
                ", strength=" + strength +
//                ", players=" + players +
                '}';
    }

}
