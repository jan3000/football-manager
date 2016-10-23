package de.footballmanager.backend.domain;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder = {"startDate", "endDate", "teams", "timeTable"})
public class Season {


    private DateTime startDate;
    private DateTime endDate;
    private List<Team> teams;
    private TimeTable timeTable;

    public Season() {}

    public Season(DateTime startDate, TimeTable timeTable, List<Team> teams) {
        this.startDate = startDate;
        this.endDate = startDate.plusYears(1);
        this.teams = teams;
        this.timeTable = timeTable;
    }

    public DateTime getStartDate() {
        return new DateTime(startDate);
    }

    public DateTime getEndDate() {
        return new DateTime(endDate);
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
}
