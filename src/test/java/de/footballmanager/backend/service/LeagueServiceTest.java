package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.*;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.util.TestUtil;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.footballmanager.backend.util.TestUtil.*;
import static java.util.stream.Collectors.toList;
import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class LeagueServiceTest {

    private static final PlayingSystem SYSTEM_442 = PlayingSystem.SYSTEM_4_4_2;
    private static final String BUNDESLIGA = "Bundesliga"; // TODO make enum
    public static final String LEAGUE_1 = "league1";
    public static final String LEAGUE_2 = "league2";
    public static final String LEAGUE_3 = "league3";
    public static final String LEAGUE_4 = "league4";
    public static final String TEAM_1 = "team1";
    public static final String TEAM_2 = "team2";
    public static final String TEAM_3 = "team3";
    public static final String TEAM_4 = "team4";
    public static final String TEAM_5 = "team5";
    public static final String TEAM_6 = "team6";
    public static final String TEAM_9 = "team9";
    public static final String TEAM_7 = "team7";
    public static final String TEAM_8 = "team8";
    public static final String TEAM_10 = "team10";
    public static final String TEAM_11 = "team11";
    public static final String TEAM_12 = "team12";

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
        setField(leagueService, "nameToLeague", nameToLeague);

        DateService dateService = createMock(DateService.class);
        expect(dateService.getToday()).andReturn(today).anyTimes();
        setField(leagueService, "dateService", dateService);
        replay(dateService);

        leagueService.generateTable(BUNDESLIGA, 1);
        leagueService.generateTable(BUNDESLIGA, 2);
        leagueService.generateTable(BUNDESLIGA, 3);
        leagueService.generateTable(BUNDESLIGA, 4);
        leagueService.generateTable(BUNDESLIGA, 5);

    }

    private ResultService setUpResultService(MatchDay matchDay) {
        ResultService resultService = createMock(ResultService.class);
        resultService.calculateNextMinute(eq((List) Lists.newArrayList(matchDay.getMatches().get(0))));
        expectLastCall().once();

        replay(resultService);
        setField(leagueService, "resultService", resultService);
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
        setField(leagueService, "nameToLeague", createLeagueMap(season));

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
        setField(leagueService, "nameToLeague", createLeagueMap(season));

        TimeTableService timeTableService = createMock(TrialAndErrorTimeTableService.class);
        expect(timeTableService.isTimeTableFinished(anyObject())).andReturn(false).once();
        replay(timeTableService);
        setField(leagueService, "timeTableService", timeTableService);

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
        setField(leagueService, "nameToLeague", createLeagueMap(season));

        TimeTableService timeTableService = createMock(TrialAndErrorTimeTableService.class);
        expect(timeTableService.isTimeTableFinished(timeTable)).andReturn(true).once();
        replay(timeTableService);
        setField(leagueService, "timeTableService", timeTableService);

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
        leagueService.generateTable(BUNDESLIGA, 6);

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

    @Test
    public void addNewSeasonsFor4Leagues() {

        // given
        Map<String, League> nameToLeague = Maps.newHashMap();
        List<String> teamsLeague1Season1 = Lists.newArrayList(TEAM_1, TEAM_2, TEAM_3);
        List<String> teamsLeague2Season1 = Lists.newArrayList(TEAM_4, TEAM_5, TEAM_6);
        List<String> teamsLeague3Season1 = Lists.newArrayList(TEAM_7, TEAM_8, TEAM_9);
        List<String> teamsLeague4Season1 = Lists.newArrayList(TEAM_10, TEAM_11, TEAM_12);
        League league1 = createLeague(LEAGUE_1, teamsLeague1Season1, 1);

        Season season1 = setSeason(league1, teamsLeague1Season1);
        nameToLeague.put(LEAGUE_1, league1);
        League league2 = createLeague(LEAGUE_2, Lists.newArrayList(TEAM_4, TEAM_5, TEAM_6), 1);
        League league3 = createLeague(LEAGUE_3, Lists.newArrayList(TEAM_7, TEAM_8, TEAM_9), 1);
        League league4 = createLeague(LEAGUE_4, Lists.newArrayList(TEAM_10, TEAM_11, TEAM_12), 1);

        setSeason(league2, teamsLeague2Season1);
        setSeason(league3, teamsLeague3Season1);
        setSeason(league4, teamsLeague4Season1);

        nameToLeague.put(LEAGUE_1, league1);
        nameToLeague.put(LEAGUE_2, league2);
        nameToLeague.put(LEAGUE_3, league3);
        nameToLeague.put(LEAGUE_4, league4);
        List<String> leaguePriorityList = Lists.newArrayList(LEAGUE_1, LEAGUE_2, LEAGUE_3, LEAGUE_4);
        setField(leagueService, "leaguePriorityList", leaguePriorityList);
        setField(leagueService, "nameToLeague", nameToLeague);

        TrialAndErrorTimeTableService timeTableService = createMock(TrialAndErrorTimeTableService.class);
        List<Team> expectedTeamsLeague1 = Lists.newArrayList(createTeam(TEAM_1), createTeam(TEAM_2), createTeam(TEAM_4));
        List<Team> expectedTeamsLeague2 = Lists.newArrayList(createTeam(TEAM_3), createTeam(TEAM_5), createTeam(TEAM_7));
        List<Team> expectedTeamsLeague3 = Lists.newArrayList(createTeam(TEAM_6), createTeam(TEAM_8), createTeam(TEAM_10));
        List<Team> expectedTeamsLeague4 = Lists.newArrayList(createTeam(TEAM_9), createTeam(TEAM_11), createTeam(TEAM_12));
        DateTime expectedStartDateSeason2 = season1.getEndDate().plusDays(1);
        TimeTable timeTableLeague1Season2 = createTimeTable(expectedTeamsLeague1);
        TimeTable timeTableLeague2Season2 = createTimeTable(expectedTeamsLeague2);
        TimeTable timeTableLeague3Season2 = createTimeTable(expectedTeamsLeague3);
        TimeTable timeTableLeague4Season2 = createTimeTable(expectedTeamsLeague4);
        expect(timeTableService.createTimeTable(expectedTeamsLeague1, expectedStartDateSeason2))
                .andReturn(timeTableLeague1Season2).once();
        expect(timeTableService.createTimeTable(expectedTeamsLeague2, expectedStartDateSeason2))
                .andReturn(timeTableLeague2Season2).once();
        expect(timeTableService.createTimeTable(expectedTeamsLeague3, expectedStartDateSeason2))
                .andReturn(timeTableLeague3Season2).once();
        expect(timeTableService.createTimeTable(expectedTeamsLeague4, expectedStartDateSeason2))
                .andReturn(timeTableLeague4Season2).once();
        setField(leagueService, "timeTableService", timeTableService);

        replay(timeTableService);

        // when
        leagueService.addNewSeason();

        // then
        assertNotNull(league1.getSeasons());
        assertEquals(2, league1.getSeasons().size());
        Season league1Season1 = league1.getSeasons().get(0);
        assertNotNull(league1Season1);
        assertSeason(league1, expectedTeamsLeague1);
        assertSeason(league2, expectedTeamsLeague2);
        assertSeason(league3, expectedTeamsLeague3);
        assertSeason(league4, expectedTeamsLeague4);

        // assert season1 of league 1 is still unchanged
        league1Season1.getTeams().forEach(team -> assertTrue(teamsLeague1Season1.contains(team.getName())));
        assertEquals(3, league1Season1.getTeams().size());
        assertNotNull(league1Season1.getTimeTable());
        assertNotNull(league1Season1.getMatchDayToTable());
        verify(timeTableService);
    }

    public void assertSeason(League league1, List<Team> expectedTeamsLeague1) {
        Season league1Season2 = league1.getSeasons().get(1);
        assertNotNull(league1Season2);
        assertEquals("13/14", league1Season2.getName());
        assertEquals(expectedTeamsLeague1, league1Season2.getTeams());
        assertNotNull(league1Season2.getTimeTable());
        assertNotNull(league1Season2.getMatchDayToTable());
    }

    private Season setSeason(League league1, List<String> teamNames) {
        List<Team> teams = teamNames.stream().map(teamName -> createTeam(teamName, SYSTEM_442)).collect(toList());
        DateTime startDateSeason1 = new DateTime("2012-08-01");
        Season season1 = new Season(startDateSeason1, createTimeTable(teams), teams);
        league1.addSeason(season1);
        return season1;
    }

    private Map<String, League> createLeagueMap(Season season) {
        Map<String, League> nameToLeague = Maps.newHashMap();
        League league = new League("Bundesliga", Lists.newArrayList(), 0);
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
