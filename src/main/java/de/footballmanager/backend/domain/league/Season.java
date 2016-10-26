package de.footballmanager.backend.domain.league;

import de.footballmanager.backend.domain.club.Team;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

@XmlType(propOrder = {"startDate", "endDate", "teams", "timeTable"})
public class Season {

    private String name;
    private DateTime startDate;
    private DateTime endDate;
    private List<Team> teams;
    private TimeTable timeTable;

    public Season() {}

    public Season(DateTime startDate, TimeTable timeTable, List<Team> teams) {
        this.startDate = startDate;
        this.endDate = startDate.plusYears(1).minusDays(1);
        this.teams = teams;
        this.timeTable = timeTable;
        this.name = startDate.toString("yy") + "/" + endDate.toString("yy");
    }

    public String getName() {
        return name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Season season = (Season) o;
        return Objects.equals(startDate, season.startDate) &&
                Objects.equals(endDate, season.endDate) &&
                Objects.equals(teams, season.teams) &&
                Objects.equals(timeTable, season.timeTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, teams, timeTable);
    }

    @Override
    public String toString() {
        return "Season{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
