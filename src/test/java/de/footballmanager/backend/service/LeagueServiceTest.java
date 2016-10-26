package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.*;
import de.footballmanager.backend.enumeration.PlayingSystem;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.footballmanager.backend.util.TestUtil.*;
import static java.util.stream.Collectors.toList;
import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeagueServiceTest {

    private static final PlayingSystem SYSTEM_442 = PlayingSystem.SYSTEM_4_4_2;
    private static final String BUNDESLIGA = "Bundesliga"; // TODO make enum

    private LeagueService leagueService;
    private TimeTable timeTable;
    private List<Team> teams;
    private DateTime today;

    @Before
    public void setUp() {
        leagueService = new LeagueService();

        List<String> teamNames = Lists.newArrayList(TEAM_NAME_1, TEAM_NAME_2, TEAM_NAME_3);
        teams = teamNames.stream().map(name -> createTeam(name, SYSTEM_442)).collect(toList());

        MatchDay matchDay1 = createMatchDay(createFinishedMatch(TEAM_NAME_2, TEAM_NAME_3, 3, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay2 = createMatchDay(createFinishedMatch(TEAM_NAME_3, TEAM_NAME_1, 3, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay3 = createMatchDay(createFinishedMatch(TEAM_NAME_1, TEAM_NAME_2, 2, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay4 = createMatchDay(createFinishedMatch(TEAM_NAME_3, TEAM_NAME_2, 0, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay5 = createMatchDay(createFinishedMatch(TEAM_NAME_1, TEAM_NAME_3, 1, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay6 = createMatchDay(createMatch(TEAM_NAME_2, TEAM_NAME_1, 4, 1, SYSTEM_442, SYSTEM_442));
        timeTable = new TimeTable(Lists.newArrayList(matchDay1, matchDay2, matchDay3, matchDay4, matchDay5, matchDay6));
        IntStream.range(1, 5).forEach(i -> timeTable.incrementCurrentMatchDay());

        today = new DateTime();
        Season season = new Season(today, timeTable, teams);

        Map<String, League> nameToLeague = createLeagueMap(season);
        ReflectionTestUtils.setField(leagueService, "nameToLeague", nameToLeague);

        DateService dateService = createMock(DateService.class);
        expect(dateService.getToday()).andReturn(today).anyTimes();
        ReflectionTestUtils.setField(leagueService, "dateService", dateService);
        replay(dateService);

        leagueService.generateChart(BUNDESLIGA, 1);
        leagueService.generateChart(BUNDESLIGA, 2);
        leagueService.generateChart(BUNDESLIGA, 3);
        leagueService.generateChart(BUNDESLIGA, 4);
        leagueService.generateChart(BUNDESLIGA, 5);

    }

    private ResultService setUpResultService(MatchDay matchDay) {
        ResultService resultService = createMock(ResultService.class);
        resultService.calculateNextMinute(eq((List) Lists.newArrayList(matchDay.getMatches().get(0))));
        expectLastCall().once();

        replay(resultService);
        ReflectionTestUtils.setField(leagueService, "resultService", resultService);
        return resultService;
    }

    private MatchDay createMatchDay(Match match) {
        return new MatchDay(Lists.newArrayList(match));
    }

    @Test
    public void runNextMinute() {
        // given
        MatchDay matchDay1 = createMatchDay(createMatch(TEAM_NAME_1, TEAM_NAME_3, 1, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay2 = createMatchDay(createMatch(TEAM_NAME_2, TEAM_NAME_1, 4, 1, SYSTEM_442, SYSTEM_442));
        TimeTable timeTable = new TimeTable(Lists.newArrayList(matchDay1, matchDay2));
        Season season = new Season(today, timeTable, teams);
        ReflectionTestUtils.setField(leagueService, "nameToLeague", createLeagueMap(season));

        int currentMatchDay = timeTable.getCurrentMatchDay();
        assertFalse(timeTable.getMatchDay(currentMatchDay).isFinished());
        ResultService resultService = setUpResultService(timeTable.getMatchDay(currentMatchDay));

        // run
        MatchDay returnedMatchDay = leagueService.runNextMinute(BUNDESLIGA);

        // assert
        assertThat(returnedMatchDay).isNotNull();
        assertThat(leagueService.getCurrentMatchDayNumber(BUNDESLIGA)).isEqualTo(currentMatchDay);

        verify(resultService);
    }

    @Test
    public void runNextMinuteAllMatchesFinished() {
        // given
        MatchDay matchDay1 = createMatchDay(createMatch(TEAM_NAME_1, TEAM_NAME_3, 1, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay2 = createMatchDay(createMatch(TEAM_NAME_2, TEAM_NAME_1, 4, 1, SYSTEM_442, SYSTEM_442));
        TimeTable timeTable = new TimeTable(Lists.newArrayList(matchDay1, matchDay2));
        Season season = new Season(today, timeTable, teams);
        ReflectionTestUtils.setField(leagueService, "nameToLeague", createLeagueMap(season));

        TimeTableService timeTableService = createMock(TrialAndErrorTimeTableService.class);
        expect(timeTableService.isTimeTableFinished(anyObject())).andReturn(false).once();
        replay(timeTableService);
        ReflectionTestUtils.setField(leagueService, "timeTableService", timeTableService);

        int currentMatchDay = timeTable.getCurrentMatchDay();
        MatchDay matchDay = timeTable.getMatchDay(currentMatchDay);
        Match match = matchDay.getMatches().get(0);
        finishMatch(match);
        assertTrue(match.isFinished());
        ResultService resultService = setUpResultService(matchDay);

        // run
        leagueService.runNextMinute(BUNDESLIGA);

        // assert
        assertThat(leagueService.getCurrentMatchDayNumber(BUNDESLIGA)).isEqualTo(currentMatchDay + 1);
        assertFalse(timeTable.isClosed());

        verify(resultService, timeTableService);
    }

    @Test
    public void runNextMinuteTimeTableShouldBeClosed() {
        // given
        MatchDay matchDay1 = createMatchDay(createFinishedMatch(TEAM_NAME_1, TEAM_NAME_3, 1, 2, SYSTEM_442, SYSTEM_442));
        MatchDay matchDay2 = createMatchDay(createFinishedMatch(TEAM_NAME_2, TEAM_NAME_1, 4, 1, SYSTEM_442, SYSTEM_442));
        TimeTable timeTable = new TimeTable(Lists.newArrayList(matchDay1, matchDay2));
        Season season = new Season(today, timeTable, teams);
        ReflectionTestUtils.setField(leagueService, "nameToLeague", createLeagueMap(season));

        TimeTableService timeTableService = createMock(TrialAndErrorTimeTableService.class);
        expect(timeTableService.isTimeTableFinished(timeTable)).andReturn(true).once();
        replay(timeTableService);
        ReflectionTestUtils.setField(leagueService, "timeTableService", timeTableService);

        timeTable.incrementCurrentMatchDay();
        int currentMatchDay = timeTable.getCurrentMatchDay();
        ResultService resultService = setUpResultService(matchDay2);

        // run
        leagueService.runNextMinute(BUNDESLIGA);

        // assert
        assertThat(leagueService.getCurrentMatchDayNumber(BUNDESLIGA)).isEqualTo(currentMatchDay);
        assertThat(timeTable.isClosed());

        verify(resultService, timeTableService);
    }

    @Test
    public void getTableOfFirstDay() {
        // run
        Table currentTable = leagueService.getTable(BUNDESLIGA, 1);

        // assert
        assertThat(currentTable).isNotNull();
        assertThat(currentTable.getEntries()).hasSize(2);

        TableEntry tableEntry1 = currentTable.getEntries().get(0);
        assertTable(tableEntry1, TEAM_NAME_2, 1, 3);
        assertGoals(tableEntry1, 3, 2, 0, 0);
        assertHomeStatistics(tableEntry1, 1, 0, 0);
        assertAwayStatistics(tableEntry1, 0, 0, 0);
        assertTotalStatistics(tableEntry1, 1, 0, 0);

        TableEntry tableEntry2 = currentTable.getEntries().get(1);
        assertTable(tableEntry2, TEAM_NAME_3, 2, 0);
        assertGoals(tableEntry2, 0, 0, 2, 3);
        assertHomeStatistics(tableEntry2, 0, 0, 0);
        assertAwayStatistics(tableEntry2, 0, 0, 1);
        assertTotalStatistics(tableEntry2, 0, 0, 1);
    }

    @Test
    public void getCurrentTableForThreeTeams() {
        // when finish last unfinished match
        timeTable.incrementCurrentMatchDay();
        Match unfinishedMatch = leagueService.getTimeTable(BUNDESLIGA).getMatchDay(leagueService.getCurrentMatchDayNumber(BUNDESLIGA)).getMatches().get(0);
        finishMatch(unfinishedMatch);
        leagueService.generateChart(BUNDESLIGA, 6);

        // run
        Table currentTable = leagueService.getCurrentTable(BUNDESLIGA);

        // assert
        assertThat(currentTable).isNotNull();
        assertThat(currentTable.getEntries()).hasSize(3);

        TableEntry tableEntry1 = currentTable.getEntries().get(2);
        assertTable(tableEntry1, TEAM_NAME_1, 3, 1);
        assertGoals(tableEntry1, 3, 4, 3, 7);
        assertHomeStatistics(tableEntry1, 0, 1, 1);
        assertAwayStatistics(tableEntry1, 0, 0, 2);
        assertTotalStatistics(tableEntry1, 0, 1, 3);

        TableEntry tableEntry2 = currentTable.getEntries().get(0);
        assertTable(tableEntry2, TEAM_NAME_2, 1, 10);
        assertGoals(tableEntry2, 7, 3, 4, 2);
        assertHomeStatistics(tableEntry2, 2, 0, 0);
        assertAwayStatistics(tableEntry2, 1, 1, 0);
        assertTotalStatistics(tableEntry2, 3, 1, 0);

        TableEntry tableEntry3 = currentTable.getEntries().get(1);
        assertTable(tableEntry3, TEAM_NAME_3, 2, 6);
        assertGoals(tableEntry3, 3, 4, 4, 4);
        assertHomeStatistics(tableEntry3, 1, 0, 1);
        assertAwayStatistics(tableEntry3, 1, 0, 1);
        assertTotalStatistics(tableEntry3, 2, 0, 2);
    }

    private Map<String, League> createLeagueMap(Season season) {
        Map<String, League> nameToLeague = Maps.newHashMap();
        League league = new League();
        league.addSeason(season);
        nameToLeague.put(BUNDESLIGA, league);
        return nameToLeague;
    }

    private void assertHomeStatistics(TableEntry tableEntry, int won, int draw, int lost) {
        assertThat(tableEntry.getHomeGamesWon()).isEqualTo(won);
        assertThat(tableEntry.getHomeGamesDraw()).isEqualTo(draw);
        assertThat(tableEntry.getHomeGamesLost()).isEqualTo(lost);
    }

    private void assertAwayStatistics(TableEntry tableEntry, int won, int draw, int lost) {
        assertThat(tableEntry.getAwayGamesWon()).isEqualTo(won);
        assertThat(tableEntry.getAwayGamesDraw()).isEqualTo(draw);
        assertThat(tableEntry.getAwayGamesLost()).isEqualTo(lost);
    }

    private void assertTotalStatistics(TableEntry tableEntry, int won, int draw, int lost) {
        assertThat(tableEntry.getTotalGamesWon()).isEqualTo(won);
        assertThat(tableEntry.getTotalGamesDraw()).isEqualTo(draw);
        assertThat(tableEntry.getTotalGamesLost()).isEqualTo(lost);
    }

    private void assertGoals(TableEntry tableEntry, int homeGoals, int receivedHomeGoals, int awayGoals, int receivedAwayGoals) {
        assertThat(tableEntry.getHomeGoals()).isEqualTo(homeGoals);
        assertThat(tableEntry.getReceivedHomeGoals()).isEqualTo(receivedHomeGoals);
        assertThat(tableEntry.getAwayGoals()).isEqualTo(awayGoals);
        assertThat(tableEntry.getReceivedAwayGoals()).isEqualTo(receivedAwayGoals);
    }

    private void assertTable(TableEntry tableEntry, String team, int place, int points) {
        assertThat(tableEntry.getTeam()).isEqualTo(team);
        assertThat(tableEntry.getPlace()).isEqualTo(place);
        assertThat(tableEntry.getPoints()).isEqualTo(points);
    }


}
