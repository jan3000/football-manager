//package de.footballmanager.backend.domain;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.google.common.collect.Lists;
//
//import de.footballmanager.backend.LeagueUtil;
//
//public class LeagueTest {
//
//    private static final String BREMEN = "Bremen";
//    private static final String MUENCHEN = "Muenchen";
//    private static final String DORTMUND = "Dortmund";
//    private static final String HAMBURG = "Hamburg";
//    private static final int NUMBER_OF_TEAMS = 8;
//    private static final int NUMBER_OF_MATCH_DAYS = 14;
//    private static final int NUMBER_OF_MATCHES_ON_ONE_DAY = 4;
//    private League league;
//    private List<Team> teams;
//
//    @Before
//    public void setUp() {
//        teams = Lists.newArrayList();
//        for (int i = 1; i <= NUMBER_OF_TEAMS; i++) {
//            teams.add(new Team(String.format("Team%s", i)));
//        }
//
//        league = new League(teams);
//
//    }
//
//    @Test
//    public void getNumberOfTeams() throws Exception {
//        assertEquals(NUMBER_OF_TEAMS, league.getNumberOfTeams());
//    }
//
//    @Test
//    public void getNumberOfMatchDays() throws Exception {
//        assertEquals(NUMBER_OF_MATCH_DAYS, league.getNumberOfMatchDays());
//    }
//
//    @Test
//    public void getNumberOfMatchesOnOneDay() {
//        assertEquals(NUMBER_OF_MATCHES_ON_ONE_DAY, league.getNumberOfMatchesOnOneDay());
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void buildFirstRoundMatchesLeagueWithoutTeams() throws Exception {
//        league = new League(null);
//    }
//
//    @Test
//    public void buildFirstRoundMatches() throws Exception {
//        List<Match> firstRoundMatches = league.buildAllMatchesOfFirstRound();
//        assertNotNull(firstRoundMatches);
//        assertEquals((NUMBER_OF_TEAMS - 1) * NUMBER_OF_TEAMS / 2, firstRoundMatches.size());
//        assertCorrectNumberOfMatchesForEveryTeam(firstRoundMatches);
//    }
//
//    @Test
//    public void buildFirstRoundMatchDays() {
//        List<Team> testTeams = Lists.newArrayList(LeagueUtil.createTeam(HAMBURG), LeagueUtil.createTeam(DORTMUND),
//                LeagueUtil.createTeam(MUENCHEN), LeagueUtil.createTeam(BREMEN));
//        league = new League();
//        league.setTeams(testTeams);
//
//        List<Match> firstRoundMatches = Lists.newArrayList();
//        firstRoundMatches.add(LeagueUtil.createMatch(HAMBURG, DORTMUND));
//        firstRoundMatches.add(LeagueUtil.createMatch(MUENCHEN, BREMEN));
//        firstRoundMatches.add(LeagueUtil.createMatch(HAMBURG, MUENCHEN));
//        firstRoundMatches.add(LeagueUtil.createMatch(DORTMUND, BREMEN));
//        firstRoundMatches.add(LeagueUtil.createMatch(HAMBURG, BREMEN));
//        firstRoundMatches.add(LeagueUtil.createMatch(MUENCHEN, DORTMUND));
//        List<MatchDay> firstRoundMatchDays = league.buildAllMatchDaysOfFirstRound(firstRoundMatches);
//
//        assertNotNull(firstRoundMatchDays);
//        for (MatchDay matchDay : firstRoundMatchDays) {
//            System.out.println("MatchDay: " + matchDay);
//            System.out.println(matchDay.toStringAllMatches());
//        }
//        assertEquals(3, firstRoundMatchDays.size());
//        MatchDay matchDay1 = firstRoundMatchDays.get(0);
//        assertMatchDaysCorrectness(matchDay1, HAMBURG, DORTMUND, MUENCHEN, BREMEN);
//
//        MatchDay matchDay2 = firstRoundMatchDays.get(1);
//        assertMatchDaysCorrectness(matchDay2, MUENCHEN, HAMBURG, DORTMUND, BREMEN);
//
//        MatchDay matchDay3 = firstRoundMatchDays.get(2);
//        assertMatchDaysCorrectness(matchDay3, HAMBURG, BREMEN, DORTMUND, MUENCHEN);
//
//    }
//
//    private void assertMatchDaysCorrectness(final MatchDay matchDay, final String homeTeam1, final String guestTeam1,
//            final String homeTeam2, final String guestTeam2) {
//        assertNotNull(matchDay);
//        assertEquals(1, matchDay.getMatchDayNumber());
//        assertEquals(2, matchDay.getMatches());
//        List<Match> matchesOfDay = matchDay.getMatches();
//        assertEquals(homeTeam1, matchesOfDay.get(0).getHomeTeam());
//        assertEquals(guestTeam1, matchesOfDay.get(0).getGuestTeam());
//        assertEquals(homeTeam2, matchesOfDay.get(1).getHomeTeam());
//        assertEquals(guestTeam2, matchesOfDay.get(1).getGuestTeam());
//    }
//
//    @Test
//    public void generateSecondRound() throws Exception {
//
//    }
//
//    private void assertCorrectNumberOfMatchesForEveryTeam(final List<Match> firstRoundMatches) {
//        for (Team team : teams) {
//            int numberOfMatches = 0;
//            for (Match match : firstRoundMatches) {
//                if (match.containsTeam(team)) {
//                    numberOfMatches++;
//                }
//            }
//            assertEquals(NUMBER_OF_TEAMS - 1, numberOfMatches);
//        }
//    }
//
//    @Test
//    public void testNumberOfMatchDays() throws Exception {
//        assertEquals(NUMBER_OF_TEAMS, league.getNumberOfTeams());
//        assertEquals(NUMBER_OF_MATCH_DAYS, league.getNumberOfMatchDays());
//    }
// }
