package de.footballmanager.backend;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.enumeration.ResultType;
import de.footballmanager.backend.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OneSeasonIT {

    private static final String BUNDESLIGA = "Bundesliga";

    @Autowired
    private TeamManagerService teamManagerService;
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private StatisticService statisticService;

    @Test
    public void runSeasonWithKiAndManagedTeam() throws Exception {

        // given: 1 KI team, 1 self managed team
        League league = createLeague();
        List<Team> teams = league.getTeams();
        Manager manager = new Manager();
        manager.setFirstName("Jan");
        manager.setLastName("Buck");
        manager.setComputerManaged(false);
        Team managedTeam = teams.get(0);
        teamManagerService.setTeamManager(manager, managedTeam);

        TimeTable timeTable = getTimeTable();

        // ----------------------------------------
        // when: run match1 day 1
        // ----------------------------------------
        MatchDay matchDay = timeTable.getMatchDay(1);
        Match match1 = matchDay.getMatches().get(0);
        Pair<PlayingSystem, Map<Position, Player>> pair = teamManagerService.getBestPlayersForBestSystem(managedTeam);
        Map<Position, Player> startEleven = pair.getSecond();
        teamManagerService.setStartEleven(match1, managedTeam, startEleven);
        teamManagerService.setStartElevenIfComputerManaged(matchDay);
        List<Match> matches = matchDay.getMatches();
        assertEquals(1, matches.size());

        matches.forEach(Match::start);

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
        System.out.println(match1.printMatch());

        // then
        Result resultMatch1 = match1.getResult();
        Table currentTable = leagueService.getTable(BUNDESLIGA, 1);
        List<TableEntry> tableEntries = currentTable.getEntries();
        assertNotNull(tableEntries);
        assertEquals(2, tableEntries.size());


        // assert statistics
        Team homeTeam1 = match1.getHomeTeam();
        Team guestTeam1 = match1.getGuestTeam();
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

        teamManagerService.setStartElevenIfComputerManaged(matchDay2);
        Pair<PlayingSystem, Map<Position, Player>> bestPlayersForBestSystem =
                teamManagerService.getBestPlayersForBestSystem(managedTeam);
        teamManagerService.setStartEleven(match2, managedTeam, bestPlayersForBestSystem.getSecond());

        leagueService.startNextMatchDay(BUNDESLIGA);
        IntStream.range(1,90).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));

        matches.forEach(match -> assertTrue(match2.isFinished()));
        System.out.println(match2.printMatch());

        Table currentTable2 = leagueService.getTable(BUNDESLIGA, 2);
        List<TableEntry> tableEntries2 = currentTable2.getEntries();
        assertNotNull(tableEntries2);
        assertEquals(2, tableEntries2.size());


        // assert statistics
        Team homeTeam2 = match2.getHomeTeam();
        Team guestTeam2 = match2.getGuestTeam();
        assertEquals(homeTeam1, guestTeam2);
        assertEquals(homeTeam2, guestTeam1);
        TeamStatistic teamStatisticHomeTeam1 = statisticService.getTeamStatistics(timeTable, homeTeam1.getName(),
                currentTable2, leagueService.getMatchDayToTable());
        TeamStatistic teamStatisticHomeTeam2 = statisticService.getTeamStatistics(timeTable, homeTeam2.getName(),
                currentTable2, leagueService.getMatchDayToTable());
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
        League league = createLeague();
        TimeTable timeTable = getTimeTable();
        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        setStartElevenForCurrentMatchDay(timeTable);
        runAndAssertDay1();

        setStartElevenForCurrentMatchDay(timeTable);
        runAndAssertDay2();

        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        // season 2
        leagueService.addNewSeason(BUNDESLIGA, leagueService.getTeams(BUNDESLIGA));

        leagueService.finishDaysUntilNextSeason(BUNDESLIGA);
        timeTable = getTimeTable();

        assertEquals(2, league.getSeasons().size());
        assertEquals(league.getSeasons().get(1), leagueService.getCurrentSeason(BUNDESLIGA));

        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        setStartElevenForCurrentMatchDay(timeTable);
        runAndAssertDay1();

        setStartElevenForCurrentMatchDay(timeTable);
        runAndAssertDay2();

        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());

        // season 3
        leagueService.addNewSeason(BUNDESLIGA, leagueService.getTeams(BUNDESLIGA));

        leagueService.finishDaysUntilNextSeason(BUNDESLIGA);
        timeTable = getTimeTable();

        assertEquals(3, league.getSeasons().size());
        assertEquals(league.getSeasons().get(2), leagueService.getCurrentSeason(BUNDESLIGA));

        assertEquals(1, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));

        setStartElevenForCurrentMatchDay(timeTable);
        runAndAssertDay1();

        setStartElevenForCurrentMatchDay(timeTable);
        runAndAssertDay2();

        assertTrue(leagueService.getCurrentSeason(BUNDESLIGA).getTimeTable().isClosed());
    }

    private TimeTable getTimeTable() {
        TimeTable timeTable = leagueService.getTimeTable(BUNDESLIGA);
        assertNotNull(timeTable);
        int numberOfDays = timeTable.getNumberOfMatchDays();
        assertEquals(2, numberOfDays);
        return timeTable;
    }

    private League createLeague() throws JAXBException, FileNotFoundException {
        leagueService.createLeagues("teams.xml", "names.txt", "surnames.txt");
        League league = leagueService.getLeague("Bundesliga");
        assertNotNull(league);
        return league;
    }

    private void runAndAssertDay2() {
        int lastMatchDayNumber;
        leagueService.startNextMatchDay(BUNDESLIGA);
        IntStream.range(1, 90).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));
        assertEquals(2, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));
        lastMatchDayNumber = leagueService.getCurrentMatchDayNumber(BUNDESLIGA) - 1;
        assertTrue(leagueService.getTimeTable(BUNDESLIGA).getMatchDay(lastMatchDayNumber).isFinished());
        assertTrue(leagueService.getCurrentMatchDay(BUNDESLIGA).isFinished());
    }

    private void runAndAssertDay1() {
        leagueService.startNextMatchDay(BUNDESLIGA);
        IntStream.range(1, 90).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));
        assertEquals(2, leagueService.getCurrentMatchDayNumber(BUNDESLIGA));
        int lastMatchDayNumber = leagueService.getCurrentMatchDayNumber(BUNDESLIGA) - 1;
        assertTrue(leagueService.getTimeTable(BUNDESLIGA).getMatchDay(lastMatchDayNumber).isFinished());
        assertFalse(leagueService.getCurrentMatchDay(BUNDESLIGA).isFinished());
    }

    private MatchDay setStartElevenForCurrentMatchDay(TimeTable timeTable) {
        int currentMatchDay = leagueService.getCurrentMatchDayNumber(BUNDESLIGA);
        MatchDay matchDay = timeTable.getMatchDay(currentMatchDay);
        teamManagerService.setStartElevenIfComputerManaged(matchDay);
        return matchDay;
    }

    private void assertTableEntries(Result resultMatch1, List<TableEntry> tableEntries, Team homeTeam, Team guestTeam) {
        String firstTeam = tableEntries.get(0).getTeam();
        String secondTeam = tableEntries.get(1).getTeam();
        if (resultMatch1.getHomeGoals() > resultMatch1.getGuestGoals()) {
            assertEquals(ResultType.HOME_WON, resultMatch1.getResultType());
            assertEquals(homeTeam.getName(), firstTeam);
            assertEquals(guestTeam.getName(), secondTeam);
        } else if (resultMatch1.getHomeGoals() < resultMatch1.getGuestGoals()) {
            assertEquals(ResultType.GUEST_WON, resultMatch1.getResultType());
            assertEquals(guestTeam.getName(), firstTeam);
            assertEquals(homeTeam.getName(), secondTeam);
        } else {
            assertEquals(ResultType.DRAW, resultMatch1.getResultType());
            assertTrue(Lists.newArrayList(firstTeam, secondTeam).containsAll(
                    Lists.newArrayList(homeTeam.getName(), guestTeam.getName())));
        }
    }

    private void assertTeamStatisticsHomeTeam(TimeTable timeTable, Result resultMatch1, Team homeTeam) {
        TeamStatistic teamStatistic = statisticService.getTeamStatistics(timeTable, homeTeam.getName(),
                leagueService.getTable(BUNDESLIGA, 1), leagueService.getMatchDayToTable());
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

    private void assertTeamStatisticsGuestTeam(TimeTable timeTable, Result resultMatch1, Team guestTeam) {
        TeamStatistic teamStatistics = statisticService.getTeamStatistics(timeTable, guestTeam.getName(),
                leagueService.getTable(BUNDESLIGA, 1), leagueService.getMatchDayToTable());
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
