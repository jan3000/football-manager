package de.footballmanager.backend.domain.league;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.club.Team;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class League {

    private String name;
    private List<Team> teams;
    private List<Season> seasons = Lists.newArrayList();

    private League() {}

    public League(String name, final List<Team> teams) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "league name must be set");
        Preconditions.checkNotNull(teams, "teams must be set to create a league");
        this.name = name;
        this.teams = teams;
    }

    public String getName() {
        return name;
    }

    public List<Team> getTeams() {
        return ImmutableList.copyOf(teams);
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
