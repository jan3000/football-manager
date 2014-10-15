package de.footballmanager.backend;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.footballmanager.backend.domain.*;
import org.junit.Test;

import com.google.common.collect.Maps;

import de.footballmanager.backend.comparator.ResultComparator;
import de.footballmanager.backend.engine.ResultEngine;
import de.footballmanager.backend.engine.TrialAndErrorTimeTableEngine;

public class ResultEngineTest {

    private final int homeWin = 0;
    private final int draw = 0;
    private final int guestWin = 0;
    private final int homeGoals = 0;
    private final int guestGoals = 0;
    private final int goalLessGames = 0;

    @Test
    public void calculateResult() throws Exception {
        List<Team> teams = LeagueTestUtil.getLeagueTeams();

        Map<Result, Integer> resultToCountMap = Maps.newHashMap();
        for (int i = 0; i < 10; i++) {
//            TimeTable timeTable = TrialAndErrorTimeTableEngine.createTimeTable(teams);
            League league = new League(teams);
            TimeTable timeTable = league.getTimeTable();
            for (MatchDay matchDay : timeTable.getAllMatchDays()) {
                for (Match match : matchDay.getMatches()) {
                    ResultEngine.calculateResult(match);
                }
            }
            System.out.println(timeTable.print());
            assertAllMatchesHaveEnded(timeTable);

            // testSorting of end table according to the teams strength
            // test distribution of match results
            System.out.println(league.printCurrentTable());
            fillAndPrintMap(timeTable, resultToCountMap);
        }
        System.out.println("\n");
        TreeMap<Result, Integer> resultToCountTreeMap = Maps.newTreeMap(new ResultComparator(resultToCountMap));
        resultToCountTreeMap.putAll(resultToCountMap);
        printMap(resultToCountTreeMap);

        // Map<Result, Double> resultToPercentMap = Maps.new
        for (Entry<Result, Integer> resultToCount : resultToCountTreeMap.entrySet()) {

        }
    }

    private Map<Result, Integer> fillAndPrintMap(final TimeTable timeTable, final Map<Result, Integer> resultToCountMap) {
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                Result result = match.getResult();
                // System.out.println("result: " + result.print());
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
                assertTrue(match.hasEnded());
            }
        }
    }

}
