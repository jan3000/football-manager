package de.footballmanager.backend.api;


import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.Table;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.statistics.ScorerStatistic;
import de.footballmanager.backend.domain.statistics.TeamStatistic;
import de.footballmanager.backend.enumeration.Position;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public abstract interface FootballManagerFacade {


    List<Team> getTeams() throws JAXBException, FileNotFoundException;

    MatchDay getTimeTableForMatchDay(int matchDay);

    MatchDay runNextMatchDayMinute();

    Table getTable(int day);

    TeamStatistic getTeamStatistic(String teamName);

    List<ScorerStatistic> getLeagueStatictics();

    // Team screen
    public Team getTeam(String name);


    // Match Day
    public void setStartElevenHome(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven);

    public void setStartElevenGuest(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven);

    // Statistics


    public void initGame();


    public void calculateMatchDay();


    public void showTable();


    public void showFormerMatchDay();


    public void showTeamDetails();


    public void changeTeamDetails();


}
