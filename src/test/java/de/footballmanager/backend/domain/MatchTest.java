package de.footballmanager.backend.domain;

import com.google.common.collect.Lists;
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
        match.changePlayer(in, out, isHomeTeam);

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
        changeInPlayerNotMemberOfTeam(runningMatch, runningMatch.getPositionPlayerMapHomeTeam(), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInAPlayerNotMemberOfTheTeamShouldThrowExceptionGuest() {
        Match runningMatch = createRunningMatch();
        changeInPlayerNotMemberOfTeam(runningMatch, runningMatch.getPositionPlayerMapGuestTeam(), false);
    }

    private void changeInPlayerNotMemberOfTeam(Match runningMatch, Map<Position, Player> positionPlayerMap, boolean isHomeTeamChange) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);

        // when
        runningMatch.changePlayer(in1, players.iterator().next(), isHomeTeamChange);
    }


    @Test(expected = IllegalArgumentException.class)
    public void changeOutNotPlayingPlayerShouldThrowExceptionHome() {

        Match runningMatch = createRunningMatch();
        changeOutNotPlayingPlayer(runningMatch, runningMatch.getHomeTeam(), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeOutNotPlayingPlayerShouldThrowExceptionGuest() {

        Match runningMatch = createRunningMatch();
        changeOutNotPlayingPlayer(runningMatch, runningMatch.getGuestTeam(), false);
    }

    private void changeOutNotPlayingPlayer(Match runningMatch, Team team, boolean isHomeTeamChange) {
        // given
        Player in = createPlayer("Player", "In", LEFT_MIDFIELDER);
        Player out = createPlayer("Player", "Out", LEFT_MIDFIELDER);
        team.getPlayers().add(in);

        // when
        runningMatch.changePlayer(in, out, isHomeTeamChange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInPlayerAlreadyPlayingShouldThrowExceptionHome() {

        Match runningMatch = createRunningMatch();
        changeInPlayerAlreadyPlaying(runningMatch, runningMatch.getPositionPlayerMapHomeTeam(), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInPlayerAlreadyPlayingShouldThrowExceptionGuest() {

        Match runningMatch = createRunningMatch();
        changeInPlayerAlreadyPlaying(runningMatch, runningMatch.getPositionPlayerMapGuestTeam(), false);
    }

    private void changeInPlayerAlreadyPlaying(Match runningMatch, Map<Position, Player> positionPlayerMap, boolean isHomeTeamChange) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        // when
        runningMatch.changePlayer(players.iterator().next(), players.iterator().next(), isHomeTeamChange);
    }

    @Test(expected = IllegalStateException.class)
    public void changeFourPlayersShouldThrowExceptionHome() {
        Match runningMatch = createRunningMatch();
        changeFourPlayers(runningMatch, runningMatch.getPositionPlayerMapHomeTeam(), runningMatch.getPlayerChangesHomeTeam(), runningMatch.getHomeTeam(), true);
    }

    @Test(expected = IllegalStateException.class)
    public void changeFourPlayersShouldThrowExceptionGuest() {
        Match runningMatch = createRunningMatch();
        changeFourPlayers(runningMatch, runningMatch.getPositionPlayerMapGuestTeam(), runningMatch.getPlayerChangesGuestTeam(), runningMatch.getGuestTeam(), false);
    }

    private void changeFourPlayers(Match runningMatch, Map<Position, Player> positionPlayerMap, List<Match.PlayerChange> playerChanges, Team team, boolean isHomeTeamChange) {
        // given
        Collection<Player> players = positionPlayerMap.values();

        Player in1 = createPlayer("New", "Player1", LEFT_MIDFIELDER);
        Player in2 = createPlayer("New", "Player2", LEFT_MIDFIELDER);
        Player in3 = createPlayer("New", "Player3", LEFT_MIDFIELDER);
        Player in4 = createPlayer("New", "Player4", LEFT_MIDFIELDER);
        team.getPlayers().addAll(Lists.newArrayList(in1, in2, in3, in4));

        // when
        Iterator<Player> iterator = players.iterator();
        runningMatch.changePlayer(in1, iterator.next(), isHomeTeamChange);
        assertEquals(1, playerChanges.size());

        runningMatch.changePlayer(in2, iterator.next(), isHomeTeamChange);
        assertEquals(2, playerChanges.size());

        runningMatch.changePlayer(in3, iterator.next(), isHomeTeamChange);
        assertEquals(3, playerChanges.size());

        runningMatch.changePlayer(in4, iterator.next(), isHomeTeamChange);
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
        runningMatch.getHomeTeam().getPlayers().addAll(Lists.newArrayList(in1, in2, in3));
        runningMatch.getGuestTeam().getPlayers().addAll(Lists.newArrayList(in4, in5, in6));

        // when
        Iterator<Player> homeIterator = playersHome.iterator();
        Iterator<Player> guestIterator = playersGuest.iterator();

        runningMatch.changePlayer(in1, homeIterator.next(), true);
        assertEquals(1, runningMatch.getPlayerChangesHomeTeam().size());

        runningMatch.changePlayer(in4, guestIterator.next(), false);
        assertEquals(1, runningMatch.getPlayerChangesGuestTeam().size());

        runningMatch.changePlayer(in5, guestIterator.next(), false);
        assertEquals(2, runningMatch.getPlayerChangesGuestTeam().size());

        runningMatch.changePlayer(in2, homeIterator.next(), true);
        assertEquals(2, runningMatch.getPlayerChangesHomeTeam().size());

        runningMatch.changePlayer(in3, homeIterator.next(), true);
        assertEquals(3, runningMatch.getPlayerChangesHomeTeam().size());

        runningMatch.changePlayer(in6, guestIterator.next(), false);
        assertEquals(3, runningMatch.getPlayerChangesGuestTeam().size());

    }

    @Test
    public void setPositionPlayerMapHomeTeam() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_1);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartEleven(teamHome);
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPositionPlayerMapHomeTeamSamePlayerTwiceShouldThrowException() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_1);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartEleven(teamHome);
        Iterator<Position> iterator = positionPlayerMap.keySet().iterator();
        Position position1 = iterator.next();
        Position position2 = iterator.next();
        positionPlayerMap.put(position1, positionPlayerMap.get(position2));
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPositionPlayerMapHomeTeamPlayerNotInTeamShouldThrowException() {
        Match match = new Match();
        Team teamHome = TestUtil.createTeam(TEAM_1);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartEleven(teamHome);
        Iterator<Position> iterator = positionPlayerMap.keySet().iterator();
        Position position1 = iterator.next();
        positionPlayerMap.put(position1, createPlayer("Unknown", "Player", GOALY));
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

    @Test(expected = IllegalStateException.class)
    public void setPositionPlayerMapHomeTeamMatchAlreadyRunningShouldThrowException() {
        Match match = createRunningMatch();
        Team teamHome = TestUtil.createTeam(TEAM_1);
        match.setHomeTeam(teamHome);
        Map<Position, Player> positionPlayerMap = createStartEleven(teamHome);
        match.setPositionPlayerMapHomeTeam(positionPlayerMap);
    }

}
