package de.footballmanager.backend.domain;

import com.google.common.base.Preconditions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "league")
@XmlType(propOrder = {"teams", "timeTable"})
public class League {

    private List<Team> teams;
    private TimeTable timeTable;

    public League() {
        super();
    }

    public League(final List<Team> teams) {
        super();
        Preconditions.checkNotNull("teams must be set to create a league", teams);
        this.teams = teams;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(final TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    @XmlElementWrapper(name = "teams")
    @XmlElement(name = "team")
    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(final List<Team> teams) {
        this.teams = teams;
    }

    public int getNumberOfTeams() {
        return teams.size();
    }


}
