package de.footballmanager.backend.domain;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "team")
@XmlType(propOrder = {"name", "strength", "players"})
public class Team {

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Team other = (Team) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Team [name=");
        builder.append(name);
        builder.append(", strength=");
        builder.append(strength);
        builder.append(", players=");
        builder.append(players);
        builder.append("]");
        return builder.toString();
    }

}
