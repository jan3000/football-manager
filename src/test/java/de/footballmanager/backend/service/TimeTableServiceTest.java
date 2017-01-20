package de.footballmanager.backend.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.TimeTable;
import de.footballmanager.backend.util.LeagueTestUtil;
import de.footballmanager.backend.util.PrintUtil;
import de.footballmanager.backend.util.TestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_4_3_3;
import static de.footballmanager.backend.util.TestUtil.*;
import static java.util.stream.Collectors.toList;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class TimeTableServiceTest {

    private static final int NUMBER_OF_TEAMS = 18;
    private static final int NUMBER_OF_MATCH_DAYS_ONE_ROUND = NUMBER_OF_TEAMS - 1;
    private static final int NUMBER_OF_MATCH_DAYS = NUMBER_OF_MATCH_DAYS_ONE_ROUND * 2;
    private static final int NUMBER_OF_MATCHES_IN_FIRST_ROUND = NUMBER_OF_MATCH_DAYS_ONE_ROUND * (NUMBER_OF_TEAMS / 2);

    private List<Team> teams = Lists.newArrayList();
    private TrialAndErrorTimeTableService timeTableService;

    @Before
    public void setUpTeams() {
        timeTableService = new TrialAndErrorTimeTableService();
        for (int i = 1; i <= NUMBER_OF_TEAMS; i++) {
            teams.add(LeagueTestUtil.createTeam(String.format("Team%s", i)));
        }
    }

    @Test
    public void getNumberOfMatchDaysOfOneRound() throws Exception {
        int numberOfMatchDaysOfOneRound = timeTableService.getNumberOfMatchDaysOfOneRound(teams);
        assertEquals(NUMBER_OF_MATCH_DAYS_ONE_ROUND, numberOfMatchDaysOfOneRound);
    }

    @Test
    public void getTotalNumberOfMatchDays() throws Exception {
        int totalNumberOfMatchDays = timeTableService.getTotalNumberOfMatchDays(teams);
        assertEquals(NUMBER_OF_MATCH_DAYS, totalNumberOfMatchDays);
    }

    @Test
    public void buildAllMatchesOfFirstRound() {
        List<Match> matchesOfFirstRound = timeTableService.buildAllMatchesOfFirstRound(teams);

        assertNotNull(matchesOfFirstRound);
        assertEquals(NUMBER_OF_MATCHES_IN_FIRST_ROUND, matchesOfFirstRound.size());
        assertAllTeamsHaveSameSumOfMatches(matchesOfFirstRound);
    }

    // -------------------------------------------------------------
    // isTeamNotInMatchDay
    // -------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void isTeamNotInMatchDayMatchDayNull() throws Exception {
        timeTableService.isTeamNotInMatchDay(null, new Team(TestUtil.TEAM_NAME_1));
    }

    @Test
    public void isTeamNotInMatchDayMatchNull() throws Exception {
        MatchDay matchDay = new MatchDay();
        assertTrue(timeTableService.isTeamNotInMatchDay(matchDay, new Team(TEAM_NAME_1)));
    }

    @Test
    public void isTeamNotInMatchDayHappyCase() throws Exception {
        MatchDay matchDay = new MatchDay();
        Match match = new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2));
        matchDay.addMatch(match);
        assertTrue(timeTableService.isTeamNotInMatchDay(matchDay, new Team(TEAM_NAME_3)));
    }

    @Test
    public void isTeamNotInMatchDayFalseBecauseOfHomeTeam() throws Exception {
        MatchDay matchDay = new MatchDay();
        Match match = new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2));
        matchDay.addMatch(match);
        assertFalse(timeTableService.isTeamNotInMatchDay(matchDay, new Team(TEAM_NAME_1)));
    }

    @Test
    public void isTeamNotInMatchDayFalseBecauseOfGuestTeam() throws Exception {
        MatchDay matchDay = new MatchDay();
        Match match = new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2));
        matchDay.addMatch(match);
        assertFalse(timeTableService.isTeamNotInMatchDay(matchDay, new Team(TEAM_NAME_2)));
    }

    // -------------------------------------------------------------
    // addMatchIfNotContainedAlready
    // -------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addMatchIfNotContainedAlreadyMatchDayIsNull() throws Exception {
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2)));
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(null, matchesWithMinimalScore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMatchIfNotContainedAlreadyNoMatchesToAdd() throws Exception {
        MatchDay matchDay = new MatchDay();
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMatchIfNotContainedAlreadyMatchesToAddIsNull() throws Exception {
        MatchDay matchDay = new MatchDay();
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(matchDay, null);
    }

    @Test
    public void addMatchIfNotContainedAlreadyEmptyMatchDay() throws Exception {
        MatchDay matchDay = new MatchDay();
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2)));
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(1, matchDay.getNumberOfMatches());
    }

    @Test
    public void addMatchIfNotContainedAlreadyNotEmptyMatchDay() throws Exception {
        MatchDay matchDay = new MatchDay();
        matchDay.addMatch(new Match(new Team(TEAM_NAME_3), new Team(TEAM_NAME_4)));
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2)));
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(2, matchDay.getNumberOfMatches());
    }

    @Test
    public void addMatchIfNotContainedAlreadyMatchContained() throws Exception {
        MatchDay matchDay = new MatchDay();
        matchDay.addMatch(new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2)));
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2)));
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(1, matchDay.getNumberOfMatches());
    }

    @Test
    public void addMatchIfNotContainedAlreadySwitchedMatchContained() throws Exception {
        MatchDay matchDay = new MatchDay();
        matchDay.addMatch(new Match(new Team(TEAM_NAME_2), new Team(TEAM_NAME_1)));
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        matchesWithMinimalScore.add(new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2)));
        timeTableService.addMatchesToMatchDayIfNotContainedAlready(matchDay, matchesWithMinimalScore);
        assertEquals(1, matchDay.getNumberOfMatches());
    }

    // -------------------------------------------------------------
    // getMatchesWithSameScore
    // -------------------------------------------------------------

    @Test
    public void getMatchesWithSameScore() throws Exception {

        Map<Match, Integer> matchToScore = Maps.newHashMap();
        Match match1Against2 = new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_2));
        matchToScore.put(match1Against2, 0);
        Match match3Against2 = new Match(new Team(TEAM_NAME_3), new Team(TEAM_NAME_2));
        matchToScore.put(match3Against2, 0);
        Match match1Against3 = new Match(new Team(TEAM_NAME_1), new Team(TEAM_NAME_3));
        matchToScore.put(match1Against3, 1);
        Match match2Against3 = new Match(new Team(TEAM_NAME_2), new Team(TEAM_NAME_3));
        matchToScore.put(match2Against3, 1);
        Match match2Against4 = new Match(new Team(TEAM_NAME_2), new Team(TEAM_NAME_4));
        matchToScore.put(match2Against4, 2);
        Match match3Against4 = new Match(new Team(TEAM_NAME_3), new Team(TEAM_NAME_4));
        matchToScore.put(match3Against4, 3);
        List<Match> matchesWithScore0 = timeTableService.getMatchesWithSameScore(matchToScore, 0);
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

        List<Match> matchesWithScore1 = timeTableService.getMatchesWithSameScore(matchToScore, 1);
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

        List<Match> matchesWithScore2 = timeTableService.getMatchesWithSameScore(matchToScore, 2);
        assertEquals(5, matchesWithScore2.size());
        assertTrue(matchesWithScore2.contains(match2Against4));
        List<Match> matchesWithScore3 = timeTableService.getMatchesWithSameScore(matchToScore, 3);
        assertEquals(6, matchesWithScore3.size());
        assertTrue(matchesWithScore3.contains(match3Against4));
    }

    // -------------------------------------------------------------
    // main run
    // -------------------------------------------------------------

    @Test
    public void buildAllPossibleMatchDayPermutationsRetry() throws Exception {
        for (int i = 0; i < 1; i++) {
            List<Match> matchesOfFirstRound = timeTableService.buildAllMatchesOfFirstRound(teams);
            List<MatchDay> firstRoundMatchDays = timeTableService
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
        List<Match> firstRoundMatches = timeTableService.buildAllMatchesOfFirstRound(teams);
        List<MatchDay> matchDays = timeTableService.buildAllPossibleMatchDayPermutationsRetry(teams,
                firstRoundMatches);

        List<MatchDay> secondRoundMatches = timeTableService.getSecondRoundMatches(matchDays);

        assertNotNull(matchDays);
        assertEquals(NUMBER_OF_MATCH_DAYS_ONE_ROUND, secondRoundMatches.size());
        assertAllTeamsHaveSameSumOfMatches2(secondRoundMatches, NUMBER_OF_MATCH_DAYS_ONE_ROUND);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTimeTableTeamsIsNull() {
        timeTableService.createTimeTable(null, new DateTime());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void createTimeTableTeamsIsEmpty() {
        timeTableService.createTimeTable(Collections.EMPTY_LIST, new DateTime());
    }

    @Test(expected = NullPointerException.class)
    public void createTimeTableStartDateIsNull() {
        timeTableService.createTimeTable(Lists.newArrayList(TestUtil.createTeam("team", SYSTEM_4_3_3)), null);
    }

    @Test
    public void createTimeTable() {
        DateTime startDate = new DateTime();

        DateService dateService = createMock(DateService.class);
        expect(dateService.setDayTime(startDate, 15, 30)).andReturn(startDate);
        replay(dateService);
        ReflectionTestUtils.setField(timeTableService, "dateService", dateService);

        TimeTable timeTable = timeTableService.createTimeTable(teams, startDate);

        assertNotNull(timeTable);
        List<MatchDay> matchDays = timeTable.getAllMatchDays();
        assertNotNull(matchDays);
        assertEquals(NUMBER_OF_MATCH_DAYS, matchDays.size());
        assertNotNull(matchDays.get(0).getDate());

        assertAppearanceOfTeamsInMatchDays(matchDays);
        assertAllTeamsHaveSameSumOfMatches2(matchDays, NUMBER_OF_MATCH_DAYS);
        assertDatesAreSet(matchDays);
        System.out.println(PrintUtil.print(timeTable));
        verify(dateService);
    }

    private void assertDatesAreSet(List<MatchDay> matchDays) {
        assertTrue(CollectionUtils.isEmpty(matchDays.stream()
                .filter(matchDay -> matchDay.getDate() == null)
                .collect(toList())));
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
                    appearancesInMatchDay, PrintUtil.print(matchDay)), 1, appearancesInMatchDay);
        }
    }
}
