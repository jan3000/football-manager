package de.footballmanager.backend.engine;

import java.io.FileNotFoundException;

import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.parser.LeagueParser;
import de.footballmanager.backend.service.TrialAndErrorTimeTableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;

@Service
public class RunEngine {


    public static void main(final String[] args) throws JAXBException, FileNotFoundException {
        System.out.println(StringUtils.repeat("-", 100));
        System.out.println("Football-Manager 2013");
        System.out.println(StringUtils.repeat("-", 100));

        LeagueParser leagueParser = new LeagueParser();
        League league = leagueParser.parse();


        for (Team team : league.getTeams()) {
            System.out.println(team);
        }

        TrialAndErrorTimeTableService timeTableService = new TrialAndErrorTimeTableService();
        TimeTable timeTable = timeTableService.createTimeTable(league.getTeams());
        league.setTimeTable(timeTable);

        for (MatchDay matchDay : league.getTimeTable().getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                ResultEngine.calculateResult(match);
            }

        }
        System.out.println(league.getTimeTable().print());
        System.out.println(league.printCurrentTable());
    }
}
