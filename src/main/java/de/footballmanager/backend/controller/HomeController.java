package de.footballmanager.backend.controller;

import de.footballmanager.backend.api.FootballManagerFacade;
import de.footballmanager.backend.domain.club.Club;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.Table;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.statistics.ScorerStatistic;
import de.footballmanager.backend.domain.statistics.TeamStatistic;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.ClubService;
import de.footballmanager.backend.service.KIService;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Path("/home")
@RequestScoped //  bean lives as long as a single HTTP request-response cycle
@Component
public class HomeController implements FootballManagerFacade {

    public static final String BUNDESLIGA = "Bundesliga";
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private ClubService clubService;
    @Autowired
    private KIService kiService;

    @GET
    @Path("teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Team> getTeams() throws JAXBException, FileNotFoundException {
        System.out.println("HomeController getTeams");
        return leagueService.getLeague(BUNDESLIGA).getTeams();
    }

    @GET
    @Path("clubs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Club> getClubs() throws JAXBException, FileNotFoundException {
        System.out.println("HomeController getClubs");
        return clubService.getAllClubs();
    }

    @GET
    @Path("timeTable/{matchDay}")
    @Produces(MediaType.APPLICATION_JSON)
    public MatchDay getTimeTableForMatchDay(@PathParam("matchDay") int matchDay) {
        System.out.println("HomeController getTimeTableForMatchDay: " + matchDay);
        return leagueService.getTimeTableForMatchDay(BUNDESLIGA, matchDay);
    }

    @GET
    @Path("setNextMatchDayToRunnable")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean setNextMatchDayToRunnable() {
        System.out.println("setNextMatchDayToRunnable");
        MatchDay matchDay = leagueService.getTimeTableForMatchDay(BUNDESLIGA, leagueService.getTimeTable(BUNDESLIGA).getCurrentMatchDay());
        kiService.handleSetStartEleven(matchDay);
        leagueService.startNextMatchDay(BUNDESLIGA);
        return true;
    }


    @GET
    @Path("runNextMatchDayMinute")
    @Produces(MediaType.APPLICATION_JSON)
    public MatchDay runNextMatchDayMinute() {
        return leagueService.runNextMinute(BUNDESLIGA);
    }

    @GET
    @Path("table/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public Table getTable(@PathParam("day") int day) {
        return leagueService.getTable(BUNDESLIGA, day);
    }

    @GET
    @Path("statistic/{teamName}")
    @Produces(MediaType.APPLICATION_JSON)
    public TeamStatistic getTeamStatistic(@PathParam("teamName") String teamName) {
        int currentMatchDay = leagueService.getCurrentMatchDayNumber(BUNDESLIGA);
        TeamStatistic teamStatistics = statisticService.getTeamStatistics(
                leagueService.getTimeTable(BUNDESLIGA), teamName,
                leagueService.getTable(BUNDESLIGA, currentMatchDay - 1), leagueService.getMatchDayToTable(BUNDESLIGA));
        return teamStatistics;
    }

    @GET
    @Path("statistics/league")
    @Produces(MediaType.APPLICATION_JSON)
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

    public void createNewManager(Manager manager) {

    }

    public void getManagerDetails(String teamName) {
    }
}



