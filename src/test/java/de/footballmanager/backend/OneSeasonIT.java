package de.footballmanager.backend;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.*;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.statistics.TeamStatistic;
import de.footballmanager.backend.domain.util.Pair;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.enumeration.ResultType;
import de.footballmanager.backend.service.*;
import de.footballmanager.backend.util.PrintUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OneSeasonIT {

    private static final String BUNDESLIGA = "Bundesliga";
    public static final String ZWEITE_BUNDESLIGA = "2. Bundesliga";

    @Autowired
    private TeamManagerService teamManagerService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private InitializationService initializationService;
    @Autowired
    private KIService kiService;
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private MatchService matchService;

    @Test
    public void runSeasonWithKiAndManagedTeam() throws Exception {

        // given: 1 KI team, 1 self managed team
        initializationService.createLeagues("club.xml", "names.txt", "surnames.txt");

        League league = leagueService.getLeague(BUNDESLIGA);
        assertNotNull(league);
        List<Team> teams = league.getTeams();
        Manager manager = new Manager("Jan", "Buck");
        manager.setComputerManaged(false);
        Team managedTeam = teams.get(0);
        teamManagerService.setTeamManager(manager, managedTeam);

        TimeTable timeTable = getAndAssertTimeTable(BUNDESLIGA);

        // ----------------------------------------
        // when: run match1 day 1
        // ----------------------------------------
        MatchDay matchDay = timeTable.getMatchDay(1);
        Match match1 = matchDay.getMatches().get(0);
        Pair<PlayingSystem, Map<Position, Player>> pair = teamManagerService.getBestPlayersForBestSystem(managedTeam);
        Map<Position, Player> startEleven = pair.getSecond();
        teamManagerService.setStartEleven(match1, managedTeam, startEleven);
        kiService.handleSetStartEleven(matchDay);
        List<Match> matches = matchDay.getMatches();
        assertEquals(1, matches.size());

        matches.forEach(match -> matchService.start(match));

        // run first half
        leagueService.startNextMatchDay(BUNDESLIGA);
        IntStream.range(1, 45).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));
        assertTrue(match1.isStarted());
        assertFalse(match1.isFinished());
        assertNotNull(match1.getHalfTimeResult());
        assertEquals(45, match1.getMinute());

        // make manual player change after 45 minutes
        List<Player> substituteBench = teamManagerService.getSubstituteBench(match1, managedTeam);
        teamManagerService.changePlayer(match1, managedTeam, substituteBench.get(2),
                startEleven.get(Position.LEFT_DEFENDER));
        IntStream.range(45, 70).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));
        assertTrue(match1.isStarted());
        assertFalse(match1.isFinished());
        assertNotNull(match1.getHalfTimeResult());
        assertEquals(70, match1.getMinute());

        // make manual system change after 70 minutes
        teamManagerService.changePlayingSystem(match1, managedTeam, PlayingSystem.SYSTEM_4_2_3_1);
        IntStream.range(70, 90).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));

        matches.forEach(match -> assertTrue(match1.isFinished()));
        System.out.println(PrintUtil.print(match1));

        // then
        Result resultMatch1 = match1.getResult();
        Table currentTable = leagueService.getTable(BUNDESLIGA, 1);
        List<TableEntry> tableEntries = currentTable.getEntries();
        assertNotNull(tableEntries);
        assertEquals(2, tableEntries.size());


        // assert statistics
        String homeTeam1 = match1.getHomeTeam();
        String guestTeam1 = match1.getGuestTeam();
        assertTableEntries(resultMatch1, tableEntries, homeTeam1, guestTeam1);
        assertTeamStatisticsHomeTeam(timeTable, resultMatch1, homeTeam1);
        assertTeamStatisticsGuestTeam(timeTable, resultMatch1, guestTeam1);



        // ----------------------------------------
        // day 2
        // ----------------------------------------
        assertEquals(2, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));
        MatchDay matchDay2 = leagueService.getTimeTableForMatchDay(BUNDESLIGA,
                leagueService.getCurrentMatchDayNumber(BUNDESLIGA));
        assertNotNull(matchDay2);
        assertEquals(1, matchDay2.getNumberOfMatches());
        Match match2 = matchDay2.getMatches().get(0);
        assertNotNull(match2);

        kiService.handleSetStartEleven(matchDay2);
        Pair<PlayingSystem, Map<Position, Player>> bestPlayersForBestSystem =
                teamManagerService.getBestPlayersForBestSystem(managedTeam);
        teamManagerService.setStartEleven(match2, managedTeam, bestPlayersForBestSystem.getSecond());

        leagueService.startNextMatchDay(BUNDESLIGA);
        IntStream.range(1,90).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));

        matches.forEach(match -> assertTrue(match2.isFinished()));
        System.out.println(PrintUtil.print(match2));

        Table currentTable2 = leagueService.getTable(BUNDESLIGA, 2);
        List<TableEntry> tableEntries2 = currentTable2.getEntries();
        assertNotNull(tableEntries2);
        assertEquals(2, tableEntries2.size());


        // assert statistics
        String homeTeam2 = match2.getHomeTeam();
        String guestTeam2 = match2.getGuestTeam();
        assertEquals(homeTeam1, guestTeam2);
        assertEquals(homeTeam2, guestTeam1);
        TeamStatistic teamStatisticHomeTeam1 = statisticService.getTeamStatistics(timeTable, homeTeam1,
                currentTable2, leagueService.getMatchDayToTable(BUNDESLIGA));
        TeamStatistic teamStatisticHomeTeam2 = statisticService.getTeamStatistics(timeTable, homeTeam2,
                currentTable2, leagueService.getMatchDayToTable(BUNDESLIGA));
        assertNotNull(teamStatisticHomeTeam1);
        assertNotNull(teamStatisticHomeTeam1.getPlacementsInSeason()[0]);
        assertNotNull(teamStatisticHomeTeam1.getPlacementsInSeason()[1]);
        int goalsHomeTeam1 = match1.getResult().getHomeGoals() + match2.getResult().getGuestGoals();
        int goalsGuestTeam1 = match1.getResult().getGuestGoals() + match2.getResult().getHomeGoals();
        assertEquals(goalsHomeTeam1, getSumOfGoals(teamStatisticHomeTeam1.getTotalGoals()));
        assertEquals(goalsGuestTeam1, getSumOfGoals(teamStatisticHomeTeam1.getReceivedTotalGoals()));

        assertEquals(goalsGuestTeam1, getSumOfGoals(teamStatisticHomeTeam2.getTotalGoals()));
        assertEquals(goalsHomeTeam1, getSumOfGoals(teamStatisticHomeTeam2.getReceivedTotalGoals()));

        // assert league finished
        assertTrue(timeTable.isClosed());
    }

    @Test
    public void startThreeSeasonsComputerManaged() throws JAXBException, FileNotFoundException {
        initializationService.createLeagues("clubLeague1.xml", "names.txt", "surnames.txt");

        League league = leagueService.getLeague(BUNDESLIGA);
        assertNotNull(league);
        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        runAndAssertRegularDay(BUNDESLIGA);
        runAndAssertLastSeasonDay(BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        // season 2
        leagueService.addNewSeason();
        leagueService.finishDaysUntilNextSeason(BUNDESLIGA);

        assertEquals(2, league.getSeasons().size());
        assertEquals(league.getSeasons().get(1), leagueService.getCurrentSeason(BUNDESLIGA));
        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        runAndAssertRegularDay(BUNDESLIGA);
        runAndAssertLastSeasonDay(BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        // season 3
        leagueService.addNewSeason();
        leagueService.finishDaysUntilNextSeason(BUNDESLIGA);

        assertEquals(3, league.getSeasons().size());
        assertEquals(league.getSeasons().get(2), leagueService.getCurrentSeason(BUNDESLIGA));
        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        runAndAssertRegularDay(BUNDESLIGA);
        runAndAssertLastSeasonDay(BUNDESLIGA);

        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());
    }

    @Test
    public void startThreeSeasonsForTwoLeaguesComputerManaged() throws JAXBException, FileNotFoundException {

        initializationService.createLeagues("club.xml", "names.txt", "surnames.txt");

        League league1 = leagueService.getLeague(BUNDESLIGA);
        assertNotNull(league1);
        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        League league2 = leagueService.getLeague(ZWEITE_BUNDESLIGA);
        assertNotNull(league2);
        assertEquals(1, leagueService.getCurrentMatchDayNumber(ZWEITE_BUNDESLIGA));

        runAndAssertRegularDay(BUNDESLIGA);
        runAndAssertLastSeasonDay(BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        // TODO: goon check 2te
        runAndAssertRegularDay(ZWEITE_BUNDESLIGA);
        runAndAssertLastSeasonDay(ZWEITE_BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(ZWEITE_BUNDESLIGA).getTimeTable().isClosed());

        // season 2 add promoted team
        leagueService.addNewSeason();
        leagueService.finishDaysUntilNextSeason(BUNDESLIGA);

        assertSeasonSetUpCorrectly(league1, BUNDESLIGA);
        assertSeasonSetUpCorrectly(league2, ZWEITE_BUNDESLIGA);

        runAndAssertRegularDay(BUNDESLIGA);
        runAndAssertLastSeasonDay(BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        runAndAssertRegularDay(ZWEITE_BUNDESLIGA);
        runAndAssertLastSeasonDay(ZWEITE_BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(ZWEITE_BUNDESLIGA).getTimeTable().isClosed());

        // season 3
        leagueService.addNewSeason();
        leagueService.finishDaysUntilNextSeason(BUNDESLIGA);

        assertEquals(3, league1.getSeasons().size());
        assertEquals(league1.getSeasons().get(2), leagueService.getCurrentSeason(BUNDESLIGA));
        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        runAndAssertRegularDay(BUNDESLIGA);
        runAndAssertLastSeasonDay(BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        runAndAssertRegularDay(ZWEITE_BUNDESLIGA);
        runAndAssertLastSeasonDay(ZWEITE_BUNDESLIGA);
        assertTrue(leagueService.getCurrentSeason(ZWEITE_BUNDESLIGA).getTimeTable().isClosed());
    }

    public void assertSeasonSetUpCorrectly(League league, String leagueName) {
        assertEquals(2, league.getSeasons().size());
        Season season2 = league.getSeasons().get(1);
        assertEquals(season2, leagueService.getCurrentSeason(leagueName));
        assertEquals(1, leagueService.getCurrentMatchDayNumber(leagueName));

        assertNotNull(season2.getTimeTable());
        assertEquals(2, season2.getTimeTable().getAllMatchDays().size());
        assertNotNull(season2.getTimeTable().getMatchDay(1).getMatches());
        assertEquals(1, season2.getTimeTable().getMatchDay(1).getMatches().size());
        assertNotNull(season2.getTimeTable().getMatchDay(1).getMatches().get(0).getHomeTeam());
        assertNotNull(season2.getTimeTable().getMatchDay(1).getMatches().get(0).getGuestTeam());
    }

    private TimeTable getAndAssertTimeTable(String leagueName) {
        TimeTable timeTable = leagueService.getTimeTable(leagueName);
        assertNotNull(timeTable);
        int numberOfDays = timeTable.getNumberOfMatchDays();
        assertEquals(2, numberOfDays);
        return timeTable;
    }

    private void runAndAssertLastSeasonDay(String league) {
        runNextMatchDay(league, 2);
        assertTrue(leagueService.getCurrentMatchDay(league).isFinished());
    }

    private void runAndAssertRegularDay(String leagueName) {
        runNextMatchDay(leagueName, 2);
        assertFalse(leagueService.getCurrentMatchDay(leagueName).isFinished());
    }

    private void runNextMatchDay(String league, int expectedMatchDayNumber) {
        kiService.handleNextMatchDay(league);
        assertEquals(expectedMatchDayNumber, leagueService.getCurrentMatchDayNumber(league));
        int lastMatchDayNumber = leagueService.getCurrentMatchDayNumber(league) - 1;
        assertTrue(leagueService.getTimeTable(league).getMatchDay(lastMatchDayNumber).isFinished());
    }

    private MatchDay setStartElevenForCurrentMatchDay(TimeTable timeTable, String league) {
        int currentMatchDay = leagueService.getCurrentMatchDayNumber(league);
        MatchDay matchDay = timeTable.getMatchDay(currentMatchDay);
        kiService.handleSetStartEleven(matchDay);
        return matchDay;
    }

    private void assertTableEntries(Result resultMatch1, List<TableEntry> tableEntries, String homeTeamName, String guestTeamName) {
        String firstTeam = tableEntries.get(0).getTeam();
        String secondTeam = tableEntries.get(1).getTeam();
        if (resultMatch1.getHomeGoals() > resultMatch1.getGuestGoals()) {
            assertEquals(ResultType.HOME_WON, resultMatch1.getResultType());
            assertEquals(homeTeamName, firstTeam);
            assertEquals(guestTeamName, secondTeam);
        } else if (resultMatch1.getHomeGoals() < resultMatch1.getGuestGoals()) {
            assertEquals(ResultType.GUEST_WON, resultMatch1.getResultType());
            assertEquals(guestTeamName, firstTeam);
            assertEquals(homeTeamName, secondTeam);
        } else {
            assertEquals(ResultType.DRAW, resultMatch1.getResultType());
            assertTrue(Lists.newArrayList(firstTeam, secondTeam).containsAll(
                    Lists.newArrayList(homeTeamName, guestTeamName)));
        }
    }

    private void assertTeamStatisticsHomeTeam(TimeTable timeTable, Result resultMatch1, String homeTeamName) {
        TeamStatistic teamStatistic = statisticService.getTeamStatistics(timeTable, homeTeamName,
                leagueService.getTable(BUNDESLIGA, 1), leagueService.getMatchDayToTable(BUNDESLIGA));
        assertNotNull(teamStatistic);
        assertEquals(resultMatch1.getHomeGoals(), getSumOfGoals(teamStatistic.getHomeGoals()));
        assertEquals(resultMatch1.getHomeGoals(), getSumOfGoals(teamStatistic.getTotalGoals()));
        assertEquals(0, getSumOfGoals(teamStatistic.getAwayGoals()));
        assertEquals(resultMatch1.getGuestGoals(), getSumOfGoals(teamStatistic.getReceivedHomeGoals()));
        assertEquals(resultMatch1.getGuestGoals(), getSumOfGoals(teamStatistic.getReceivedTotalGoals()));
        assertEquals(0, getSumOfGoals(teamStatistic.getReceivedAwayGoals()));

        assertNotNull(teamStatistic.getPlacementsInSeason()[0]);
        if (resultMatch1.getHomeGoals() > 0) {
            assertTrue(teamStatistic.getScorers().size() > 0);
        }
    }

    private void assertTeamStatisticsGuestTeam(TimeTable timeTable, Result resultMatch1, String guestTeamName) {
        TeamStatistic teamStatistics = statisticService.getTeamStatistics(timeTable, guestTeamName,
                leagueService.getTable(BUNDESLIGA, 1), leagueService.getMatchDayToTable(BUNDESLIGA));
        assertNotNull(teamStatistics);
        assertEquals(resultMatch1.getHomeGoals(), getSumOfGoals(teamStatistics.getReceivedAwayGoals()));
        assertEquals(resultMatch1.getHomeGoals(), getSumOfGoals(teamStatistics.getReceivedTotalGoals()));
        assertEquals(0, getSumOfGoals(teamStatistics.getReceivedHomeGoals()));
        assertEquals(resultMatch1.getGuestGoals(), getSumOfGoals(teamStatistics.getAwayGoals()));
        assertEquals(resultMatch1.getGuestGoals(), getSumOfGoals(teamStatistics.getTotalGoals()));
        assertEquals(0, getSumOfGoals(teamStatistics.getHomeGoals()));

        assertNotNull(teamStatistics.getPlacementsInSeason()[0]);
        if (resultMatch1.getGuestGoals() > 0) {
            assertTrue(teamStatistics.getScorers().size() > 0);
        }
    }

    private int getSumOfGoals(Integer[] goals) {
        return Lists.newArrayList(goals).stream().mapToInt(Integer::intValue).sum();
    }
}
