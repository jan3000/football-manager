package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;
import de.footballmanager.backend.parser.LeagueParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public class LeagueService {

    @Autowired
    private LeagueParser leagueParser;
    @Autowired
    private TrialAndErrorTimeTableService timeTableService;

    private League league;
    private TimeTable timeTable;

    public void initLeague() {
        try {
            System.out.println("34343434343");
            if (league == null) {
                System.out.println("444444444444");
                league = leagueParser.parse();
                timeTable = timeTableService.createTimeTable(league.getTeams());
            }
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Team> getTeams() {
        initLeague();
        return league.getTeams();
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public MatchDay getTimeTableForMatchDay(int matchDay) {
        return timeTable.getMatchDay(matchDay);
    }

    public void getCurrentTable() {

    }
}
