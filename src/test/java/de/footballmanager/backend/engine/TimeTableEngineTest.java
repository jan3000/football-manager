package de.footballmanager.backend.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.footballmanager.backend.LeagueTestUtil;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;

public class TimeTableEngineTest {

    private static final String TEAM4 = "team4";
    private static final String TEAM3 = "team3";
    private static final String TEAM1 = "team1";
    private static final String TEAM2 = "team2";
    private static final int NUMBER_OF_TEAMS = 18;
    private static final int NUMBER_OF_MATCH_DAYS_ONE_ROUND = NUMBER_OF_TEAMS - 1;
    private static final int NUMBER_OF_MATCH_DAYS = NUMBER_OF_MATCH_DAYS_ONE_ROUND * 2;
    private static final int NUMBER_OF_MATCHES_IN_FIRST_ROUND = NUMBER_OF_MATCH_DAYS_ONE_ROUND * (NUMBER_OF_TEAMS / 2);

    List<Team> teams = Lists.newArrayList();

    @Before
    public void setUpTeams() {
        for (int i = 1; i <= NUMBER_OF_TEAMS; i++) {
            teams.add(LeagueTestUtil.createTeam(String.format("Team%s", i)));
        }
    }

    @Test
    public void getNumberOfMatchDaysOfOneRound() throws Exception {
        int numberOfMatchDaysOfOneRound = TrialAndErrorTimeTableEngine.getNumberOfMatchDaysOfOneRound(teams);
        assertEquals(NUMBER_OF_MATCH_DAYS_ONE_ROUND, numberOfMatchDaysOfOneRound);
    }

    @Test
    public void getTotalNumberOfMatchDays() throws Exception {
        int totalNumberOfMatchDays = TrialAndErrorTimeTableEngine.getTotalNumberOfMatchDays(teams);
        assertEquals(NUMBER_OF_MATCH_DAYS, totalNumberOfMatchDays);
    }

    @Test
    public void buildAllMatchesOfFirstRound() {
        List<Match> matchesOfFirstRound = TrialAndErrorTimeTableEngine.buildAllMatchesOfFirstRound(teams);

        assertNotNull(matchesOfFirstRound);
        assertEquals(NUMBER_OF_MATCHES_IN_FIRST_ROUND, matchesOfFirstRound.size());
        assertAllTeamsHaveSameSumOfMatches(matchesOfFirstRound);
    }

    // -------------------------------------------------------------
    // isTeamNotInMatchDay
    // -------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void isTeamNotInMatchDayMatchDayNull() throws Exception {
        TrialAndErrorTimeTableEngine.isTeamNotInMatchDay(null, new Team(TEAM1));
    }

    @Test
    public void isTeamNotInMatchDayMatchNull() throws Exception {
        MatchDay matchDay = new MatchDay();
        assertTrue(TrialAndErrorTimeTableEngine.isTeamNotInMatchDay(matchDay, new Team(TEAM1)));
    }

    @Test
    public void isTeamNotInMatchDayHappyCase() throws Exception {
        MatchDay matchDay = new MatchDay();
        Match match = new Match(new Team(TEAM1), new Team(TEAM2));
        matchDay.addMatch(match);
        assertTrue(TrialAndErrorTimeTableEngine.isTeamNotInMatchDay(matchDay, new Team(TEAM3)));
    }

    @Test
    public void isTeamNotInMatchDayFalseBecauseOfHomeTeam() throws Exception {
        MatchDay matchDay = new MatchDay();
        Match match = new Match(new Team(TEAM1), new Team(TEAM2));
        matchDay.addMatch(match);
        assertFalse(TrialAndErrorTimeTableEngine.isTeamNotInMatchDay(matchDay, new Team(TEAM1)));
    }

    @Test
    public void isTeamNotInMatchDayFalseBecauseOfGuestTeam() throws Exception {
        MatchDay matchDay = new MatchDay();
        Match match = new Match(new Team(TEAM1), new Team(TEAM2));
        matchDay.addMatch(match);
        assertFalse(TrialAndErrorTimeTableEngine.isTeamNotInMatchDay(matchDay, new Team(TEAM2)));
    }

    // -------------------------------------------------------------
    // addMatchIfNotContainedAlready
    // -------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addMatchIfNotContainedAlreadyMatchDayIsNull() throws Exception {
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM1), new Team(TEAM2)));
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(null, matchesWithMinimalScore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMatchIfNotContainedAlreadyNoMatchesToAdd() throws Exception {
        MatchDay matchDay = new MatchDay();
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMatchIfNotContainedAlreadyMatchesToAddIsNull() throws Exception {
        MatchDay matchDay = new MatchDay();
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(matchDay, null);
    }

    @Test
    public void addMatchIfNotContainedAlreadyEmptyMatchDay() throws Exception {
        MatchDay matchDay = new MatchDay();
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM1), new Team(TEAM2)));
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(1, matchDay.getNumberOfMatches());
    }

    @Test
    public void addMatchIfNotContainedAlreadyNotEmptyMatchDay() throws Exception {
        MatchDay matchDay = new MatchDay();
        matchDay.addMatch(new Match(new Team(TEAM3), new Team(TEAM4)));
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM1), new Team(TEAM2)));
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(2, matchDay.getNumberOfMatches());
    }

    @Test
    public void addMatchIfNotContainedAlreadyMatchContained() throws Exception {
        MatchDay matchDay = new MatchDay();
        matchDay.addMatch(new Match(new Team(TEAM1), new Team(TEAM2)));
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM1), new Team(TEAM2)));
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(1, matchDay.getNumberOfMatches());
    }

    @Test
    public void addMatchIfNotContainedAlreadySwitchedMatchContained() throws Exception {
        MatchDay matchDay = new MatchDay();
        matchDay.addMatch(new Match(new Team(TEAM2), new Team(TEAM1)));
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM1), new Team(TEAM2)));
        TrialAndErrorTimeTableEngine.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(1, matchDay.getNumberOfMatches());
    }

    // -------------------------------------------------------------
    // getMatchesWithSameScore
    // -------------------------------------------------------------

    @Test
    public void getMatchesWithSameScore() throws Exception {

        Map<Match, Integer> matchToScore = Maps.newHashMap();
        Match match1Against2 = new Match(new Team(TEAM1), new Team(TEAM2));
        matchToScore.put(match1Against2, 0);
        Match match3Against2 = new Match(new Team(TEAM3), new Team(TEAM2));
        matchToScore.put(match3Against2, 0);
        Match match1Against3 = new Match(new Team(TEAM1), new Team(TEAM3));
        matchToScore.put(match1Against3, 1);
        Match match2Against3 = new Match(new Team(TEAM2), new Team(TEAM3));
        matchToScore.put(match2Against3, 1);
        Match match2Against4 = new Match(new Team(TEAM2), new Team(TEAM4));
        matchToScore.put(match2Against4, 2);
        Match match3Against4 = new Match(new Team(TEAM3), new Team(TEAM4));
        matchToScore.put(match3Against4, 3);
        List<Match> matchesWithScore0 = TrialAndErrorTimeTableEngine.getMatchesWithSameScore(matchToScore, 0);
        assertEquals(2, matchesWithScore0.size());
        boolean containsMatch1Against2 = false;
        boolean containsMatch3Against2 = false;
        for (Match match : matchesWithScore0) {
            if (match.equals(match1Against2)) {
                containsMatch1Against2 = true;
            } else if (match.equals(match3Against2)) {
                containsMatch3Against2 = true;
            }
        }
        assertTrue(containsMatch1Against2);
        assertTrue(containsMatch3Against2);

        List<Match> matchesWithScore1 = TrialAndErrorTimeTableEngine.getMatchesWithSameScore(matchToScore, 1);
        assertEquals(4, matchesWithScore1.size());
        boolean containsMatch1Against3 = false;
        boolean containsMatch2Against3 = false;
        for (Match match : matchesWithScore1) {
            if (match.equals(match1Against3)) {
                containsMatch1Against3 = true;
            } else if (match.equals(match2Against3)) {
                containsMatch2Against3 = true;
            }
        }
        assertTrue(containsMatch1Against3);
        assertTrue(containsMatch2Against3);

        List<Match> matchesWithScore2 = TrialAndErrorTimeTableEngine.getMatchesWithSameScore(matchToScore, 2);
        assertEquals(5, matchesWithScore2.size());
        assertTrue(matchesWithScore2.contains(match2Against4));
        List<Match> matchesWithScore3 = TrialAndErrorTimeTableEngine.getMatchesWithSameScore(matchToScore, 3);
        assertEquals(6, matchesWithScore3.size());
        assertTrue(matchesWithScore3.contains(match3Against4));
    }

    // -------------------------------------------------------------
    // main run
    // -------------------------------------------------------------

    @Test
    public void buildAllPossibleMatchDayPermutationsRetry() throws Exception {
        for (int i = 0; i < 1; i++) {
            List<Match> matchesOfFirstRound = TrialAndErrorTimeTableEngine.buildAllMatchesOfFirstRound(teams);
            List<MatchDay> firstRoundMatchDays = TrialAndErrorTimeTableEngine
                    .buildAllPossibleMatchDayPermutationsRetry(teams, matchesOfFirstRound);
            assertAppearanceOfTeamsInMatchDays(firstRoundMatchDays);
            assertAllTeamsHaveSameSumOfMatches2(firstRoundMatchDays, NUMBER_OF_MATCH_DAYS_ONE_ROUND);
        }
    }

    private void assertAllTeamsHaveSameSumOfMatches2(final List<MatchDay> firstRoundMatchDays,
            final int numberOfExpectedMatchDays) {
        Map<Team, Integer> teamToNumberOfMatchDay = Maps.newHashMap();
        Map<Team, Integer> teamToNumberOfHomeMatchDay = Maps.newHashMap();
        Map<Team, Integer> teamToNumberOfGuestMatchDay = Maps.newHashMap();
        for (MatchDay matchDay : firstRoundMatchDays) {
            for (Match match : matchDay.getMatches()) {
                incrementMapCounter(teamToNumberOfMatchDay, match.getHomeTeam());
                incrementMapCounter(teamToNumberOfMatchDay, match.getGuestTeam());
                incrementMapCounter(teamToNumberOfHomeMatchDay, match.getHomeTeam());
                incrementMapCounter(teamToNumberOfGuestMatchDay, match.getGuestTeam());
            }
        }

        assertNumberOfTotalMatchDays(teamToNumberOfMatchDay, numberOfExpectedMatchDays);

        assertEquals(NUMBER_OF_TEAMS, teamToNumberOfHomeMatchDay.size());
        assertEquals(NUMBER_OF_TEAMS, teamToNumberOfGuestMatchDay.size());

        for (int i = 1; i <= teamToNumberOfHomeMatchDay.size(); i++) {
            Team team = teams.get(i - 1);
            assertEquals("sum of home and guest matchDays must equal total sum of matches in one round",
                    numberOfExpectedMatchDays,
                    teamToNumberOfHomeMatchDay.get(team) + teamToNumberOfGuestMatchDay.get(team));
        }
    }

    private void assertNumberOfTotalMatchDays(final Map<Team, Integer> teamToNumberOfMatchDay,
            final int numberOfExpectedMatchDays) {
        Integer numberOfMatchDays = null;
        assertEquals(NUMBER_OF_TEAMS, teamToNumberOfMatchDay.size());
        for (Entry<Team, Integer> teamToNumberOfMatchDays : teamToNumberOfMatchDay.entrySet()) {
            if (numberOfMatchDays == null) {
                numberOfMatchDays = teamToNumberOfMatchDays.getValue();
            } else {
                assertEquals(numberOfMatchDays, teamToNumberOfMatchDays.getValue());
            }
        }
        assertEquals(numberOfExpectedMatchDays, numberOfMatchDays.intValue());
    }

    private void incrementMapCounter(final Map<Team, Integer> teamToNumberOfMatchDay, final Team team) {
        if (teamToNumberOfMatchDay.get(team) == null) {
            teamToNumberOfMatchDay.put(team, 1);
        } else {
            teamToNumberOfMatchDay.put(team, teamToNumberOfMatchDay.get(team) + 1);
        }
    }

    @Test
    public void addSecondRoundMatches() throws Exception {
        List<Match> firstRoundMatches = TrialAndErrorTimeTableEngine.buildAllMatchesOfFirstRound(teams);
        List<MatchDay> matchDays = TrialAndErrorTimeTableEngine.buildAllPossibleMatchDayPermutationsRetry(teams,
                firstRoundMatches);

        List<MatchDay> secondRoundMatches = TrialAndErrorTimeTableEngine.getSecondRoundMatches(matchDays);

        assertNotNull(matchDays);
        assertEquals(NUMBER_OF_MATCH_DAYS_ONE_ROUND, secondRoundMatches.size());
        assertAllTeamsHaveSameSumOfMatches2(secondRoundMatches, NUMBER_OF_MATCH_DAYS_ONE_ROUND);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTimeTableTeamsIsNull() {
        TrialAndErrorTimeTableEngine.createTimeTable(null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void createTimeTableTeamsIsEmpty() {
        TrialAndErrorTimeTableEngine.createTimeTable(Collections.EMPTY_LIST);
    }

    @Test
    public void createTimeTable() {
        TimeTable timeTable = TrialAndErrorTimeTableEngine.createTimeTable(teams);

        assertNotNull(timeTable);
        List<MatchDay> matchDays = timeTable.getAllMatchDays();
        assertNotNull(matchDays);
        assertEquals(NUMBER_OF_MATCH_DAYS, matchDays.size());

        assertAppearanceOfTeamsInMatchDays(matchDays);
        assertAllTeamsHaveSameSumOfMatches2(matchDays, NUMBER_OF_MATCH_DAYS);
        System.out.println(timeTable.print());
    }

    private void assertAllTeamsHaveSameSumOfMatches(final List<Match> matches) {
        for (Team team : teams) {
            int numberOfMatches = 0;
            for (Match match : matches) {
                if (match.containsTeam(team)) {
                    numberOfMatches++;
                }
            }
            assertEquals(NUMBER_OF_MATCH_DAYS_ONE_ROUND, numberOfMatches);
        }
    }

    private void assertAppearanceOfTeamsInMatchDays(final List<MatchDay> matchDays) {
        for (MatchDay matchDay : matchDays) {
            assertThatAllTeamsInMatchDay(matchDay);
        }
    }

    private void assertThatAllTeamsInMatchDay(final MatchDay matchDay) {
        for (Team team : teams) {
            int appearancesInMatchDay = 0;
            for (Match match : matchDay.getMatches()) {
                if (team.equals(match.getHomeTeam()) || team.equals(match.getGuestTeam())) {
                    appearancesInMatchDay++;
                }
            }
            assertEquals(String.format("team [%s] appears [%s] times in timeTable of matchDay [%s]", team.getName(),
                    appearancesInMatchDay, matchDay.print()), 1, appearancesInMatchDay);
        }
    }
}
