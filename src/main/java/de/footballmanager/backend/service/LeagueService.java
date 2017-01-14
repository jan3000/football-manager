package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import de.footballmanager.backend.comparator.TeamValueComparator;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.parser.LeagueParser;
import de.footballmanager.backend.parser.PlayerParserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class LeagueService {

    @Autowired
    private TrialAndErrorTimeTableService timeTableService;
    @Autowired
    private PlayerParserService playerParserService;
    @Autowired
    private ResultService resultService;
    @Autowired
    private LeagueParser leagueParser;
    @Autowired
    private DateService dateService;

    private Map<String, League> nameToLeague = Maps.newHashMap();
    private Map<Integer, Table> matchDayToTable = Maps.newHashMap();

    @PostConstruct
    public void initLeagues() {
        try {
            if (nameToLeague == null) {
                createLeagues("team.xml", "names.txt", "surnames.txt");
            }
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createLeagues(String teamsFile, String firstNameFile, String lastNameFile)
            throws JAXBException, FileNotFoundException {
        System.out.println("INIT STARTED");
        LeaguesWrapper leaguesWrapper = leagueParser.parse(teamsFile);
        leaguesWrapper.getLeagues().forEach(league -> {
            List<Team> teams = league.getTeams();
            TimeTable timeTable = timeTableService.createTimeTable(teams, dateService.getToday());
            league.addSeason(new Season(dateService.getToday(), timeTable, teams));
            nameToLeague.put(league.getName(), league);
            playerParserService.parsePlayerForLeague(league, firstNameFile, lastNameFile);
        });
        System.out.println("INIT FINISHED");
    }

    public League getLeague(String leagueName) {
        League league = nameToLeague.get(leagueName);
        Preconditions.checkNotNull(league, "no league found for ", leagueName);
        return league;
    }

    public void addNewSeason(String leagueName, List<Team> teams) {
        League league = getLeague(leagueName);
        List<Season> seasons = league.getSeasons();
        Preconditions.checkArgument(seasons.size() > 0, "at least one season must be set to get the next season");
        Season lastSeason = seasons.get(seasons.size() - 1);
        DateTime startDate = lastSeason.getEndDate().plusDays(1);
        // go on here

        TimeTable timeTable = timeTableService.createTimeTable(teams, startDate);
        Season nextSeason = new Season(startDate, timeTable, teams);
        league.addSeason(nextSeason);

    }

    public Season getCurrentSeason(String leagueName) {
        League league = getLeague(leagueName);
        DateTime today = dateService.getToday();
        return league.getSeasons().stream()
                .filter(season -> isEqualOrAfter(today, season) && today.isBefore(season.getEndDate()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "no season found for league: " + leagueName + ", today: " + today));
    }

    private boolean isEqualOrAfter(DateTime today, Season season) {
        return today.isEqual(season.getStartDate()) || today.isAfter(season.getStartDate());
    }

//    public void setStartElevenHome(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven) {
//        getMatch(matchDayNumber, teamName).setPositionPlayerMapHomeTeam(positionToStartEleven);
//    }

    private Match getMatch(String leagueName, int matchDayNumber, String teamName) {
        League league = getLeague(leagueName);
        Preconditions.checkNotNull(league, "no league found for leagueName: ", leagueName);
        TimeTable timeTable = getCurrentSeason(leagueName).getTimeTable();
        Preconditions.checkNotNull(timeTable, "no timeTable found for leagueName: ", leagueName);
        return timeTable.getMatch(matchDayNumber, teamName);
    }

//    public void setStartElevenGuest(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven) {
//        getMatch(matchDayNumber, teamName).setPositionPlayerMapGuestTeam(positionToStartEleven);
//    }

    public List<Team> getTeams(String leagueName) {
        League league = getLeague(leagueName);
        Preconditions.checkNotNull(league, "no league found with name: ", leagueName);
        return league.getTeams();
    }

    /**
     * Sets up next match day.
     */
    public void startNextMatchDay(String leagueName) {
        TimeTable timeTable = getTimeTable(leagueName);
        MatchDay matchDay = timeTable.getMatchDay(timeTable.getCurrentMatchDay());
        List<Match> matches = matchDay.getMatches();
        matches.forEach(Match::start);
    }

    public void finishDay() {
        dateService.addDays(1);
    }

    public void finishDaysUntilNextMatchDay(String leagueName) {
        TimeTable timeTable = getTimeTable(leagueName);
        MatchDay currentMatchDay = timeTable.getMatchDay(timeTable.getCurrentMatchDay());
        dateService.finishDaysUntil(currentMatchDay.getDate());
    }

    public void finishDaysUntilNextSeason(String leagueName) {
        Season currentSeason = getCurrentSeason(leagueName);
        League league = getLeague(leagueName);
        List<Season> seasons = league.getSeasons();
        int indexOfCurrentSeason = seasons.indexOf(currentSeason);
        if (indexOfCurrentSeason + 1 < seasons.size()) {
            Season nextSeason = seasons.get(indexOfCurrentSeason + 1);
            dateService.finishDaysUntil(nextSeason.getStartDate());
        }
        ;
    }

    /**
     * Call startNextMatchDay before.
     *
     * @return
     */
    public MatchDay runNextMinute(String leagueName) {
        TimeTable timeTable = getTimeTable(leagueName);
        MatchDay matchDay = timeTable.getMatchDay(timeTable.getCurrentMatchDay());
        List<Match> matches = matchDay.getMatches();
        resultService.calculateNextMinute(matches);

        if (haveAllMatchesEnded(matches)) {

            generateChart(leagueName, timeTable.getCurrentMatchDay());
            if (timeTableService.isTimeTableFinished(timeTable)) {
                timeTable.setClosed();
            } else {
                timeTable.incrementCurrentMatchDay();
            }
        }

        return matchDay;
    }

    private boolean haveAllMatchesEnded(List<Match> matches) {
        return Collections2.filter(matches, Match::isFinished).size() == matches.size();
    }


    public TimeTable getTimeTable(String leagueName) {
        Season currentSeason = getCurrentSeason(leagueName);
        Preconditions.checkNotNull(currentSeason, "no currentSeason found for ", leagueName);
        return currentSeason.getTimeTable();
    }

    public int getCurrentMatchDayNumber(String leagueName) {
        TimeTable timeTable = getTimeTable(leagueName);
        Preconditions.checkNotNull(timeTable, "no timeTable found for ", leagueName);
        return timeTable.getCurrentMatchDay();
    }

    public MatchDay getCurrentMatchDay(String leagueName) {
        TimeTable timeTable = getTimeTable(leagueName);
        Preconditions.checkNotNull(timeTable, "no timeTable found for ", leagueName);
        return timeTable.getMatchDay(getCurrentMatchDayNumber(leagueName));
    }

    public MatchDay getTimeTableForMatchDay(String leagueName, int matchDay) {
//        initLeagues();
        System.out.println("return match day");
        TimeTable timeTable = getTimeTable(leagueName);
        return timeTable.getMatchDay(matchDay);
    }

    public Table getCurrentTable(String leagueName) {
        TimeTable timeTable = getTimeTable(leagueName);
        Preconditions.checkArgument(timeTable != null, "timeTable must not be null");
        return getTable(leagueName, timeTable.getCurrentMatchDay());
    }

    public Map<Integer, Table> getMatchDayToTable() {
        return matchDayToTable;
    }

    public Table getTable(String leagueName, int day) {
        if (matchDayToTable.containsKey(day)) {
            return matchDayToTable.get(day);
        } else {
            TimeTable timeTable = getTimeTable(leagueName);
            Preconditions.checkNotNull(timeTable, "no timeTable found for ", leagueName);
            return matchDayToTable.get(timeTable.getCurrentMatchDay() - 1);
        }
    }

    protected Table generateChart(String leagueName, int day) {
        System.out.println("GENERATE CHART FOR DAY: " + day);
        Map<Team, Integer> teamToPointsMap = Maps.newHashMap();
        Map<Team, TableEntry> teamToTableEntryMap = Maps.newHashMap();

        TimeTable timeTable = getTimeTable(leagueName);
        Preconditions.checkNotNull(timeTable, "no timeTable found for ", leagueName);
        for (MatchDay matchDay : timeTable.getAllMatchDays().asList().subList(0, day)) {
            for (Match match : matchDay.getMatches()) {
                if (match.isFinished()) {
                    if (!teamToPointsMap.containsKey(match.getHomeTeam())) {
                        teamToPointsMap.put(match.getHomeTeam(), 0);
                        teamToTableEntryMap.put(match.getHomeTeam(), new TableEntry(match.getHomeTeam().getName()));
                    }
                    if (!teamToPointsMap.containsKey(match.getGuestTeam())) {
                        teamToPointsMap.put(match.getGuestTeam(), 0);
                        teamToTableEntryMap.put(match.getGuestTeam(), new TableEntry(match.getGuestTeam().getName()));
                    }

                    TableEntry homeTableEntry = teamToTableEntryMap.get(match.getHomeTeam());
                    TableEntry guestTableEntry = teamToTableEntryMap.get(match.getGuestTeam());
                    homeTableEntry.setHomeGoals(homeTableEntry.getHomeGoals() + match.getGoalsHomeTeam());
                    homeTableEntry.setReceivedHomeGoals(homeTableEntry.getReceivedHomeGoals() + match.getGoalsGuestTeam());
                    guestTableEntry.setAwayGoals(guestTableEntry.getAwayGoals() + match.getGoalsGuestTeam());
                    guestTableEntry.setReceivedAwayGoals(guestTableEntry.getReceivedAwayGoals() + match.getGoalsHomeTeam());
                    switch (match.getResultType()) {
                        case HOME_WON:
                            teamToPointsMap.put(match.getHomeTeam(), teamToPointsMap.get(match.getHomeTeam()) + 3);
                            homeTableEntry.setPoints(homeTableEntry.getPoints() + 3);
                            homeTableEntry.setHomeGamesWon(homeTableEntry.getHomeGamesWon() + 1);
                            guestTableEntry.setAwayGamesLost(guestTableEntry.getAwayGamesLost() + 1);
                            break;
                        case DRAW:
                            teamToPointsMap.put(match.getHomeTeam(), teamToPointsMap.get(match.getHomeTeam()) + 1);
                            teamToPointsMap.put(match.getGuestTeam(), teamToPointsMap.get(match.getGuestTeam()) + 1);
                            homeTableEntry.setPoints(homeTableEntry.getPoints() + 1);
                            guestTableEntry.setPoints(guestTableEntry.getPoints() + 1);
                            homeTableEntry.setHomeGamesDraw(homeTableEntry.getHomeGamesDraw() + 1);
                            guestTableEntry.setAwayGamesDraw(guestTableEntry.getAwayGamesDraw() + 1);
                            break;
                        case GUEST_WON:
                            teamToPointsMap.put(match.getGuestTeam(), teamToPointsMap.get(match.getGuestTeam()) + 3);
                            guestTableEntry.setPoints(guestTableEntry.getPoints() + 3);
                            homeTableEntry.setHomeGamesLost(homeTableEntry.getHomeGamesLost() + 1);
                            guestTableEntry.setAwayGamesWon(guestTableEntry.getAwayGamesWon() + 1);
                            break;
                        default:
                            System.out.println("unknown match result type: " + match.getResultType());
                            break;
                    }
                }
            }
        }

        final Table table = new Table();
        if (!teamToPointsMap.isEmpty()) {
            TreeMap<Team, Integer> sortedTeamToPointsMap = Maps.newTreeMap(new TeamValueComparator(teamToTableEntryMap));
            sortedTeamToPointsMap.putAll(teamToPointsMap);
            int place = 1;
            for (Team team : sortedTeamToPointsMap.keySet()) {
                TableEntry tableEntry = teamToTableEntryMap.get(team);
                tableEntry.setPlace(place);
                place++;
                table.addEntry(tableEntry);
            }

            System.out.println("GENERATE CHART END");
            matchDayToTable.put(day, table);
        }
        return table;
    }
}
