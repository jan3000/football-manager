package de.footballmanager.backend.service;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.LeagueTestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Maps;

import de.footballmanager.backend.comparator.ResultComparator;

import static org.fest.assertions.Assertions.assertThat;

public class ResultServiceTest {

    private ResultService resultService;

    @Before
    public void setUp() {
        resultService = new ResultService();
    }

    @Test
    public void getScorer() {
        Team team = new Team("Hamburger SV");
        Player player = createPlayer("Furtok", Position.STRIKER, 88);
        team.getPlayers().add(player);
        Player scorer = resultService.getScorer(team);
        assertThat(scorer).isNotNull();
    }

    private Player createPlayer(String lastName, Position position, int strength) {
        Player player = new Player();
        player.setFirstname("Jan");
        player.setLastname(lastName);
        player.setPosition(position);
        player.setStrength(strength);
        return player;
    }

    @Test
    @Ignore
    public void calculateResult() throws Exception {
        TrialAndErrorTimeTableService timeTableService = new TrialAndErrorTimeTableService();
        List<Team> teams = LeagueTestUtil.getLeagueTeams();

        Map<Result, Integer> resultToCountMap = Maps.newHashMap();
        for (int i = 0; i < 10; i++) {
            TimeTable timeTable = timeTableService.createTimeTable(teams);
            League league = new League(teams);
            league.setTimeTable(timeTable);
            for (MatchDay matchDay : timeTable.getAllMatchDays()) {
                for (Match match : matchDay.getMatches()) {
                    resultService.calculateResult(match);
                }
            }
            System.out.println(timeTable.print());
            assertAllMatchesHaveEnded(timeTable);

            // testSorting of end table according to the teams strength
            // test distribution of match results
            fillAndPrintMap(timeTable, resultToCountMap);
        }
        System.out.println("\n");
        TreeMap<Result, Integer> resultToCountTreeMap = Maps.newTreeMap(new ResultComparator(resultToCountMap));
        resultToCountTreeMap.putAll(resultToCountMap);
        printMap(resultToCountTreeMap);

    }

    private Map<Result, Integer> fillAndPrintMap(final TimeTable timeTable, final Map<Result, Integer> resultToCountMap) {
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                Result result = match.getResult();
                if (!resultToCountMap.containsKey(result)) {
                    resultToCountMap.put(result, 1);
                } else {
                    resultToCountMap.put(result, resultToCountMap.get(result) + 1);
                }
            }
        }

        printMap(resultToCountMap);
        return resultToCountMap;
    }

    private void printMap(final Map<Result, Integer> resultToCountMap) {
        for (Entry<Result, Integer> resultToCount : resultToCountMap.entrySet()) {
            System.out.println(String.format("resultToCount: %s\t %s", resultToCount.getKey().print(),
                    resultToCount.getValue()));
        }
    }

    private void assertAllMatchesHaveEnded(final TimeTable timeTable) {
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                org.junit.Assert.assertTrue(match.isFinished());
            }
        }
    }

}
