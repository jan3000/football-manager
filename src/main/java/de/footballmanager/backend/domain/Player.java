package de.footballmanager.backend.domain;

import com.neovisionaries.i18n.CountryCode;
import org.joda.time.DateTime;

import de.footballmanager.backend.enumeration.Position;
import org.joda.time.Years;

public class Player {

    private String lastname;
    private String firstname;
    private Position position;
    private DateTime dateOfBirth;
    private CountryCode homeCountry;
    private int strength;
//    private Team team;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(final Position position) {
        this.position = position;
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
