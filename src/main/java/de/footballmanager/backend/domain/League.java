package de.footballmanager.backend.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "league")
@XmlType(propOrder = {"name", "teams"})
public class League {

    private String name;
    private List<Team> teams;
    private List<Season> seasons = Lists.newArrayList();

    public League() {
        super();
    }

    public League(final List<Team> teams) {
        super();
        Preconditions.checkNotNull(teams, "teams must be set to create a league");
        this.teams = teams;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Season> getSeasons() {
        return ImmutableList.copyOf(seasons);
    }

    public void addSeason(Season season) {
        seasons.add(season);
    }
}
