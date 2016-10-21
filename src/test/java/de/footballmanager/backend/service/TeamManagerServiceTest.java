package de.footballmanager.backend.service;

import com.google.common.collect.*;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.TestUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.footballmanager.backend.enumeration.Position.*;
import static de.footballmanager.backend.util.TestUtil.createMatchDay;
import static de.footballmanager.backend.util.TestUtil.createPlayer;
import static de.footballmanager.backend.util.TestUtil.createTeam;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class TeamManagerServiceTest {


    public static final String TEAM_NAME = "Hamburger SV";
    private TeamManagerService teamManagerService;

    @Before
    public void setUp() {
        teamManagerService = new TeamManagerService();
    }

    @Test
    public void hasPlayerForSystem() {
        // given
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2);
        team.setPlayers(team.getPlayers().subList(0, 11));

        assertTrue(teamManagerService.hasPlayerForSystem(team, PlayingSystem.SYSTEM_4_4_2));
    }

    @Test
    public void hasPlayerForSystemNo() {
        // given
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2);
        team.setPlayers(team.getPlayers().subList(0, 11));
        team.getPlayers().get(0).setPosition(Position.RIGHT_WINGER);

        assertFalse(teamManagerService.hasPlayerForSystem(team, PlayingSystem.SYSTEM_4_4_2));
    }

    @Test
    public void setBestPlayersForSystemWith11PlayersWithMatchingPositions() {
        // given
        PlayingSystem playingSystem = PlayingSystem.SYSTEM_4_4_2;
        Team team = createTeam(TEAM_NAME, playingSystem, 11);
        List<Player> players = team.getPlayers();

        StrengthService strengthService = createMock(StrengthService.class);
        ReflectionTestUtils.setField(teamManagerService, "strengthService", strengthService);
        replay(strengthService);

        // when
        Map<Position, Player> positionPlayerMap = teamManagerService.setBestPlayersForSystem(team, playingSystem);

        // then
        verify(strengthService);
        ImmutableMap<Position, Player> expectedPositionPlayerMap = Maps.uniqueIndex(players, Player::getPosition);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());
        assertEquals(expectedPositionPlayerMap.get(GOALY), positionPlayerMap.get(GOALY));
        assertEquals(expectedPositionPlayerMap.get(LEFT_DEFENDER), positionPlayerMap.get(LEFT_DEFENDER));
        assertEquals(expectedPositionPlayerMap.get(LEFT_STOPPER), positionPlayerMap.get(LEFT_STOPPER));
        assertEquals(expectedPositionPlayerMap.get(RIGHT_STOPPER), positionPlayerMap.get(RIGHT_STOPPER));
        assertEquals(expectedPositionPlayerMap.get(RIGHT_DEFENDER), positionPlayerMap.get(RIGHT_DEFENDER));
        assertEquals(expectedPositionPlayerMap.get(LEFT_DEFENSIVE_MIDFIELDER), positionPlayerMap.get(LEFT_DEFENSIVE_MIDFIELDER));
        assertEquals(expectedPositionPlayerMap.get(RIGHT_DEFENSIVE_MIDFIELDER), positionPlayerMap.get(RIGHT_DEFENSIVE_MIDFIELDER));
        assertEquals(expectedPositionPlayerMap.get(LEFT_MIDFIELDER), positionPlayerMap.get(LEFT_MIDFIELDER));
        assertEquals(expectedPositionPlayerMap.get(RIGHT_MIDFIELDER), positionPlayerMap.get(RIGHT_MIDFIELDER));
        assertEquals(expectedPositionPlayerMap.get(LEFT_STRIKER), positionPlayerMap.get(LEFT_STRIKER));
        assertEquals(expectedPositionPlayerMap.get(RIGHT_STRIKER), positionPlayerMap.get(RIGHT_STRIKER));
    }

    @Test
    public void setBestPlayersForSystemWith11PlayersWithoutLeftMidfielderBut2RightMidfielders() {
        // given
        PlayingSystem playingSystem = PlayingSystem.SYSTEM_4_4_2;
        Team team = createTeam(TEAM_NAME, playingSystem, 11);

        List<Player> players = team.getPlayers();
        Map<Position, Player> expectedPositionPlayerMap = Maps.uniqueIndex(players, Player::getPosition);
        players.remove(expectedPositionPlayerMap.get(LEFT_MIDFIELDER));
        Player alternativePlayer1 = expectedPositionPlayerMap.get(RIGHT_MIDFIELDER);
        Player alternativePlayer2 = createPlayer("Mr", "Right", RIGHT_MIDFIELDER);
        players.add(alternativePlayer2);

        StrengthService strengthService = createMock(StrengthService.class);
        expect(strengthService.getPlayerStrengthOnPosition(RIGHT_MIDFIELDER, alternativePlayer1)).andReturn(80).times(1);
        expect(strengthService.getPlayerStrengthOnPosition(RIGHT_MIDFIELDER, alternativePlayer2)).andReturn(50).times(1);

        ReflectionTestUtils.setField(teamManagerService, "strengthService", strengthService);
        replay(strengthService);

        // when
        Map<Position, Player> positionPlayerMap = teamManagerService.setBestPlayersForSystem(team, playingSystem);

        // then
        verify(strengthService);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());
        assertEquals(alternativePlayer2, positionPlayerMap.get(LEFT_MIDFIELDER));
        assertEquals(alternativePlayer1, positionPlayerMap.get(RIGHT_MIDFIELDER));
        assertEquals(Position.RIGHT_MIDFIELDER, positionPlayerMap.get(LEFT_MIDFIELDER).getPosition());
    }

    @Test
    public void setBestPlayersForSystemWith11PlayersWithoutLeftMidfielderBut4RightMidfielders() {
        // given
        PlayingSystem playingSystem = PlayingSystem.SYSTEM_4_4_2;
        Team team = createTeam(TEAM_NAME, playingSystem, 11);

        List<Player> players = team.getPlayers();
        Map<Position, Player> expectedPositionPlayerMap = Maps.uniqueIndex(players, Player::getPosition);
        players.remove(expectedPositionPlayerMap.get(LEFT_MIDFIELDER));
        Player alternativePlayer1 = expectedPositionPlayerMap.get(RIGHT_MIDFIELDER);
        Player alternativePlayer2 = createPlayer("Jan", "Bier", RIGHT_MIDFIELDER);
        players.add(alternativePlayer2);
        Player alternativePlayer3 = createPlayer("Mr", "Right", RIGHT_MIDFIELDER);
        players.add(alternativePlayer3);
        Player alternativePlayer4 = createPlayer("Tobias", "Mümmelmann", RIGHT_MIDFIELDER);
        players.add(alternativePlayer4);

        StrengthService strengthService = createMock(StrengthService.class);
        expect(strengthService.getPlayerStrengthOnPosition(RIGHT_MIDFIELDER, alternativePlayer1)).andReturn(80).times(3);
        expect(strengthService.getPlayerStrengthOnPosition(RIGHT_MIDFIELDER, alternativePlayer2)).andReturn(60).times(1);
        expect(strengthService.getPlayerStrengthOnPosition(RIGHT_MIDFIELDER, alternativePlayer3)).andReturn(60).times(1);
        expect(strengthService.getPlayerStrengthOnPosition(RIGHT_MIDFIELDER, alternativePlayer4)).andReturn(60).times(1);


        expect(strengthService.getPlayerStrengthOnPosition(LEFT_MIDFIELDER, alternativePlayer2)).andReturn(80).times(2);
        expect(strengthService.getPlayerStrengthOnPosition(LEFT_MIDFIELDER, alternativePlayer3)).andReturn(60).times(1);
        expect(strengthService.getPlayerStrengthOnPosition(LEFT_MIDFIELDER, alternativePlayer4)).andReturn(60).times(1);

        ReflectionTestUtils.setField(teamManagerService, "strengthService", strengthService);
        replay(strengthService);

        // when
        Map<Position, Player> positionPlayerMap = teamManagerService.setBestPlayersForSystem(team, playingSystem);

        // then
        verify(strengthService);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());
        assertEquals(alternativePlayer2, positionPlayerMap.get(LEFT_MIDFIELDER));
        assertEquals(alternativePlayer1, positionPlayerMap.get(RIGHT_MIDFIELDER));
        assertEquals(Position.RIGHT_MIDFIELDER, positionPlayerMap.get(LEFT_MIDFIELDER).getPosition());
    }


    @Test
    public void getBestPlayersIfMultipleSystemsHaveSameStrength() {
        // given
        Team team = createTeamForFourSystems();

        // when
        Pair<PlayingSystem, Map<Position, Player>> bestPlayersForBestSystem = teamManagerService.
                getBestPlayersForBestSystem(team);

        // then
        assertNotNull(bestPlayersForBestSystem);
        PlayingSystem playingSystem = bestPlayersForBestSystem.getFirst();
        Map<Position, Player> positionPlayerMap = bestPlayersForBestSystem.getSecond();
        assertNotNull(playingSystem);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());

    }


    @Test
    public void getBestPlayerForBestSystemReturnsSystemAndPlayersInCaseOfMultipleSystemMatches() {
        // given
        Team team = createTeamForFourSystems();
        List<Player> players = team.getPlayers();

        removeTail(players, 17);

        // when
        Pair<PlayingSystem, Map<Position, Player>> bestPlayersForBestSystem = teamManagerService.getBestPlayersForBestSystem(team);

        // then
        assertNotNull(bestPlayersForBestSystem);
        PlayingSystem playingSystem = bestPlayersForBestSystem.getFirst();
        Map<Position, Player> positionPlayerMap = bestPlayersForBestSystem.getSecond();
        assertNotNull(playingSystem);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());
    }

    @Test
    public void getBestPlayerForBestSystem443() {
        // given
        Team team = createTeamForFourSystems();
        List<Player> players = team.getPlayers();
        setPositionAndStrength(players.get(15), LEFT_WINGER, 99);
        setPositionAndStrength(players.get(0), GOALY, 29);

        removeTail(players, 17);

        // when
        Pair<PlayingSystem, Map<Position, Player>> bestPlayersForBestSystem = teamManagerService.getBestPlayersForBestSystem(team);

        // then
        assertNotNull(bestPlayersForBestSystem);
        PlayingSystem playingSystem = bestPlayersForBestSystem.getFirst();
        Map<Position, Player> positionPlayerMap = bestPlayersForBestSystem.getSecond();
        assertNotNull(playingSystem);
        assertEquals(PlayingSystem.SYSTEM_4_3_3, playingSystem);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());

        assertPlayerSelected(team, "Mr.", "11", positionPlayerMap.get(GOALY));
        assertPlayerSelected(team, "Mr.", "1", positionPlayerMap.get(LEFT_DEFENDER));
        assertPlayerSelected(team, "Mr.", "2", positionPlayerMap.get(LEFT_STOPPER));
        assertPlayerSelected(team, "Mr.", "3", positionPlayerMap.get(RIGHT_STOPPER));
        assertPlayerSelected(team, "Mr.", "4", positionPlayerMap.get(RIGHT_DEFENDER));
        assertPlayerSelected(team, "Mr.", "14", positionPlayerMap.get(CENTRAL_DEFENSIVE_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "8", positionPlayerMap.get(LEFT_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "7", positionPlayerMap.get(RIGHT_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "15", positionPlayerMap.get(LEFT_WINGER));
        assertPlayerSelected(team, "Mr.", "16", positionPlayerMap.get(RIGHT_WINGER));
        assertPlayerSelected(team, "Mr.", "13", positionPlayerMap.get(CENTRAL_STRIKER));
    }

    @Test
    public void getTeamStrength() {
        Team team = createTeam("Team", PlayingSystem.SYSTEM_4_4_2);
        Map<Position, Player> positionPlayerMap = teamManagerService.getBestPlayersForSystem(team, PlayingSystem.SYSTEM_4_4_2);
        int teamStrength = teamManagerService.getTeamStrength(positionPlayerMap.values());
        assertEquals(TestUtil.DEFAULT_STRENGTH, teamStrength);
    }

    @Test
    public void setStartEleven() {
        MatchDay matchDay = createMatchDay();
        teamManagerService.setStartElevenIfComputerManaged(matchDay);
        assertNotNull(matchDay);
        Match match1 = matchDay.getMatches().get(0);
        Match match2 = matchDay.getMatches().get(1);
        assertNotNull(match1.getPositionPlayerMapHomeTeam());
        assertEquals(11, match1.getPositionPlayerMapHomeTeam().size());
        assertNotNull(match1.getPositionPlayerMapGuestTeam());
        assertEquals(11, match1.getPositionPlayerMapGuestTeam().size());
        assertNotNull(match2.getPositionPlayerMapHomeTeam());
        assertEquals(11, match1.getPositionPlayerMapHomeTeam().size());
        assertNotNull(match2.getPositionPlayerMapGuestTeam());
        assertEquals(11, match1.getPositionPlayerMapGuestTeam().size());
    }


    @Test
    public void getBestPlayersForSystemSelectionBasedOnPosition() {
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2_DIAMOND);
        List<Player> players = team.getPlayers();
        players.get(11).setPosition(CENTRAL_STRIKER);
        players.get(12).setPosition(CENTRAL_STOPPER);
        players.get(13).setPosition(RIGHT_WINGER);
        players.get(14).setPosition(LEFT_WINGER);
        removeTail(players, 15);
        shufflePlayers(team);
        team.getPlayers().forEach(System.out::println);
        Map<Position, Player> positionPlayerMap = teamManagerService.getBestPlayersForSystem(team, PlayingSystem.SYSTEM_4_4_2_DIAMOND);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());
        assertPlayerSelected(team, "Mr.", "0", positionPlayerMap.get(GOALY));
        assertPlayerSelected(team, "Mr.", "1", positionPlayerMap.get(LEFT_DEFENDER));
        assertPlayerSelected(team, "Mr.", "2", positionPlayerMap.get(LEFT_STOPPER));
        assertPlayerSelected(team, "Mr.", "3", positionPlayerMap.get(RIGHT_STOPPER));
        assertPlayerSelected(team, "Mr.", "4", positionPlayerMap.get(RIGHT_DEFENDER));
        assertPlayerSelected(team, "Mr.", "5", positionPlayerMap.get(CENTRAL_DEFENSIVE_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "6", positionPlayerMap.get(LEFT_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "7", positionPlayerMap.get(RIGHT_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "8", positionPlayerMap.get(CENTRAL_OFFENSIVE_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "9", positionPlayerMap.get(LEFT_STRIKER));
        assertPlayerSelected(team, "Mr.", "10", positionPlayerMap.get(RIGHT_STRIKER));
    }

    private void removeTail(List<Player> players, int fromIndex) {
        players.subList(fromIndex, players.size()).clear();
    }

    private void assertPlayerSelected(Team team, String firstName, String lastName, Player player) {
        assertEquals(teamManagerService.getPlayerByName(team, firstName, lastName).get(), player);
    }

    @Test
    public void getBestPlayersForSystemSelectionBasedOnStrength() {
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2_DIAMOND);
        List<Player> players = team.getPlayers();
        setPositionAndStrength(players.get(1), GOALY, 21);
        setPositionAndStrength(players.get(2), GOALY, 88);
        setPositionAndStrength(players.get(3), GOALY, 71);
        setPositionAndStrength(players.get(4), LEFT_DEFENDER, 54);
        setPositionAndStrength(players.get(5), LEFT_DEFENDER, 71);
        setPositionAndStrength(players.get(6), LEFT_STOPPER, 71);
        setPositionAndStrength(players.get(7), LEFT_STOPPER, 33);
        setPositionAndStrength(players.get(8), CENTRAL_STOPPER, 91);
        setPositionAndStrength(players.get(9), RIGHT_STOPPER, 1);
        setPositionAndStrength(players.get(10), RIGHT_DEFENDER, 71);
        setPositionAndStrength(players.get(11), RIGHT_DEFENDER, 78);
        setPositionAndStrength(players.get(12), CENTRAL_DEFENSIVE_MIDFIELDER, 71);
        setPositionAndStrength(players.get(13), CENTRAL_DEFENSIVE_MIDFIELDER, 31);
        setPositionAndStrength(players.get(14), LEFT_MIDFIELDER, 71);
        setPositionAndStrength(players.get(15), LEFT_MIDFIELDER, 70);
        setPositionAndStrength(players.get(16), CENTRAL_OFFENSIVE_MIDFIELDER, 11);
        setPositionAndStrength(players.get(17), CENTRAL_OFFENSIVE_MIDFIELDER, 81);
        setPositionAndStrength(players.get(18), RIGHT_MIDFIELDER, 71);
        setPositionAndStrength(players.get(19), RIGHT_MIDFIELDER, 98);
        setPositionAndStrength(players.get(20), CENTRAL_STRIKER, 71);
        setPositionAndStrength(players.get(21), LEFT_STRIKER, 71);
        setPositionAndStrength(players.get(0), RIGHT_STRIKER, 71);
        shufflePlayers(team);
        team.getPlayers().forEach(System.out::println);
        Map<Position, Player> positionPlayerMap = teamManagerService.getBestPlayersForSystem(team, PlayingSystem.SYSTEM_4_4_2_DIAMOND);
        assertNotNull(positionPlayerMap);
        assertEquals(11, positionPlayerMap.size());
        assertPlayerSelected(team, "Mr.", "2", positionPlayerMap.get(GOALY));
        assertPlayerSelected(team, "Mr.", "5", positionPlayerMap.get(LEFT_DEFENDER));
        assertPlayerSelected(team, "Mr.", "6", positionPlayerMap.get(LEFT_STOPPER));
        assertPlayerSelected(team, "Mr.", "9", positionPlayerMap.get(RIGHT_STOPPER));
        assertPlayerSelected(team, "Mr.", "11", positionPlayerMap.get(RIGHT_DEFENDER));
        assertPlayerSelected(team, "Mr.", "12", positionPlayerMap.get(CENTRAL_DEFENSIVE_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "14", positionPlayerMap.get(LEFT_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "19", positionPlayerMap.get(RIGHT_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "17", positionPlayerMap.get(CENTRAL_OFFENSIVE_MIDFIELDER));
        assertPlayerSelected(team, "Mr.", "21", positionPlayerMap.get(LEFT_STRIKER));
        assertPlayerSelected(team, "Mr.", "0", positionPlayerMap.get(RIGHT_STRIKER));
    }


    private void setPositionAndStrength(Player player, Position position, int strength) {
        player.setPosition(position);
        player.setStrength(strength);
    }

    @Test
    public void getPlayerByName() {
        Team team = createTeam("Team1", PlayingSystem.SYSTEM_4_4_2_DIAMOND);
        Optional<Player> maybePlayer = teamManagerService.getPlayerByName(team, "Mr.", "5");
        assertTrue(maybePlayer.isPresent());
        assertEquals(maybePlayer.get().getFirstname(), "Mr.");
        assertEquals(maybePlayer.get().getLastname(), "5");
    }

    private void shufflePlayers(Team team) {
        team.setPlayers(Lists.newArrayList(Sets.newHashSet(team.getPlayers())));
    }

    @Test
    public void getPossibleSystemsNotFound() {
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2);
        team.getPlayers().get(10).setPosition(GOALY);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(0, possibleSystems.size());
    }

    @Test
    public void getPossibleSystemsJustOneSystemMatching() {
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(1, possibleSystems.size());
        assertEquals(PlayingSystem.SYSTEM_4_4_2, possibleSystems.get(0));
    }

    @Test
    public void getPossibleSystemsJustTwoSystemMatching() {
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2);
        team.getPlayers().get(12).setPosition(Position.CENTRAL_OFFENSIVE_MIDFIELDER);
        team.getPlayers().get(13).setPosition(Position.CENTRAL_STRIKER);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(2, possibleSystems.size());
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_4_2));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_2_3_1));
    }

    @Test
    public void getPossibleSystemsFourSystemsMatching() {
        Team team = createTeamForFourSystems();
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(4, possibleSystems.size());
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_4_2));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_2_3_1));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_3_3));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_4_2_DIAMOND));
    }

    private Team createTeamForFourSystems() {
        Team team = createTeam(TEAM_NAME, PlayingSystem.SYSTEM_4_4_2);
        team.getPlayers().get(12).setPosition(Position.CENTRAL_OFFENSIVE_MIDFIELDER);
        team.getPlayers().get(13).setPosition(Position.CENTRAL_STRIKER);
        team.getPlayers().get(14).setPosition(Position.CENTRAL_DEFENSIVE_MIDFIELDER);
        team.getPlayers().get(15).setPosition(Position.LEFT_WINGER);
        team.getPlayers().get(16).setPosition(Position.RIGHT_WINGER);
        return team;
    }


    @Test
    @Parameters
    public void hasPlayerForSystem(PlayingSystem systemOfTeam, PlayingSystem systemToMatch, boolean isMatch) {
        Team team = createTeam("Team1", PlayingSystem.SYSTEM_4_4_2);
        TestUtil.setPlayerPositions(team, systemOfTeam);
        assertEquals(isMatch, teamManagerService.hasPlayerForSystem(team, systemToMatch));
    }


    public Object[][] parametersForHasPlayerForSystem() {
        return new Object[][]{
            {PlayingSystem.SYSTEM_3_4_3 , PlayingSystem.SYSTEM_3_4_3, true},
                {PlayingSystem.SYSTEM_4_2_3_1 , PlayingSystem.SYSTEM_4_2_3_1, true},
                {PlayingSystem.SYSTEM_4_3_3, PlayingSystem.SYSTEM_4_3_3, true},
                {PlayingSystem.SYSTEM_4_4_2, PlayingSystem.SYSTEM_4_4_2, true},
                {PlayingSystem.SYSTEM_4_4_2_DIAMOND , PlayingSystem.SYSTEM_4_4_2_DIAMOND, true},
                {PlayingSystem.SYSTEM_4_2_3_1 , PlayingSystem.SYSTEM_3_4_3, false},
                {PlayingSystem.SYSTEM_4_4_2, PlayingSystem.SYSTEM_4_4_2_DIAMOND, false},
        };
    }

}
