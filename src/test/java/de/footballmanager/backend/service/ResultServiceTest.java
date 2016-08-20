package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.LeagueTestUtil;
import de.footballmanager.backend.util.TestUtil;
import jersey.repackaged.com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

public class ResultServiceTest {

    private ResultService resultService;

    @Before
    public void setUp() {
        resultService = new ResultService();
    }

    @Test
    public void addHomeGoal() {
        // given
        Match match = createMatch();
        assertEquals(0, match.getGoals().size());

        // when
        int minute = 12;
        resultService.addHomeGoal(match, minute);

        // then
        assertGoalIsMade(match, minute, 1, 0);
    }

    @Test
    public void addGuestGoal() {
        // given
        Match match = createMatch();
        assertEquals(0, match.getGoals().size());

        // when
        int minute = 12;
        resultService.addGuestGoal(match, minute);

        // then
        assertGoalIsMade(match, minute, 0, 1);
    }

    private void assertGoalIsMade(Match match, int minute, int expectedHomeGoals, int expectedGuastGoals) {
        assertNotNull(match);
        assertEquals(1, match.getGoals().size());
        final Goal goal = match.getGoals().get(0);
        assertEquals(expectedHomeGoals, goal.getNewResult().getHomeGoals());
        assertEquals(expectedGuastGoals, goal.getNewResult().getGuestGoals());
        assertEquals(minute, goal.getMinute());
        assertNotNull(goal.getTeam());
        assertNotNull(goal.getScorer());
    }

    @Test
    public void calculateNextMinuteAddsOneMinuteToMatch() {
        List<Match> matches = Lists.newArrayList();
        Match match = createMatch();
        matches.add(match);
        assertEquals(1, match.getMinute());
        resultService.calculateNextMinute(matches);
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals(2, match.getMinute());
        assertFalse(match.isFinished());
    }

    @Test
    public void calculateNextMinuteFinished() {
        List<Match> matches = Lists.newArrayList();
        Match match = createMatch();
        IntStream.range(1, 89).forEach(i -> match.increaseMinute());
        matches.add(match);
        assertEquals(89, match.getMinute());
        resultService.calculateNextMinute(matches);
        assertEquals(90, match.getMinute());
        assertTrue(match.isFinished());
    }

    private Match createMatch() {
        Match match = new Match();
        match.setHomeTeam(TestUtil.createTeam("Homie"));
        match.setGuestTeam(TestUtil.createTeam("Guesty"));
        return match;
    }

    @Test
    public void getMaxKey() {
        assertMaxKey(20);
    }

    @Test
    public void getMaxKeyEqualKeys() {
        assertMaxKey(40);
    }

    private void assertMaxKey(int key1) {
        Map<Integer, Player> valueToPlayer = Maps.newHashMap();
        valueToPlayer.put(key1, createPlayer("Wood", Position.DEFENSIVE_MIDFIELDER, 12));
        valueToPlayer.put(40, createPlayer("Water", Position.DEFENSIVE_MIDFIELDER, 12));
        Integer maxKey = resultService.getMaxKey(valueToPlayer);
        assertNotNull(maxKey);
        assertEquals(Integer.valueOf(40), maxKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMaxKeyEmptyMap() {
        resultService.getMaxKey(Maps.newHashMap());
    }


    @Test(expected = IllegalArgumentException.class)
    public void getScorerNoPlayersSet() {
        Team team = new Team("Hamburger SV");
        resultService.getScorer(team);
    }

    @Test
    public void getScorer() {
        Team team = new Team("Hamburger SV");
        Player player1 = createPlayer("Water", Position.STRIKER, 88);
        Player player2 = createPlayer("Wood", Position.STRIKER, 87);
        team.getPlayers().add(player1);
        team.getPlayers().add(player2);
        Player scorer = resultService.getScorer(team);
        assertThat(scorer).isIn(Sets.newHashSet(player1, player2));
    }

    private Player createPlayer(String lastName, Position position, int strength) {
        Player player = new Player.Builder("Jan", lastName).setPosition(position).build();
        player.setStrength(strength);
        return player;
    }


    // -------------------------------------------------------------------------------
    // TODO: where to put this
    // -------------------------------------------------------------------------------
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


    public class ResultComparator implements Comparator<Result> {

        private final Map<Result, Integer> map;

        public ResultComparator(final Map<Result, Integer> map) {
            super();
            this.map = map;
        }

        public int compare(final Result result1, final Result result2) {
            if (map.get(result1) > map.get(result2)) {
                return 1;
            } else {
                return -1;
            }
        }
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
