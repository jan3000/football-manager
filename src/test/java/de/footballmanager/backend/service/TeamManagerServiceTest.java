package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.util.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static de.footballmanager.backend.util.TestUtil.createStartEleven;
import static de.footballmanager.backend.util.TestUtil.createTeam;
import static org.junit.Assert.assertEquals;

public class TeamManagerServiceTest {


    private TeamManagerService teamManagerService;

    @Before
    public void setUp() {
        teamManagerService = new TeamManagerService();
    }


    @Test
    @Ignore("go on here")
    public void test() {
        Team team = createTeam("Team1");
        createStartEleven(team, TeamManagerService.SYSTEM_4_3_3);
        assertEquals(true, teamManagerService.hasPlayerForSystem(team, TeamManagerService.SYSTEM_4_3_3));
    }

}
