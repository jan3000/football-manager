package de.footballmanager.backend.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.KindOfGoal;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Map;

import static de.footballmanager.backend.util.TestUtil.TEAM_1;
import static de.footballmanager.backend.util.TestUtil.TEAM_2;
import static de.footballmanager.backend.util.TestUtil.buildMatch;
import static org.fest.assertions.Assertions.assertThat;

public class StatisticServiceTest {


    private TimeTable timeTable;
    private Team team1;
    private Team team2;
    private StatisticService statisticService;
    private Table table;

    @Before
    public void setUp() {
        statisticService = new StatisticService();
        table = new Table();

        team1 = new Team(TEAM_1);
        team2 = new Team(TEAM_2);
        timeTable = new TimeTable();
        Match match1 = buildMatch(TEAM_1, TEAM_2, 2, 2);
        match1.addGoal(new Goal(12, team1, new Player(), KindOfGoal.HEAD, new Result(1, 0)));
        match1.addGoal(new Goal(23, team2, new Player(), KindOfGoal.HEAD, new Result(1, 1)));
        match1.addGoal(new Goal(44, team2, new Player(), KindOfGoal.HEAD, new Result(1, 2)));
        match1.addGoal(new Goal(90, team1, new Player(), KindOfGoal.HEAD, new Result(2, 2)));
        MatchDay matchDay1 = new MatchDay(Lists.newArrayList(match1));
        Match match2 = buildMatch(TEAM_2, TEAM_1, 4, 1);
        match2.addGoal(new Goal(8, team2, new Player(), KindOfGoal.HEAD, new Result(1, 0)));
        match2.addGoal(new Goal(12, team1, new Player(), KindOfGoal.HEAD, new Result(1, 1)));
        match2.addGoal(new Goal(33, team2, new Player(), KindOfGoal.HEAD, new Result(2, 1)));
        match2.addGoal(new Goal(54, team2, new Player(), KindOfGoal.HEAD, new Result(3, 1)));
        match2.addGoal(new Goal(58, team2, new Player(), KindOfGoal.HEAD, new Result(4, 1)));
        MatchDay matchDay2 = new MatchDay(Lists.newArrayList(match2));
        timeTable.addMatchDays(Lists.newArrayList(matchDay1, matchDay2));
    }

    @Test
    public void getTeamStatisticsGoalsPerMinute() {
        TableEntry tableEntry = new TableEntry(team1.getName());
        tableEntry.setPlace(3);
        table.addEntry(tableEntry);

        // run
        TeamStatistic teamStatistic = statisticService.getGoalDistribution(timeTable, team1.getName(), table);

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

        Integer[] placementsInSeason = statisticService.getPlacementsInSeason(team1.getName(), 3, matchDayToTable);

        assertThat(placementsInSeason.length).isEqualTo(34);
        assertThat(placementsInSeason[0]).isEqualTo(3);
        assertThat(placementsInSeason[1]).isEqualTo(3);
        for (int i = 2; i < 34; i++) {
            assertThat(placementsInSeason[i]).isNull();
        }
    }

}
