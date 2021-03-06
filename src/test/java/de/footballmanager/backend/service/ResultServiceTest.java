package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.*;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.LeagueTestUtil;
import de.footballmanager.backend.util.PrintUtil;
import de.footballmanager.backend.util.TestUtil;
import jersey.repackaged.com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_4_4_2;
import static de.footballmanager.backend.util.TestUtil.*;
import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

public class ResultServiceTest {

    private ResultService resultService;
    private MatchService matchService;
    private ClubService clubService;

    @Before
    public void setUp() {
        resultService = new ResultService();
        matchService = createMock(MatchService.class);
        clubService = createMock(ClubService.class);
        ReflectionTestUtils.setField(resultService, "clubService", clubService);
        ReflectionTestUtils.setField(resultService, "matchService", matchService);
    }

    @Test
    public void addHomeGoal() {
        // given
        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Match match = createRunningMatch(homeTeam, createTeam(TEAM_NAME_1, SYSTEM_4_4_2));
        startMatch(match);

        expect(clubService.getTeam(homeTeam.getName())).andReturn(homeTeam);
        matchService.increaseGoalsHomeTeam(eq(match), (Goal) anyObject());
        expectLastCall();

        replay(clubService, matchService);
        assertEquals(0, match.getGoals().size());

        // when
        int minute = 12;
        resultService.addHomeGoal(match, minute);

        // then
        verify(clubService, matchService);
    }

    private void startMatch(Match match) {
        match.setStarted(true);
    }

    private void finishMatch(Match match) {
        IntStream.range(1, MatchService.MINUTES_OF_GAME).forEach(i -> matchService.increaseMinute(match));
    }

    @Test
    public void addGuestGoal() {
        // given
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match match = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), guestTeam);
        startMatch(match);

        expect(clubService.getTeam(guestTeam.getName())).andReturn(guestTeam);
        matchService.increaseGoalsGuestTeam(eq(match), (Goal) anyObject());
        expectLastCall();

        replay(clubService, matchService);
        assertEquals(0, match.getGoals().size());

        // when
        int minute = 12;
        resultService.addGuestGoal(match, minute);

        // then
        verify(clubService, matchService);
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
        // given
        StrengthService strengthService = mockStrengthService();
        List<Match> matches = Lists.newArrayList();
        Match match = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), createTeam(TEAM_NAME_1, SYSTEM_4_4_2));
        startMatch(match);
        matches.add(match);
        matchService.increaseMinute(match);
        expectLastCall().once();
        assertEquals(1, match.getMinute());

        replay(matchService);

        // when
        resultService.calculateNextMinute(matches);

        // then
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertFalse(match.isFinished());
        verify(strengthService, matchService);
    }

    @Test
    @Ignore("This is now done in MatchService")
    public void calculateNextMinuteFinished() {

        StrengthService strengthService = mockStrengthService();

        List<Match> matches = Lists.newArrayList();
        Match match = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), createTeam(TEAM_NAME_1, SYSTEM_4_4_2));
        IntStream.range(1, 89).forEach(i -> matchService.increaseMinute(match));
        matches.add(match);
        assertEquals(89, match.getMinute());
        resultService.calculateNextMinute(matches);
        assertEquals(90, match.getMinute());
        assertTrue(match.isFinished());

        verify(strengthService);
    }

    private StrengthService mockStrengthService() {
        StrengthService strengthService = createMock(StrengthService.class);
        expect(strengthService.getStrength(anyObject())).andReturn(100).times(2);

        replay(strengthService);
        ReflectionTestUtils.setField(resultService, "strengthService", strengthService);
        return strengthService;
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
        valueToPlayer.put(key1, createPlayer("Wood", Position.CENTRAL_DEFENSIVE_MIDFIELDER, 12));
        valueToPlayer.put(40, createPlayer("Water", Position.CENTRAL_DEFENSIVE_MIDFIELDER, 12));
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
        Player player1 = createPlayer("Water", Position.CENTRAL_STRIKER, 88);
        Player player2 = createPlayer("Wood", Position.CENTRAL_STRIKER, 87);
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
    @Ignore("fix and move this later")
    public void calculateResult() throws Exception {
        TrialAndErrorTimeTableService timeTableService = new TrialAndErrorTimeTableService();
        List<Team> teams = LeagueTestUtil.getLeagueTeams();

        Map<Result, Integer> resultToCountMap = Maps.newHashMap();
        for (int i = 0; i < 10; i++) {
            TimeTable timeTable = timeTableService.createTimeTable(teams, new DateTime());
            for (MatchDay matchDay : timeTable.getAllMatchDays()) {
                for (Match match : matchDay.getMatches()) {
                    resultService.calculateResult(match);
                }
            }
            System.out.println(PrintUtil.print(timeTable));
            assertAllMatchesHaveEnded(timeTable);

            // testSorting of end table according to the teams strength
            // test distribution of match results
            fillAndPrintMap(resultToCountMap, timeTable.getAllMatchDays());
        }
        System.out.println("\n");
        TreeMap<Result, Integer> resultToCountTreeMap = Maps.newTreeMap(new ResultComparator(resultToCountMap));
        resultToCountTreeMap.putAll(resultToCountMap);
        printMap(resultToCountTreeMap);

    }


    //TODO go on here with testing the resultService
    @Test
    @Ignore("maybe thats an IT?")
    public void bulkResultTest() {
        StrengthService strengthService = createMock(StrengthService.class);
        expect(strengthService.getStrength(anyObject())).andReturn(100).anyTimes();
        Team team1 = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        Team team2 = TestUtil.createTeam(TEAM_NAME_2, PlayingSystem.SYSTEM_4_4_2);

        expect(clubService.getTeam(TEAM_NAME_1)).andReturn(team1).anyTimes();
        expect(clubService.getTeam(TEAM_NAME_2)).andReturn(team2).anyTimes();

        replay(strengthService, clubService);
        ReflectionTestUtils.setField(resultService, "strengthService", strengthService);

        List<Match> matches = Lists.newArrayList();
        IntStream.rangeClosed(1, 100).forEach(i -> {
            matches.addAll(runCompleteMatches(team1, team2));
//            System.out.println(matches.get(0).getResult().print());
        });
        Map<Result, Integer> resultToCountMap = Maps.newHashMap();
        createResultToCountMap(resultToCountMap, matches);
        printMap(resultToCountMap);
        verify(strengthService);
    }

    private List<Match> runCompleteMatches(Team team1, Team team2) {
        List<Match> matches = Lists.newArrayList(createMatch(team1, team2));
        matches.forEach(m -> m.setStarted(true));
        IntStream.range(1, 90).forEach(i -> {
            resultService.calculateNextMinute(matches);
        });
        return matches;

    }


    private Map<Result, Integer> fillAndPrintMap(final Map<Result, Integer> resultToCountMap, List<MatchDay> matchDays) {
        for (MatchDay matchDay : matchDays) {
            createResultToCountMap(resultToCountMap, matchDay.getMatches());
        }

        printMap(resultToCountMap);
        return resultToCountMap;
    }

    private void createResultToCountMap(Map<Result, Integer> resultToCountMap, List<Match> matches) {
        for (Match match : matches) {
            Result result = match.getResult();
            if (!resultToCountMap.containsKey(result)) {
                resultToCountMap.put(result, 1);
            } else {
                resultToCountMap.put(result, resultToCountMap.get(result) + 1);
            }
        }
    }

    private void printMap(final Map<Result, Integer> resultToCountMap) {
        TreeMap<Result, Integer> resultToCountMapSorted = Maps.newTreeMap(new ResultComparator(resultToCountMap));
        resultToCountMapSorted.putAll(resultToCountMap);

        int totalAmountOfResults = resultToCountMapSorted.values().stream().reduce(0, Integer::sum);
        System.out.println("totalAmountOfResults: " + totalAmountOfResults);
        for (Entry<Result, Integer> resultToCount : resultToCountMapSorted.entrySet()) {
            System.out.println(String.format("resultToCount: %s\t %s\t %s", resultToCount.getKey().print(),
                    resultToCount.getValue(), BigDecimal.valueOf(resultToCount.getValue() * 100).divide(BigDecimal.valueOf(totalAmountOfResults))));
        }
    }

    private void assertAllMatchesHaveEnded(final TimeTable timeTable) {
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                org.junit.Assert.assertTrue(match.isFinished());
            }
        }
    }


    private class ResultComparator implements Comparator<Result> {

        private final Map<Result, Integer> map;

        ResultComparator(final Map<Result, Integer> map) {
            super();
            this.map = map;
        }

        public int compare(final Result result1, final Result result2) {
            if (map.get(result1) < map.get(result2)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
