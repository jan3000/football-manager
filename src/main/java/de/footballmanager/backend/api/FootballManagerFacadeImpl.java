package de.footballmanager.backend.api;

import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.Table;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.statistics.ScorerStatistic;
import de.footballmanager.backend.domain.statistics.TeamStatistic;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Service
public class FootballManagerFacadeImpl implements FootballManagerFacade {

    public static final String BUNDESLIGA = "Bundesliga";

    @Autowired
    private LeagueService leagueService;
    @Autowired
    private StatisticService statisticService;

    @Override
    public List<Team> getTeams() throws JAXBException, FileNotFoundException {
        System.out.println("HomeController getTeams");
        return leagueService.getLeague(BUNDESLIGA).getTeams();
    }

    @Override
    public MatchDay getTimeTableForMatchDay(int matchDay) {
        System.out.println("HomeController getTimeTableForMatchDay: " + matchDay);
        return leagueService.getTimeTableForMatchDay(BUNDESLIGA, matchDay);
    }

    @Override
    public MatchDay runNextMatchDayMinute() {
        return leagueService.runNextMinute(BUNDESLIGA);
    }

    @Override
    public Table getTable(int day) {
        return leagueService.getTable(BUNDESLIGA, day);
    }

    @Override
    public TeamStatistic getTeamStatistic(String teamName) {
        int currentMatchDay = leagueService.getCurrentMatchDayNumber(BUNDESLIGA);
        TeamStatistic teamStatistics = statisticService.getTeamStatistics(
                leagueService.getTimeTable(BUNDESLIGA), teamName,
                leagueService.getTable(BUNDESLIGA, currentMatchDay - 1), leagueService.getMatchDayToTable(BUNDESLIGA));
        return teamStatistics;
    }

    @Override
    public List<ScorerStatistic> getLeagueStatictics() {
        return statisticService.getScorerTable(leagueService.getLeague("Bundesliga").getTeams(),
                leagueService.getTimeTable(BUNDESLIGA));
    }

    @Override
    public Team getTeam(String name) {
        return null;
    }

    @Override
    public void setStartElevenHome(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven) {
//        leagueService.setStartElevenHome(matchDayNumber, teamName, positionToStartEleven);
    }

    @Override
    public void setStartElevenGuest(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven) {
//        leagueService.setStartElevenGuest(matchDayNumber, teamName, positionToStartEleven);
    }

    @Override
    public void initGame() {

    }

    @Override
    public void calculateMatchDay() {

    }

    @Override
    public void showTable() {

    }

    @Override
    public void showFormerMatchDay() {

    }

    @Override
    public void showTeamDetails() {

    }

    @Override
    public void changeTeamDetails() {

    }
}
