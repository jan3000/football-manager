package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import de.footballmanager.backend.comparator.TeamValueComparator;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.parser.LeagueParser;
import de.footballmanager.backend.parser.PlayerParser;
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
    private LeagueParser leagueParser;
    @Autowired
    private PlayerParser playerParser;
    @Autowired
    private ResultService resultService;
    @Autowired
    private TrialAndErrorTimeTableService timeTableService;

    private League league;
    private TimeTable timeTable;
    private Map<Integer, Table> matchDayToTable = Maps.newHashMap();

    @PostConstruct
    public void initLeague() {
        try {
            if (league == null) {
                System.out.println("INIT STARTED");
                league = leagueParser.parse();
                timeTable = timeTableService.createTimeTable(league.getTeams());
                playerParser.parsePlayerForLeague(league);
                System.out.println("INIT FINISHED");
            }
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Team> getTeams() {
        return league.getTeams();
    }

    public MatchDay runNextMinute() {
        MatchDay matchDay = timeTable.getMatchDay(timeTable.getCurrentMatchDay());
        List<Match> matches = matchDay.getMatches();
        resultService.calculateNextMinute(matches);

        if (haveAllMatchesEnded(matches)) {
            generateChart(timeTable.getCurrentMatchDay());
            timeTable.incrementCurrentMatchDay();
        }
        ;

        return matchDay;
    }

    private boolean haveAllMatchesEnded(List<Match> matches) {
        return Collections2.filter(matches, Match::isFinished).size() == matches.size();
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public int getCurrentMatchDay() {
        return timeTable.getCurrentMatchDay();
    }

    public MatchDay getTimeTableForMatchDay(int matchDay) {
        initLeague();
        System.out.println("return match day");
        return timeTable.getMatchDay(matchDay);
    }

    public Table getCurrentTable() {
        Preconditions.checkArgument(timeTable != null, "timeTable must not be null");
        return getTable(timeTable.getCurrentMatchDay());
    }

    public Map<Integer, Table> getMatchDayToTable() {
        return matchDayToTable;
    }

    public Table getTable(int day) {
        if (matchDayToTable.containsKey(day)) {
            return matchDayToTable.get(day);
        } else {
            return matchDayToTable.get(timeTable.getCurrentMatchDay());
        }
    }

    protected Table generateChart(int day) {
        System.out.println("GENERATE CHART FOR DAY: " + day);
        Map<Team, Integer> teamToPointsMap = Maps.newHashMap();
        Map<Team, TableEntry> teamToTableEntryMap = Maps.newHashMap();

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
