package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;
import de.footballmanager.backend.parser.LeagueParser;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class LeagueService {

    @Autowired
    private LeagueParser leagueParser;
    @Autowired
    private TrialAndErrorTimeTableService timeTableService;

    private League league;

    public void initLeague() {
        try {
            if (league == null) {
                league = leagueParser.parse();
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
        Preconditions.checkArgument(league != null, "league must be initialized before timeTable generation");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(league.getTeams()), "teams must exist before timeTable generation");
        return timeTableService.createTimeTable(league.getTeams());
    }

    public void getCurrentTable() {

    }
}