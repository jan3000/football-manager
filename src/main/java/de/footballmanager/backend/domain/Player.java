package de.footballmanager.backend.domain;

import com.neovisionaries.i18n.CountryCode;
import de.footballmanager.backend.enumeration.Position;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Objects;

public class Player {

    private final String lastname;
    private final String firstname;
    private final Position position;
    private DateTime dateOfBirth;
    private CountryCode homeCountry;
    private int strength;
    private PlayerStatistics playerStatistics;
//    private Team team;


    private Player(Builder builder) {
        this.firstname = builder.firstName;
        this.lastname = builder.lastName;
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

        public Builder(String lastName, String firstName) {
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

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return getFirstname() + " " + getLastname();
    }

    public String getFirstname() {
        return firstname;
    }

    public Position getPosition() {
        return position;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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


    public CountryCode getHomeCountry() {
        return homeCountry;
    }

    public void setHomeCountry(CountryCode homeCountry) {
        this.homeCountry = homeCountry;
    }

    public int getAge() {
        return Years.yearsBetween(getDateOfBirth(), new DateTime()).getYears();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return strength == player.strength &&
                Objects.equals(lastname, player.lastname) &&
                Objects.equals(firstname, player.firstname) &&
                position == player.position &&
                Objects.equals(dateOfBirth, player.dateOfBirth) &&
                homeCountry == player.homeCountry &&
                Objects.equals(playerStatistics, player.playerStatistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastname, firstname, position, dateOfBirth, homeCountry, strength, playerStatistics);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Player [lastname=");
        builder.append(lastname);
        builder.append(", firstname=");
        builder.append(firstname);
        builder.append(", position=");
        builder.append(position);
        builder.append(", dateOfBirth=");
        builder.append(dateOfBirth);
        builder.append(", strength=");
        builder.append(strength);
//        builder.append(", team=");
//        builder.append(team);
        builder.append("]");
        return builder.toString();
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(firstname).append(" ").append(lastname);
        builder.append(", ");
        builder.append(getAge());
        builder.append(", ");
        builder.append(getPosition());
        builder.append(", ");
        builder.append(getStrength());
        return builder.toString();
    }

}
