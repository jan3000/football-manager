package de.footballmanager.backend.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Club;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.infrastructure.Stadium;
import de.footballmanager.backend.domain.league.League;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.util.xml.LeagueInitializer;
import de.footballmanager.backend.domain.util.xml.LeaguesWrapper;
import de.footballmanager.backend.domain.league.Season;
import de.footballmanager.backend.domain.league.TimeTable;
import de.footballmanager.backend.domain.util.xml.StadiumInitializer;
import de.footballmanager.backend.parser.LeagueParser;
import de.footballmanager.backend.parser.PersonParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Service
public class InitializationService {

    public static final String NAME_FILE = "names.txt";
    public static final String SURNAMES_FILE = "surnames.txt";
    @Autowired
    private TrialAndErrorTimeTableService timeTableService;
    @Autowired
    private PersonParserService personParserService;
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private LeagueParser leagueParser;
    @Autowired
    private DateService dateService;
    @Autowired
    private ClubService clubService;

    private Map<String, League> nameToLeague = Maps.newHashMap();

    @PostConstruct
    public void initLeagues() {
        try {
            if (nameToLeague == null) {
                createLeagues("club.xml", NAME_FILE, SURNAMES_FILE);
            }
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createLeagues(String teamsFile, String firstNameFile, String lastNameFile)
            throws JAXBException, FileNotFoundException {
        System.out.println("INIT STARTED");
        List<String> leaguePriorityList = Lists.newArrayList();
        LeaguesWrapper leaguesWrapper = leagueParser.parse(teamsFile);
        List<LeagueInitializer> leagueInitializers = leaguesWrapper.getLeagues();
        leagueInitializers.forEach(leagueInitializer -> {
            leaguePriorityList.add(leagueInitializer.getName());
            List<Team> teams = Lists.newArrayList();
            leagueInitializer.getClubInitializerList().forEach(data -> {
                String clubName = data.getName();
                Stadium stadium = new Stadium();
                StadiumInitializer stadiumData = data.getStadiumInitializer();
                stadium.setStance(stadiumData.getStance());
                stadium.setRoofedStance(stadium.getRoofedStance());
                stadium.setSeats(stadium.getSeats());
                stadium.setRoofedSeats(stadium.getRoofedSeats());
                String firstName = personParserService.getName(NAME_FILE);
                String lastName = personParserService.getName(SURNAMES_FILE);

                Team team = new Team(clubName);
                team.setStrength(data.getStrength());
                teams.add(team);

                Club club = new Club(clubName);
                club.setStadium(stadium);
                club.setTeam(team);
                clubService.registerClub(club);
                clubService.setManager(clubName, new Manager(firstName, lastName));

            });
            League league = new League(leagueInitializer.getName(), teams, leagueInitializer.getNumberOfPromotions());
            TimeTable timeTable = timeTableService.createTimeTable(teams, dateService.getToday());
            league.addSeason(new Season(dateService.getToday(), timeTable, teams));
            nameToLeague.put(league.getName(), league);
            personParserService.parsePlayerForLeague(league, firstNameFile, lastNameFile);


        });
        leagueService.setLeaguePriorityList(leaguePriorityList);
        leagueService.setNameToLeague(nameToLeague);
        System.out.println("INIT FINISHED");
    }
}
