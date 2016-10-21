package de.footballmanager.backend;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.parser.PlayerParserService;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.service.ResultService;
import de.footballmanager.backend.service.TeamManagerService;
import de.footballmanager.backend.service.TimeTableService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.footballmanager.backend.enumeration.Position.GOALY;
import static de.footballmanager.backend.enumeration.Position.LEFT_DEFENDER;
import static org.junit.Assert.*;

//@Ignore("go on here")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class OneSeasonIT {

    @Autowired
    private TimeTableService timeTableService;
    @Autowired
    private TeamManagerService teamManagerService;
    @Autowired
    private ResultService resultService;
    @Autowired
    private LeagueService leagueService;

    @Test
    public void test() throws Exception {

        // given: 1 KI team, 1 self managed team
        leagueService.createLeague("teams.xml", "names.txt", "surnames.txt");
        List<Team> teams = leagueService.getTeams();
        Manager manager = new Manager();
        manager.setFirstName("Jan");
        manager.setLastName("Buck");
        manager.setComputerManaged(false);
        Team managedTeam = teams.get(0);
        Team computerTeam = teams.get(1);
        teamManagerService.setTeamManager(manager, managedTeam);


        TimeTable timeTable = timeTableService.createTimeTable(Lists.newArrayList(computerTeam, managedTeam));
        assertNotNull(timeTable);
        int numberOfDays = timeTable.getAllMatchDays().size();
        assertEquals(2, numberOfDays);




        // when: run match day 1
        MatchDay matchDay = timeTable.getMatchDay(1);
        Match match = matchDay.getMatches().get(0);
        ListMultimap<Position, Player> positionToPlayerMap = teamManagerService.getPositionToPlayerMap(managedTeam);
        Map<Position, Player> bestPlayersForSystem = teamManagerService.getBestPlayersForSystem(managedTeam,
                PlayingSystem.SYSTEM_4_4_2);
        teamManagerService.setStartEleven(match, managedTeam, bestPlayersForSystem);
        teamManagerService.setStartElevenIfComputerManaged(matchDay);
        List<Match> matches = matchDay.getMatches();
        assertEquals(1, matches.size());


        matches.forEach(Match::start);

        // run first half
        IntStream.range(0, 45).forEach(i -> resultService.calculateNextMinute(matches));
        assertTrue(match.isStarted());
        assertFalse(match.isFinished());
        assertNotNull(match.getHalfTime());
        assertTrue(match.getResult() == null);
        assertEquals(45, match.getMinute());

        // make manual player change
        List<Player> substituteBench = teamManagerService.getSubstituteBench(match, managedTeam);
        teamManagerService.changePlayer(match, managedTeam, substituteBench.get(2),
                bestPlayersForSystem.get(Position.LEFT_DEFENDER));

        // make manual system change




    }
}
