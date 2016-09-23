package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.PlayingSystem;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.TestUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static de.footballmanager.backend.enumeration.Position.CENTRAL_DEFENSIVE_MIDFIELDER;
import static de.footballmanager.backend.enumeration.Position.GOALY;
import static de.footballmanager.backend.util.TestUtil.createTeam;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class TeamManagerServiceTest {


    private TeamManagerService teamManagerService;

    @Before
    public void setUp() {
        teamManagerService = new TeamManagerService();
    }

    @Test
    public void getPossibleSystemsNotFound() {
        Team team = TestUtil.createTeam("Hamburger SV", PlayingSystem.SYSTEM_4_4_2);
        team.getPlayers().get(10).setPosition(GOALY);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(0, possibleSystems.size());
    }

    @Test
    public void getPossibleSystemsJustOneSystemMatching() {
        Team team = TestUtil.createTeam("Hamburger SV", PlayingSystem.SYSTEM_4_4_2);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(1, possibleSystems.size());
        assertEquals(PlayingSystem.SYSTEM_4_4_2, possibleSystems.get(0));
    }

    @Test
    public void getPossibleSystemsJustTwoSystemMatching() {
        Team team = TestUtil.createTeam("Hamburger SV", PlayingSystem.SYSTEM_4_4_2);
        team.getPlayers().get(12).setPosition(Position.CENTRAL_OFFENSIVE_MIDFIELDER);
        team.getPlayers().get(13).setPosition(Position.CENTRAL_STRIKER);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(2, possibleSystems.size());
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_4_2));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_2_3_1));
    }

    @Test
    public void getPossibleSystemsJustThreeSystemMatching() {
        Team team = TestUtil.createTeam("Hamburger SV", PlayingSystem.SYSTEM_4_4_2);
        team.getPlayers().get(12).setPosition(Position.CENTRAL_OFFENSIVE_MIDFIELDER);
        team.getPlayers().get(13).setPosition(Position.CENTRAL_STRIKER);
        team.getPlayers().get(14).setPosition(Position.CENTRAL_DEFENSIVE_MIDFIELDER);
        team.getPlayers().get(15).setPosition(Position.LEFT_WINGER);
        team.getPlayers().get(16).setPosition(Position.RIGHT_WINGER);
        List<PlayingSystem> possibleSystems = teamManagerService.getPossibleSystems(team);
        assertNotNull(possibleSystems);
        assertEquals(4, possibleSystems.size());
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_4_2));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_2_3_1));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_3_3));
        assertTrue(possibleSystems.contains(PlayingSystem.SYSTEM_4_4_2_DIAMOND));
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
