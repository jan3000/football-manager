package de.footballmanager.backend.domain.league;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.TestUtil;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static de.footballmanager.backend.enumeration.Position.GOALY;
import static de.footballmanager.backend.enumeration.Position.LEFT_MIDFIELDER;
import static de.footballmanager.backend.util.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatchTest {

    @Test
    public void changeOnePlayerHome() {
        Match runningMatch = createRunningMatch();
        changeOnePlayer(runningMatch, runningMatch.getPlayerChangesHomeTeam(), runningMatch.getHomeTeam(), true);
    }

    @Test
    public void changeOnePlayerGuest() {
        Match runningMatch = createRunningMatch();
        changeOnePlayer(runningMatch, runningMatch.getPlayerChangesGuestTeam(), runningMatch.getGuestTeam(), false);
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
        match.changePlayer(team, in, out);

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
        Match runningMatch = createRunningMatch();
        changeInPlayerNotMemberOfTeam(runningMatch, runningMatch.getPositionPlayerMapHomeTeam());
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInAPlayerNotMemberOfTheTeamShouldThrowExceptionGuest() {
        Match runningMatch = createRunningMatch();
        changeInPlayerNotMemberOfTeam(runningMatch, runningMatch.getPositionPlayerMapGuestTeam());
    }

    private void changeInPlayerNotMemberOfTeam(Match runningMatch, Map<Position, Player> positionPlayerMap) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);

        // when
        runningMatch.changePlayer(runningMatch.getHomeTeam(), in1, players.iterator().next());
    }


    @Test(expected = IllegalArgumentException.class)
    public void changeOutNotPlayingPlayerShouldThrowExceptionHome() {

        Match runningMatch = createRunningMatch();
        changeOutNotPlayingPlayer(runningMatch, runningMatch.getHomeTeam());
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeOutNotPlayingPlayerShouldThrowExceptionGuest() {

        Match runningMatch = createRunningMatch();
        changeOutNotPlayingPlayer(runningMatch, runningMatch.getGuestTeam());
    }

    private void changeOutNotPlayingPlayer(Match runningMatch, Team team) {
        // given
        Player in = createPlayer("Player", "In", LEFT_MIDFIELDER);
        Player out = createPlayer("Player", "Out", LEFT_MIDFIELDER);
        team.getPlayers().add(in);

        // when
        runningMatch.changePlayer(team, in, out);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInPlayerAlreadyPlayingShouldThrowExceptionHome() {

        Match runningMatch = createRunningMatch();
        changeInPlayerAlreadyPlaying(runningMatch.getHomeTeam(), runningMatch, runningMatch.getPositionPlayerMapHomeTeam());
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInPlayerAlreadyPlayingShouldThrowExceptionGuest() {

        Match runningMatch = createRunningMatch();
        changeInPlayerAlreadyPlaying(runningMatch.getGuestTeam(), runningMatch, runningMatch.getPositionPlayerMapGuestTeam());
    }

    private void changeInPlayerAlreadyPlaying(Team team, Match runningMatch, Map<Position, Player> positionPlayerMap) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        // when
        runningMatch.changePlayer(team, players.iterator().next(), players.iterator().next());
    }

    @Test(expected = IllegalStateException.class)
    public void changeFourPlayersShouldThrowExceptionHome() {
        Match runningMatch = createRunningMatch();
        changeFourPlayers(runningMatch, runningMatch.getPositionPlayerMapHomeTeam(), runningMatch.getPlayerChangesHomeTeam(), runningMatch.getHomeTeam());
    }

    @Test(expected = IllegalStateException.class)
    public void changeFourPlayersShouldThrowExceptionGuest() {
        Match runningMatch = createRunningMatch();
        changeFourPlayers(runningMatch, runningMatch.getPositionPlayerMapGuestTeam(), runningMatch.getPlayerChangesGuestTeam(), runningMatch.getGuestTeam());
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
        runningMatch.changePlayer(team, in1, iterator.next());
        assertEquals(1, playerChanges.size());

        runningMatch.changePlayer(team, in2, iterator.next());
        assertEquals(2, playerChanges.size());

        runningMatch.changePlayer(team, in3, iterator.next());
        assertEquals(3, playerChanges.size());

        runningMatch.changePlayer(team, in4, iterator.next());
    }

    @Test
    public void changeThreePlayersForBothTeamsShouldWork() {
        Match runningMatch = createRunningMatch();
        Collection<Player> playersHome = runningMatch.getPositionPlayerMapHomeTeam().values();
        Collection<Player> playersGuest = runningMatch.getPositionPlayerMapGuestTeam().values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);
        Player in2 = createPlayer("New", "Player2", LEFT_MIDFIELDER);
        Player in3 = createPlayer("New", "Player3", LEFT_MIDFIELDER);
        Player in4 = createPlayer("New", "Player4", LEFT_MIDFIELDER);
        Player in5 = createPlayer("New", "Player5", LEFT_MIDFIELDER);
        Player in6 = createPlayer("New", "Player6", LEFT_MIDFIELDER);
        Team homeTeam = runningMatch.getHomeTeam();
        homeTeam.getPlayers().addAll(Lists.newArrayList(in1, in2, in3));
        Team guestTeam = runningMatch.getGuestTeam();
        guestTeam.getPlayers().addAll(Lists.newArrayList(in4, in5, in6));

        // when
        Iterator<Player> homeIterator = playersHome.iterator();
        Iterator<Player> guestIterator = playersGuest.iterator();

        runningMatch.changePlayer(homeTeam, in1, homeIterator.next());
        assertEquals(1, runningMatch.getPlayerChangesHomeTeam().size());

        runningMatch.changePlayer(guestTeam, in4, guestIterator.next());
        assertEquals(1, runningMatch.getPlayerChangesGuestTeam().size());

        runningMatch.changePlayer(guestTeam, in5, guestIterator.next());
        assertEquals(2, runningMatch.getPlayerChangesGuestTeam().size());

        runningMatch.changePlayer(homeTeam, in2, homeIterator.next());
        assertEquals(2, runningMatch.getPlayerChangesHomeTeam().size());

        runningMatch.changePlayer(homeTeam, in3, homeIterator.next());
        assertEquals(3, runningMatch.getPlayerChangesHomeTeam().size());

        runningMatch.changePlayer(guestTeam, in6, guestIterator.next());
        assertEquals(3, runningMatch.getPlayerChangesGuestTeam().size());

    }

    @Test
    public void setPositionPlayerMapHomeTeam() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(teamHome, PlayingSystem.SYSTEM_4_4_2);
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPositionPlayerMapHomeTeamSamePlayerTwiceShouldThrowException() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(teamHome, PlayingSystem.SYSTEM_4_4_2);
        Iterator<Position> iterator = positionPlayerMap.keySet().iterator();
        Position position1 = iterator.next();
        Position position2 = iterator.next();
        positionPlayerMap.put(position1, positionPlayerMap.get(position2));
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPositionPlayerMapHomeTeamPlayerNotInTeamShouldThrowException() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_NAME_1, PlayingSystem.SYSTEM_4_4_2);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(teamHome, PlayingSystem.SYSTEM_4_4_2);
        Iterator<Position> iterator = positionPlayerMap.keySet().iterator();
        Position position1 = iterator.next();
        positionPlayerMap.put(position1, createPlayer("Unknown", "Player", GOALY));
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

//    @Test(expected = IllegalStateException.class)
//    public void setPositionPlayerMapHomeTeamMatchAlreadyRunningShouldThrowException() {
//        Match match = createRunningMatch();
//        Map<Position, Player> positionPlayerMap = createStartElevenMatchingGivenSystem(match.getHomeTeam(),
//                PlayingSystem.SYSTEM_4_4_2);
//        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
//    }

}
