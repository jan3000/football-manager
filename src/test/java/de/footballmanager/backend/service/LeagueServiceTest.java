package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;

public class LeagueServiceTest {

    private static final Team TEAM_1 = new Team("Team1");
    private static final Team TEAM_2 = new Team("Team2");
    private static final Team TEAM_3 = new Team("Team3");
    private static final Team TEAM_4 = new Team("Team4");

    private LeagueService leagueService;

    @Before
    public void setUp() {
        leagueService = new LeagueService();
    }

    @Test
    public void runNextMinute() {
        // prepare
        MatchDay matchDay = new MatchDay();
        Match match = new Match();
        matchDay.getMatches().add(match);
        ResultService resultService = setUpResultService(matchDay);

        // run
        MatchDay returnedMatchDay = leagueService.runNextMinute();

        // assert
        assertThat(returnedMatchDay).isNotNull();
        assertThat(leagueService.getCurrentMatchDay()).isEqualTo(1);

        verify(resultService);
    }

    @Test
    public void runNextMinuteAllMatchesFinished() {
        // prepare
        MatchDay matchDay = new MatchDay();
        Match match = new Match();
        match.setFinished(true);
        matchDay.getMatches().add(match);
        ResultService resultService = setUpResultService(matchDay);

        // run
        leagueService.runNextMinute();

        // assert
        assertThat(leagueService.getCurrentMatchDay()).isEqualTo(2);

        verify(resultService);
    }

    private ResultService setUpResultService(MatchDay matchDay) {
        TimeTable timeTable = new TimeTable();
        timeTable.addMatchDays(Lists.newArrayList(matchDay));

        ResultService resultService = createMock(ResultService.class);
        resultService.calculateNextMinute(eq((List)Lists.newArrayList(matchDay.getMatches().get(0))));
        expectLastCall().once();

        replay(resultService);
        ReflectionTestUtils.setField(leagueService, "timeTable", timeTable);
        ReflectionTestUtils.setField(leagueService, "resultService", resultService);
        return resultService;
    }

    @Test
    public void getCurrentTable() {
        // prepare
        TimeTable timeTable = new TimeTable();
        Match match1 = buildMatch(TEAM_1, TEAM_2, 4, 1);
        Match match2 = buildMatch(TEAM_2, TEAM_1, 3, 2);
        MatchDay matchDay = new MatchDay(Lists.newArrayList(match1));
        MatchDay matchDay2 = new MatchDay(Lists.newArrayList(match2));
        timeTable.addMatchDays(Lists.newArrayList(matchDay, matchDay2));

        ReflectionTestUtils.setField(leagueService, "timeTable", timeTable);

        // run
        Table currentTable = leagueService.getCurrentTable();

        // assert
        assertThat(currentTable).isNotNull();
        assertThat(currentTable.getEntries()).hasSize(2);

        TableEntry tableEntry1 = currentTable.getEntries().get(0);
        assertTable(tableEntry1, TEAM_1, 1, 3);
        assertGoals(tableEntry1, 4, 1, 2, 3);
        assertHomeStatistics(tableEntry1, 1, 0, 0);
        assertAwayStatistics(tableEntry1, 0, 0, 1);
        assertTotalStatistics(tableEntry1, 1, 0, 1);

        TableEntry tableEntry2 = currentTable.getEntries().get(1);
        assertTable(tableEntry2, TEAM_2, 2, 3);
        assertGoals(tableEntry2, 3, 2, 1, 4);
        assertHomeStatistics(tableEntry2, 1, 0, 0);
        assertAwayStatistics(tableEntry2, 0, 0, 1);
        assertTotalStatistics(tableEntry2, 1, 0, 1);
    }
    @Test
    public void getCurrentTableSameGoalDifference() {
        // prepare
        TimeTable timeTable = new TimeTable();
        MatchDay matchDay = new MatchDay(Lists.newArrayList(buildMatch(TEAM_1, TEAM_2, 2, 1)));
        MatchDay matchDay2 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_2, TEAM_1, 3, 2)));
        timeTable.addMatchDays(Lists.newArrayList(matchDay, matchDay2));

        ReflectionTestUtils.setField(leagueService, "timeTable", timeTable);

        // run
        Table currentTable = leagueService.getCurrentTable();

        // assert
        assertThat(currentTable.getEntries()).hasSize(2);
        assertTable(currentTable.getEntries().get(0), TEAM_1, 2, 3);
        assertTable(currentTable.getEntries().get(1), TEAM_2, 1, 3);
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

    private void assertTable(TableEntry tableEntry, Team team, int place, int points) {
        assertThat(tableEntry.getTeam()).isEqualTo(team);
        assertThat(tableEntry.getPlace()).isEqualTo(place);
        assertThat(tableEntry.getPoints()).isEqualTo(points);
    }

    private Match buildMatch(Team team1, Team team2, int homeGoals, int guestGoals) {
        Match match = new Match();
        match.setFinished(true);
        match.setHomeTeam(team1);
        match.setGuestTeam(team2);
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

}
