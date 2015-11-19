package de.footballmanager.backend.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.parser.LeagueParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public class LeagueService {

    public static final Predicate<Match> IS_ENDED_PREDICATE = new Predicate<Match>() {
        @Override
        public boolean apply(Match input) {
            return input.isFinished();
        }
    };
    @Autowired
    private LeagueParser leagueParser;
    @Autowired
    private ResultService resultService;
    @Autowired
    private TrialAndErrorTimeTableService timeTableService;

    private int currentMatchDay;
    private League league;
    private TimeTable timeTable;

    public void initLeague() {
        try {
            System.out.println("34343434343");
            if (league == null) {
                System.out.println("444444444444");
                currentMatchDay = 1;
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

    public MatchDay runNextMinute() {
        MatchDay matchDay = timeTable.getMatchDay(currentMatchDay);
        List<Match> matches = matchDay.getMatches();
        resultService.calculateNextMinute(matches);

        if (haveAllMatchesEnded(matches)) {
            currentMatchDay++;
        };

        return matchDay;
    }

    private boolean haveAllMatchesEnded(List<Match> matches) {
        return Collections2.filter(matches, IS_ENDED_PREDICATE).size() == matches.size();
    }

    /**
     *
     * @return finished matchDay
     */
    public MatchDay runNextMatchDay() {
        MatchDay matchDay = timeTable.getMatchDay(currentMatchDay);
        for (Match match : matchDay.getMatches()) {
            resultService.calculateResult(match);
        }
        currentMatchDay++;
        return timeTable.getMatchDay(currentMatchDay - 1);
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public MatchDay getTimeTableForMatchDay(int matchDay) {
        initLeague();
        return timeTable.getMatchDay(matchDay);
    }

    public void getCurrentTable() {

    }
}
