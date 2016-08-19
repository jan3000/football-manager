package de.footballmanager.backend.comparator;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.TableEntry;
import de.footballmanager.backend.domain.Team;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class TeamValueComparatorTest {


    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";
    private Map<Team, TableEntry> teamTableEntryMap;
    private Team team1;
    private Team team2;
    private TableEntry tableEntry1;
    private TableEntry tableEntry2;


    @Before
    public void setUp() {
        teamTableEntryMap = Maps.newHashMap();
        team1 = new Team(TEAM_1);
        team2 = new Team(TEAM_2);
        tableEntry1 = new TableEntry(TEAM_1);
        tableEntry2 = new TableEntry(TEAM_2);
    }


    @Test
    @Parameters
    public void differentPoints(String name, int points1, int points2, int expectedCompare) {
        tableEntry1.setPoints(points1);
        tableEntry2.setPoints(points2);
        teamTableEntryMap.put(team1, tableEntry1);
        teamTableEntryMap.put(team2, tableEntry2);

        TeamValueComparator teamValueComparator = new TeamValueComparator(teamTableEntryMap);
        int compare = teamValueComparator.compare(team1, team2);
        assertThat(compare).isEqualTo(expectedCompare);
    }

    @Test
    @Parameters
    public void samePointsButDifferentGoalDifference(String name, int goals1, int goals2, int expectedCompare) {
        tableEntry1.setPoints(3);
        tableEntry1.setHomeGoals(goals1);
        tableEntry1.setAwayGoals(goals1);

        tableEntry2.setPoints(3);
        tableEntry2.setHomeGoals(goals2);
        tableEntry2.setAwayGoals(goals2);
        teamTableEntryMap.put(team1, tableEntry1);
        teamTableEntryMap.put(team2, tableEntry2);

        TeamValueComparator teamValueComparator = new TeamValueComparator(teamTableEntryMap);
        int compare = teamValueComparator.compare(team1, team2);
        assertThat(compare).isEqualTo(expectedCompare);
    }

    @Test
    @Parameters
    public void samePointsAndGoalDifferences(String name, int goals1, int goals2, int expectedCompare) {
        tableEntry1.setPoints(3);
        tableEntry1.setHomeGoals(goals1);
        tableEntry1.setAwayGoals(goals1);
        tableEntry1.setReceivedHomeGoals(goals1 - 5);
        tableEntry1.setReceivedAwayGoals(goals1 - 5);

        tableEntry2.setPoints(3);
        tableEntry2.setHomeGoals(goals2);
        tableEntry2.setAwayGoals(goals2);
        tableEntry2.setReceivedHomeGoals(goals2 - 5);
        tableEntry2.setReceivedAwayGoals(goals2 - 5);

        teamTableEntryMap.put(team1, tableEntry1);
        teamTableEntryMap.put(team2, tableEntry2);

        TeamValueComparator teamValueComparator = new TeamValueComparator(teamTableEntryMap);
        int compare = teamValueComparator.compare(team1, team2);
        assertThat(compare).isEqualTo(expectedCompare);
    }

    public Object[][] parametersForDifferentPoints() {
        return new Object[][]{{"team1 has more points", 3, 1, -1}, {"team2 has more points", 32, 45, 1}};
    }

    public Object[][] parametersForSamePointsButDifferentGoalDifference() {
        return new Object[][]{{"team1 has better goalDifference", 3, 1, -1},
                {"team2 has better goalDifference", 32, 45, 1}};
    }

    public Object[][] parametersForSamePointsAndGoalDifferences() {
        return new Object[][]{{"team1 has more goals", 3, 1, -1},
                {"team2 has more goals", 32, 45, 1}, {"team2 has more goals", 32, 32, -1}};
    }

}
