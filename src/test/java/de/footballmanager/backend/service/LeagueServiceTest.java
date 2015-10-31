package de.footballmanager.backend.service;


import de.footballmanager.backend.domain.Team;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LeagueServiceTest {

    private LeagueService leagueService;

    @Before
    public void setUp() {
        leagueService = new LeagueService();
    }

    @Test
    public void test() {
//        leagueService.initLeague();
//        List<Team> teams = leagueService.getTeams();
//        Assertions.assertThat(teams).isNotEmpty();
    }

}
