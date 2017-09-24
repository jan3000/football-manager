package de.footballmanager.backend.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Club;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.ClubService;
import de.footballmanager.backend.service.MatchService;
import de.footballmanager.backend.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_4_4_2;
import static de.footballmanager.backend.enumeration.Position.GOALY;
import static de.footballmanager.backend.enumeration.Position.LEFT_MIDFIELDER;
import static de.footballmanager.backend.util.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatchServiceTest {

    private MatchService matchService;
    private ClubService clubService;

    @Before
    public void setUp() {
        matchService = new MatchService();
        clubService = new ClubService();
        ReflectionTestUtils.setField(matchService, "clubService", clubService);
    }

    @Test
    public void changeOnePlayerHome() {
        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(homeTeam, guestTeam);
        Map<String, Club> clubNameToClub = createClubNameToClubMap(Lists.newArrayList(homeTeam, guestTeam));
        ReflectionTestUtils.setField(clubService, "clubNameToClub", clubNameToClub);
        changeOnePlayer(runningMatch, runningMatch.getPlayerChangesHomeTeam(), homeTeam, true);
    }

    @Test
    public void changeOnePlayerGuest() {
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), guestTeam);
        changeOnePlayer(runningMatch, runningMatch.getPlayerChangesGuestTeam(), guestTeam, false);
    }

    private void changeOnePlayer(Match match, List<Match.PlayerChange> playerChanges, Team team, boolean isHomeTeam) {
        // given
        Collection<Player> currentEleven = getPositionPlayerMap(match, isHomeTeam).values();

        Player out = currentEleven.iterator().next();
        Position positionForChange = out.getPosition();
        Player in = createPlayer("New", "Player", LEFT_MIDFIELDER);
        team.getPlayers().add(in);
        assertTrue(!currentEleven.contains(in));

        // when
        matchService.changePlayer(match, team.getName(), in, out);

        // then
        Map<Position, Player> positionPlayerMap = getPositionPlayerMap(match, isHomeTeam);
        assertEquals(11, positionPlayerMap.entrySet().size());
        assertTrue(positionPlayerMap.values().contains(in));
        assertTrue(!positionPlayerMap.values().contains(out));
        assertEquals(in, positionPlayerMap.get(positionForChange));

        assertEquals(1, playerChanges.size());
    }

    private Map<Position, Player> getPositionPlayerMap(Match match, boolean isHomeTeam) {
        Map<Position, Player> positionPlayerMap;
        if (isHomeTeam) {
            positionPlayerMap = match.getPositionPlayerMapHomeTeam();
        } else {
            positionPlayerMap = match.getPositionPlayerMapGuestTeam();
        }
        return positionPlayerMap;
    }


    @Test(expected = IllegalArgumentException.class)
    public void changeInAPlayerNotMemberOfTheTeamShouldThrowExceptionHome() {
        Match runningMatch = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), createTeam(TEAM_NAME_1, SYSTEM_4_4_2));
        changeInPlayerNotMemberOfTeam(runningMatch, runningMatch.getPositionPlayerMapHomeTeam());
    }

    private Match createRunningMatch(Team homeTeam, Team guestTeam) {
        Map<String, Club> clubNameToClub = createClubNameToClubMap(Lists.newArrayList(homeTeam, guestTeam));
        ReflectionTestUtils.setField(clubService, "clubNameToClub", clubNameToClub);

        return TestUtil.createRunningMatch(homeTeam, guestTeam);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInAPlayerNotMemberOfTheTeamShouldThrowExceptionGuest() {
        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(homeTeam, guestTeam);

        changeInPlayerNotMemberOfTeam(runningMatch, runningMatch.getPositionPlayerMapGuestTeam());
    }

    private void changeInPlayerNotMemberOfTeam(Match runningMatch, Map<Position, Player> positionPlayerMap) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);

        // when
        matchService.changePlayer(runningMatch, runningMatch.getHomeTeam(), in1, players.iterator().next());
    }


    @Test(expected = IllegalArgumentException.class)
    public void changeOutNotPlayingPlayerShouldThrowExceptionHome() {

        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(homeTeam, createTeam(TEAM_NAME_1, SYSTEM_4_4_2));
        changeOutNotPlayingPlayer(runningMatch, homeTeam);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeOutNotPlayingPlayerShouldThrowExceptionGuest() {

        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), guestTeam);
        changeOutNotPlayingPlayer(runningMatch, guestTeam);
    }

    private void changeOutNotPlayingPlayer(Match runningMatch, Team team) {
        // given
        Player in = createPlayer("Player", "In", LEFT_MIDFIELDER);
        Player out = createPlayer("Player", "Out", LEFT_MIDFIELDER);
        team.getPlayers().add(in);

        // when
        matchService.changePlayer(runningMatch, team.getName(), in, out);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInPlayerAlreadyPlayingShouldThrowExceptionHome() {

        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(homeTeam, guestTeam);
        changeInPlayerAlreadyPlaying(homeTeam, runningMatch, runningMatch.getPositionPlayerMapHomeTeam());
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInPlayerAlreadyPlayingShouldThrowExceptionGuest() {

        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), guestTeam);
        changeInPlayerAlreadyPlaying(guestTeam, runningMatch, runningMatch.getPositionPlayerMapGuestTeam());
    }

    private void changeInPlayerAlreadyPlaying(Team team, Match runningMatch, Map<Position, Player> positionPlayerMap) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        // when
        matchService.changePlayer(runningMatch, team.getName(), players.iterator().next(), players.iterator().next());
    }

    @Test(expected = IllegalStateException.class)
    public void changeFourPlayersShouldThrowExceptionHome() {
        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(homeTeam, createTeam(TEAM_NAME_1, SYSTEM_4_4_2));
        changeFourPlayers(runningMatch, runningMatch.getPositionPlayerMapHomeTeam(), runningMatch.getPlayerChangesHomeTeam(), homeTeam);
    }

    @Test(expected = IllegalStateException.class)
    public void changeFourPlayersShouldThrowExceptionGuest() {
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);
        Match runningMatch = createRunningMatch(createTeam(TEAM_NAME_2, SYSTEM_4_4_2), guestTeam);
        changeFourPlayers(runningMatch, runningMatch.getPositionPlayerMapGuestTeam(), runningMatch.getPlayerChangesGuestTeam(), guestTeam);
    }

    private void changeFourPlayers(Match runningMatch, Map<Position, Player> positionPlayerMap, List<Match.PlayerChange> playerChanges, Team team) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);
        Player in2 = createPlayer("New", "Player2", LEFT_MIDFIELDER);
        Player in3 = createPlayer("New", "Player3", LEFT_MIDFIELDER);
        Player in4 = createPlayer("New", "Player4", LEFT_MIDFIELDER);
        team.getPlayers().addAll(Lists.newArrayList(in1, in2, in3, in4));

        // when
        Iterator<Player> iterator = players.iterator();
        matchService.changePlayer(runningMatch, team.getName(), in1, iterator.next());
        assertEquals(1, playerChanges.size());

        matchService.changePlayer(runningMatch, team.getName(), in2, iterator.next());
        assertEquals(2, playerChanges.size());

        matchService.changePlayer(runningMatch, team.getName(), in3, iterator.next());
        assertEquals(3, playerChanges.size());

        matchService.changePlayer(runningMatch, team.getName(), in4, iterator.next());
    }

    @Test
    public void changeThreePlayersForBothTeamsShouldWork() {
        Team homeTeam = createTeam(TEAM_NAME_2, SYSTEM_4_4_2);
        Team guestTeam = createTeam(TEAM_NAME_1, SYSTEM_4_4_2);

        Match runningMatch = createRunningMatch(homeTeam, guestTeam);
        Collection<Player> playersHome = runningMatch.getPositionPlayerMapHomeTeam().values();
        Collection<Player> playersGuest = runningMatch.getPositionPlayerMapGuestTeam().values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);
        Player in2 = createPlayer("New", "Player2", LEFT_MIDFIELDER);
        Player in3 = createPlayer("New", "Player3", LEFT_MIDFIELDER);
        Player in4 = createPlayer("New", "Player4", LEFT_MIDFIELDER);
        Player in5 = createPlayer("New", "Player5", LEFT_MIDFIELDER);
        Player in6 = createPlayer("New", "Player6", LEFT_MIDFIELDER);
        homeTeam.getPlayers().addAll(Lists.newArrayList(in1, in2, in3));
        guestTeam.getPlayers().addAll(Lists.newArrayList(in4, in5, in6));

        // when
        Iterator<Player> homeIterator = playersHome.iterator();
        Iterator<Player> guestIterator = playersGuest.iterator();

        matchService.changePlayer(runningMatch, homeTeam.getName(), in1, homeIterator.next());
        assertEquals(1, runningMatch.getPlayerChangesHomeTeam().size());

        matchService.changePlayer(runningMatch, guestTeam.getName(), in4, guestIterator.next());
        assertEquals(1, runningMatch.getPlayerChangesGuestTeam().size());

        matchService.changePlayer(runningMatch, guestTeam.getName(), in5, guestIterator.next());
        assertEquals(2, runningMatch.getPlayerChangesGuestTeam().size());

        matchService.changePlayer(runningMatch, homeTeam.getName(), in2, homeIterator.next());
        assertEquals(2, runningMatch.getPlayerChangesHomeTeam().size());

        matchService.changePlayer(runningMatch, homeTeam.getName(), in3, homeIterator.next());
        assertEquals(3, runningMatch.getPlayerChangesHomeTeam().size());

        matchService.changePlayer(runningMatch, guestTeam.getName(), in6, guestIterator.next());
        assertEquals(3, runningMatch.getPlayerChangesGuestTeam().size());

    }

    private Map<String, Club> createClubNameToClubMap(List<Team> teams) {
        Map<String, Club> clubNameToClub = Maps.newHashMap();
        teams.forEach(team -> {
            Club club = new Club(team.getName());
            club.setTeam(team);
            clubNameToClub.put(team.getName(), club);
        });
        return clubNameToClub;
    }

    @Test
    public void setPositionPlayerMapHomeTeam() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        match.setHomeTeam(teamHome.getName());
        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(teamHome, PlayingSystem.SYSTEM_4_4_2);
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPositionPlayerMapHomeTeamSamePlayerTwiceShouldThrowException() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        Team teamGuest = TestUtil.createTeam(TEAM_NAME_2, PlayingSystem.SYSTEM_4_4_2);
        match.setHomeTeam(teamHome.getName());

        createRunningMatch(teamHome, teamGuest);

        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(teamHome, PlayingSystem.SYSTEM_4_4_2);
        Iterator<Position> iterator = positionPlayerMap.keySet().iterator();
        Position position1 = iterator.next();
        Position position2 = iterator.next();
        positionPlayerMap.put(position1, positionPlayerMap.get(position2));
        matchService.setPositionPlayerMapHomeTeam(match, positionPlayerMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPositionPlayerMapHomeTeamPlayerNotInTeamShouldThrowException() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        Team teamGuest = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        match.setHomeTeam(teamHome.getName());
        createRunningMatch(teamHome, teamGuest);
        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(teamHome, PlayingSystem.SYSTEM_4_4_2);
        Iterator<Position> iterator = positionPlayerMap.keySet().iterator();
        Position position1 = iterator.next();
        positionPlayerMap.put(position1, createPlayer("Unknown", "Player", GOALY));
        matchService.setPositionPlayerMapHomeTeam(match, positionPlayerMap);
    }

//    @Test(expected = IllegalStateException.class)
//    public void setPositionPlayerMapHomeTeamMatchAlreadyRunningShouldThrowException() {
//        Match match = createRunningMatch();
//        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(match.getHomeTeam(),
//                PlayingSystem.SYSTEM_4_4_2);
//        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
//    }

}
