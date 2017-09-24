package de.footballmanager.backend.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.*;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.statistics.ScorerStatistic;
import de.footballmanager.backend.domain.statistics.TeamStatistic;
import de.footballmanager.backend.enumeration.KindOfGoal;
import de.footballmanager.backend.enumeration.PlayingSystem;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.footballmanager.backend.util.TestUtil.*;
import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;

public class StatisticServiceTest {

    private TimeTable timeTable;
    private Team team1;
    private Team team2;
    private StatisticService statisticService;
    private Table table;
    private Player scorer1;
    private Player scorer2;
    private Player scorer3;
    private MatchService matchService;
    private Match match1;
    private Match match2;

    private Player buildPlayer(String firstName, String lastName) {
        return new Player.Builder(firstName, lastName).build();
    }

    @Before
    public void setUp() {
        statisticService = new StatisticService();
        matchService = createMock(MatchService.class);
        ReflectionTestUtils.setField(statisticService, "matchService", matchService);
        table = new Table();

        team1 = new Team(TEAM_NAME_1);
        team2 = new Team(TEAM_NAME_2);
        match1 = createFinishedMatch(TEAM_NAME_1, TEAM_NAME_2, 2, 2, PlayingSystem.SYSTEM_4_4_2, PlayingSystem.SYSTEM_4_4_2);
        match1.setStarted(true);
        match1.setFinished(true);
        scorer1 = buildPlayer("John", "Dumbo");
        scorer2 = buildPlayer("Jeff", "Patterns");
        scorer3 = buildPlayer("Jordy", "Madrid");
        match1.addGoal(new Goal(12, team1.getName(), scorer1, KindOfGoal.HEAD, new Result(1, 0)));
        match1.addGoal(new Goal(23, team2.getName(), scorer2, KindOfGoal.HEAD, new Result(1, 1)));
        match1.addGoal(new Goal(44, team2.getName(), scorer2, KindOfGoal.HEAD, new Result(1, 2)));
        match1.addGoal(new Goal(90, team1.getName(), scorer1, KindOfGoal.HEAD, new Result(2, 2)));
        MatchDay matchDay1 = new MatchDay(Lists.newArrayList(match1));
        match2 = createFinishedMatch(TEAM_NAME_2, TEAM_NAME_1, 4, 1, PlayingSystem.SYSTEM_4_4_2, PlayingSystem.SYSTEM_4_4_2);
        match2.addGoal(new Goal(8, team2.getName(), scorer2, KindOfGoal.HEAD, new Result(1, 0)));
        match2.addGoal(new Goal(12, team1.getName(), scorer1, KindOfGoal.HEAD, new Result(1, 1)));
        match2.addGoal(new Goal(33, team2.getName(), scorer3, KindOfGoal.HEAD, new Result(2, 1)));
        match2.addGoal(new Goal(54, team2.getName(), scorer2, KindOfGoal.HEAD, new Result(3, 1)));
        match2.addGoal(new Goal(58, team2.getName(), scorer2, KindOfGoal.HEAD, new Result(4, 1)));
        match2.setStarted(true);
        match2.setFinished(true);
        MatchDay matchDay2 = new MatchDay(Lists.newArrayList(match2));

        timeTable = new TimeTable(Lists.newArrayList(matchDay1, matchDay2));
    }

    @Test
    public void getTeamStatisticsGoalsPerMinute() {
        TableEntry tableEntry = new TableEntry(team1.getName());
        tableEntry.setPlace(3);
        table.addEntry(tableEntry);

        // run
        TeamStatistic teamStatistic = statisticService.getTeamStatistics(timeTable, team1.getName(), table,
                Maps.newHashMap());

        // assert
        assertThat(teamStatistic.getCurrentTableEntry()).isNotNull();
        assertThat(teamStatistic.getCurrentTableEntry().getTeam()).isEqualTo(team1.getName());


        assertThat(teamStatistic).isNotNull();
        Integer[] expectedHomeGoalsTeam1 = new Integer[90];
        Arrays.fill(expectedHomeGoalsTeam1, 0);
        expectedHomeGoalsTeam1[11] = 1;
        expectedHomeGoalsTeam1[89] = 1;
        Integer[] expectedAwayGoalsTeam1 = new Integer[90];
        Arrays.fill(expectedAwayGoalsTeam1, 0);
        expectedAwayGoalsTeam1[11] = 1;
        Integer[] expectedTotalGoalsTeam1 = new Integer[90];
        Arrays.fill(expectedTotalGoalsTeam1, 0);
        expectedTotalGoalsTeam1[11] = 2;
        expectedTotalGoalsTeam1[89] = 1;

        Integer[] expectedReceivedTotalGoalsTeam1 = new Integer[90];
        Arrays.fill(expectedReceivedTotalGoalsTeam1, 0);
        expectedReceivedTotalGoalsTeam1[7] = 1;
        expectedReceivedTotalGoalsTeam1[22] = 1;
        expectedReceivedTotalGoalsTeam1[32] = 1;
        expectedReceivedTotalGoalsTeam1[43] = 1;
        expectedReceivedTotalGoalsTeam1[53] = 1;
        expectedReceivedTotalGoalsTeam1[57] = 1;
        Integer[] expectedReceivedHomeGoalsTeam1 = new Integer[90];
        Arrays.fill(expectedReceivedHomeGoalsTeam1, 0);
        expectedReceivedHomeGoalsTeam1[22] = 1;
        expectedReceivedHomeGoalsTeam1[43] = 1;
        Integer[] expectedReceivedAwayGoalsTeam1 = new Integer[90];
        Arrays.fill(expectedReceivedAwayGoalsTeam1, 0);
        expectedReceivedAwayGoalsTeam1[7] = 1;
        expectedReceivedAwayGoalsTeam1[32] = 1;
        expectedReceivedAwayGoalsTeam1[53] = 1;
        expectedReceivedAwayGoalsTeam1[57] = 1;

        assertThat(teamStatistic.getHomeGoals()).isEqualTo(expectedHomeGoalsTeam1);
        assertThat(teamStatistic.getAwayGoals()).isEqualTo(expectedAwayGoalsTeam1);
        assertThat(teamStatistic.getTotalGoals()).isEqualTo(expectedTotalGoalsTeam1);

        assertThat(teamStatistic.getReceivedTotalGoals()).isEqualTo(expectedReceivedTotalGoalsTeam1);
        assertThat(teamStatistic.getReceivedHomeGoals()).isEqualTo(expectedReceivedHomeGoalsTeam1);
        assertThat(teamStatistic.getReceivedAwayGoals()).isEqualTo(expectedReceivedAwayGoalsTeam1);
    }

    @Test
    public void getPlacements() {
        TableEntry tableEntry = new TableEntry(team1.getName());
        tableEntry.setPlace(3);
        table.addEntry(tableEntry);
        Map<Integer, Table> matchDayToTable = Maps.newHashMap();
        matchDayToTable.put(1, table);
        matchDayToTable.put(2, table);
        timeTable.incrementCurrentMatchDay();
        timeTable.setClosed();

        Integer[] placementsInSeason = statisticService.getPlacementsInSeason(team1.getName(), matchDayToTable, timeTable);

        int numberOfMatchDays = timeTable.getNumberOfMatchDays();
        assertThat(placementsInSeason.length).isEqualTo(numberOfMatchDays);
        assertThat(placementsInSeason[0]).isEqualTo(3);
        assertThat(placementsInSeason[1]).isEqualTo(3);
    }

    @Test
    public void getScorers() {
        // given
        expect(matchService.getMatchOfTeam(timeTable.getMatchDay(1), team1.getName())).andReturn(match1);
        expect(matchService.getMatchOfTeam(timeTable.getMatchDay(2), team1.getName())).andReturn(match2);
        expect(matchService.getMatchOfTeam(timeTable.getMatchDay(1), team2.getName())).andReturn(match1);
        expect(matchService.getMatchOfTeam(timeTable.getMatchDay(2), team2.getName())).andReturn(match2);
        replay(matchService);

        // when
        List<ScorerStatistic> scorerTable = statisticService.getScorerTable(Lists.newArrayList(team1, team2), timeTable);

        // then
        assertThat(scorerTable.size()).isEqualTo(3);
        assertThat(scorerTable.get(0).getPlayer()).isEqualTo(scorer2.getFullname());
        assertThat(scorerTable.get(0).getGoals()).isEqualTo(5);
        assertThat(scorerTable.get(0).getTeam()).isEqualTo(team2.getName());

        assertThat(scorerTable.get(1).getPlayer()).isEqualTo(scorer1.getFullname());
        assertThat(scorerTable.get(1).getGoals()).isEqualTo(3);
        assertThat(scorerTable.get(1).getTeam()).isEqualTo(team1.getName());

        assertThat(scorerTable.get(2).getPlayer()).isEqualTo(scorer3.getFullname());
        assertThat(scorerTable.get(2).getGoals()).isEqualTo(1);
        assertThat(scorerTable.get(2).getTeam()).isEqualTo(team2.getName());
        verify(matchService);
    }

}
