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

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

//@Ignore("go on here")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class OneSeasonIT {

    @Autowired
    private TimeTableService timeTableService;
    @Autowired
    private TeamManagerService teamManagerService;
    @Autowired
    private ResultService resultService;
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private StatisticService statisticService;

    @Test
    public void test() throws Exception {

        // given: 1 KI team, 1 self managed team
        leagueService.createLeague("teams.xml", "names.txt", "surnames.txt");
        List<Team> teams = leagueService.getTeams();
        Manager manager = new Manager();
        manager.setFirstName("Jan");
        manager.setLastName("Buck");
        manager.setComputerManaged(false);
        Team managedTeam = teams.get(0);
        Team computerTeam = teams.get(1);
        teamManagerService.setTeamManager(manager, managedTeam);

        TimeTable timeTable = leagueService.getTimeTable();
        assertNotNull(timeTable);
        int numberOfDays = timeTable.getAllMatchDays().size();
        assertEquals(2, numberOfDays);

        // when: run match day 1
        MatchDay matchDay = timeTable.getMatchDay(1);
        Match match = matchDay.getMatches().get(0);
        Pair<PlayingSystem, Map<Position, Player>> pair = teamManagerService.getBestPlayersForBestSystem(managedTeam);
        Map<Position, Player> startEleven = pair.getSecond();
        teamManagerService.setStartEleven(match, managedTeam, startEleven);
        teamManagerService.setStartElevenIfComputerManaged(matchDay);
        List<Match> matches = matchDay.getMatches();
        assertEquals(1, matches.size());

        matches.forEach(Match::start);

        // run first half
        leagueService.startNextMatchDay();
        IntStream.range(1, 45).forEach(i -> leagueService.runNextMinute());
        assertTrue(match.isStarted());
        assertFalse(match.isFinished());
        assertNotNull(match.getHalfTimeResult());
        assertEquals(45, match.getMinute());

        // make manual player change after 45 minutes
        List<Player> substituteBench = teamManagerService.getSubstituteBench(match, managedTeam);
        teamManagerService.changePlayer(match, managedTeam, substituteBench.get(2),
                startEleven.get(Position.LEFT_DEFENDER));
        IntStream.range(45, 70).forEach(i -> leagueService.runNextMinute());
        assertTrue(match.isStarted());
        assertFalse(match.isFinished());
        assertNotNull(match.getHalfTimeResult());
        assertEquals(70, match.getMinute());

        // make manual system change after 70 minutes
        teamManagerService.changePlayingSystem(match, managedTeam, PlayingSystem.SYSTEM_4_2_3_1);
        IntStream.range(70, 90).forEach(i -> leagueService.runNextMinute());

        matches.forEach(match1 -> assertTrue(match.isFinished()));

        Result resultMatch1 = match.getResult();
        System.out.println(resultMatch1.print());

        Table currentTable = leagueService.getTable(1);
        List<TableEntry> tableEntries = currentTable.getEntries();
        assertNotNull(tableEntries);
        assertEquals(2, tableEntries.size());


        // check statistics
        Team homeTeam = match.getHomeTeam();
        Team guestTeam = match.getGuestTeam();
        if (resultMatch1.getHomeGoals() > resultMatch1.getGuestGoals()) {
            assertEquals(ResultType.HOME_WON, resultMatch1.getResultType());
            assertEquals(homeTeam.getName(), tableEntries.get(0).getTeam());
            assertEquals(guestTeam.getName(), tableEntries.get(1).getTeam());
        } else if (resultMatch1.getHomeGoals() < resultMatch1.getGuestGoals()) {
            assertEquals(ResultType.GUEST_WON, resultMatch1.getResultType());
            assertEquals(guestTeam.getName(), tableEntries.get(0).getTeam());
            assertEquals(homeTeam.getName(), tableEntries.get(1).getTeam());
        } else {
            assertEquals(ResultType.DRAW, resultMatch1.getResultType());
            assertTrue(tableEntries.containsAll(Lists.newArrayList(homeTeam.getName(), guestTeam.getName())));
        }

        // assert team statistics
        assertTeamStatisticsHomeTeam(timeTable, resultMatch1, homeTeam);
        assertTeamStatisticsGuestTeam(timeTable, resultMatch1, guestTeam);




        // day 2

    }

    private void assertTeamStatisticsHomeTeam(TimeTable timeTable, Result resultMatch1, Team homeTeam) {
        TeamStatistic teamStatistic = statisticService.getGoalDistribution(timeTable, homeTeam.getName(),
                leagueService.getTable(1), leagueService.getMatchDayToTable());
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
        TeamStatistic teamStatistics = statisticService.getGoalDistribution(timeTable, guestTeam.getName(),
                leagueService.getTable(1), leagueService.getMatchDayToTable());
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
