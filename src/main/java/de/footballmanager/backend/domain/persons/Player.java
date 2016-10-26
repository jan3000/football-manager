package de.footballmanager.backend.domain.persons;

import com.neovisionaries.i18n.CountryCode;
import de.footballmanager.backend.domain.statistics.PlayerStatistics;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.enumeration.Talent;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Objects;

public class Player extends Person {

    private Position position;
    private int strength;
    private PlayerStatistics playerStatistics;
    private Talent talent;


    private Player(Builder builder) {
        super.setFirstName(builder.firstName);
        super.setLastName(builder.lastName);
        this.position = builder.position;
        this.strength = builder.strength;

    }

    // TODO fix builder pattern if its not correct
    public static class Builder {
        private final String lastName;
        private final String firstName;
        private Position position;
        private DateTime dateOfBirth;
        private CountryCode homeCountry;
        private int strength;

        public Builder(String firstName, String lastName) {
            this.lastName = lastName;
            this.firstName = firstName;
        }

        public Builder setPosition(Position position) {
            this.position = position;
            return this;
        }
        public Builder setStrength(int strength) {
            this.strength = strength;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(final int strength) {
        this.strength = strength;
    }

//    public String getTeam() {
//        return team.getName();
//    }
//
//    public void setTeam(final Team team) {
//        this.team = team;
//    }

    public int getAge() {
        return Years.yearsBetween(getDateOfBirth(), new DateTime()).getYears();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return strength == player.strength &&
                position == player.position &&
                Objects.equals(playerStatistics, player.playerStatistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), position, strength, playerStatistics);
    }

    @Override
    public String toString() {
        return "Player{" +
                "position=" + position +
                ", strength=" + strength +
                ", playerStatistics=" + playerStatistics +
                '}';
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getFirstName()).append(" ").append(super.getLastName());
        builder.append(", ");
        builder.append(getAge());
        builder.append(", ");
        builder.append(getPosition());
        builder.append(", ");
        builder.append(getStrength());
        return builder.toString();
    }

}
