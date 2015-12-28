package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static de.footballmanager.backend.util.TestUtil.*;
import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;

public class LeagueServiceTest {


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
        resultService.calculateNextMinute(eq((List) Lists.newArrayList(matchDay.getMatches().get(0))));
        expectLastCall().once();

        replay(resultService);
        ReflectionTestUtils.setField(leagueService, "timeTable", timeTable);
        ReflectionTestUtils.setField(leagueService, "resultService", resultService);
        return resultService;
    }

    @Test
    public void getCurrentTable() {
        // prepare
        setUpTimeTable();

        // run
        Table currentTable = leagueService.getCurrentTable();

        // assert
        assertThat(currentTable).isNotNull();
        assertThat(currentTable.getEntries()).hasSize(3);

        TableEntry tableEntry1 = currentTable.getEntries().get(2);
        assertTable(tableEntry1, TEAM_1, 3, 1);
        assertGoals(tableEntry1, 3, 4, 3, 7);
        assertHomeStatistics(tableEntry1, 0, 1, 1);
        assertAwayStatistics(tableEntry1, 0, 0, 2);
        assertTotalStatistics(tableEntry1, 0, 1, 3);

        TableEntry tableEntry2 = currentTable.getEntries().get(0);
        assertTable(tableEntry2, TEAM_2, 1, 10);
        assertGoals(tableEntry2, 7, 3, 4, 2);
        assertHomeStatistics(tableEntry2, 2, 0, 0);
        assertAwayStatistics(tableEntry2, 1, 1, 0);
        assertTotalStatistics(tableEntry2, 3, 1, 0);

        TableEntry tableEntry3 = currentTable.getEntries().get(1);
        assertTable(tableEntry3, TEAM_3, 2, 6);
        assertGoals(tableEntry3, 3, 4, 4, 4);
        assertHomeStatistics(tableEntry3, 1, 0, 1);
        assertAwayStatistics(tableEntry3, 1, 0, 1);
        assertTotalStatistics(tableEntry3, 2, 0, 2);
    }

    @Test
    public void getTableOfFirstDay() {
        // prepare
        setUpTimeTable();

        // run
        Table currentTable = leagueService.getTable(1);

        // assert
        assertThat(currentTable).isNotNull();
        assertThat(currentTable.getEntries()).hasSize(2);

        TableEntry tableEntry1 = currentTable.getEntries().get(0);
        assertTable(tableEntry1, TEAM_2, 1, 3);
        assertGoals(tableEntry1, 3, 2, 0, 0);
        assertHomeStatistics(tableEntry1, 1, 0, 0);
        assertAwayStatistics(tableEntry1, 0, 0, 0);
        assertTotalStatistics(tableEntry1, 1, 0, 0);

        TableEntry tableEntry2 = currentTable.getEntries().get(1);
        assertTable(tableEntry2, TEAM_3, 2, 0);
        assertGoals(tableEntry2, 0, 0, 2, 3);
        assertHomeStatistics(tableEntry2, 0, 0, 0);
        assertAwayStatistics(tableEntry2, 0, 0, 1);
        assertTotalStatistics(tableEntry2, 0, 0, 1);
    }

    private void setUpTimeTable() {
        TimeTable timeTable = new TimeTable();
        MatchDay matchDay1 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_2, TEAM_3, 3, 2)));
        MatchDay matchDay2 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_3, TEAM_1, 3, 2)));
        MatchDay matchDay3 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_1, TEAM_2, 2, 2)));
        MatchDay matchDay4 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_3, TEAM_2, 0, 2)));
        MatchDay matchDay5 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_1, TEAM_3, 1, 2)));
        MatchDay matchDay6 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_2, TEAM_1, 4, 1)));
        timeTable.addMatchDays(Lists.newArrayList(matchDay1, matchDay2, matchDay3, matchDay4, matchDay5, matchDay6));
        timeTable.setCurrentMatchDay(6);
        ReflectionTestUtils.setField(leagueService, "timeTable", timeTable);
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
        assertThat(tableEntry.getTeam().getName()).isEqualTo(team);
        assertThat(tableEntry.getPlace()).isEqualTo(place);
        assertThat(tableEntry.getPoints()).isEqualTo(points);
    }



}
