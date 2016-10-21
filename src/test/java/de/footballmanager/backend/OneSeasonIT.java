package de.footballmanager.backend;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.parser.PlayerParserService;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.service.ResultService;
import de.footballmanager.backend.service.TeamManagerService;
import de.footballmanager.backend.service.TimeTableService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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


        // run season
        IntStream.range(1, numberOfDays + 1).forEach(day -> {
            MatchDay matchDay = timeTable.getMatchDay(day);

            teamManagerService.setStartElevenIfComputerManaged(matchDay);
            List<Match> matches = matchDay.getMatches();
            assertEquals(1, matches.size());


            matches.forEach(Match::start);

            resultService.calculateNextMinute(matches);
        });



    }
}
