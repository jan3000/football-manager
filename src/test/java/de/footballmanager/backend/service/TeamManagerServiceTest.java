package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.PlayingSystem;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.util.TestUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.footballmanager.backend.util.TestUtil.createTeam;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class TeamManagerServiceTest {


    private TeamManagerService teamManagerService;

    @Before
    public void setUp() {
        teamManagerService = new TeamManagerService();
    }


    @Test
    @Parameters
    public void hasPlayerForSystem(PlayingSystem systemOfTeam, PlayingSystem systemToMatch, boolean isMatch) {
        Team team = createTeam("Team1");
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
