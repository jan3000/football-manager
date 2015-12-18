package de.footballmanager.backend.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import de.footballmanager.backend.comparator.TeamValueComparator;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.parser.LeagueParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    private League league;
    private TimeTable timeTable;

    public void initLeague() {
        try {
            if (league == null) {
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
        MatchDay matchDay = timeTable.getMatchDay(timeTable.getCurrentMatchDay());
        List<Match> matches = matchDay.getMatches();
        resultService.calculateNextMinute(matches);

        if (haveAllMatchesEnded(matches)) {
            timeTable.incrementCurrentMatchDay();
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
        MatchDay matchDay = timeTable.getMatchDay(timeTable.getCurrentMatchDay());
        for (Match match : matchDay.getMatches()) {
            resultService.calculateResult(match);
        }
        timeTable.incrementCurrentMatchDay();;
        return timeTable.getMatchDay(timeTable.getCurrentMatchDay() - 1);
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public int getCurrentMatchDay() {
        return timeTable.getCurrentMatchDay();
    }

    public MatchDay getTimeTableForMatchDay(int matchDay) {
        initLeague();
        return timeTable.getMatchDay(matchDay);
    }

    public Table getCurrentTable() {
        Map<Team, Integer> teamToPointsMap = Maps.newHashMap();
        Map<Team, TableEntry> teamToTableEntryMap = Maps.newHashMap();

        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                if (match.isFinished()) {
                    if (!teamToPointsMap.containsKey(match.getHomeTeam())) {
                        teamToPointsMap.put(match.getHomeTeam(), 0);
                        teamToTableEntryMap.put(match.getHomeTeam(), new TableEntry(match.getHomeTeam()));
                    }
                    if (!teamToPointsMap.containsKey(match.getGuestTeam())) {
                        teamToPointsMap.put(match.getGuestTeam(), 0);
                        teamToTableEntryMap.put(match.getGuestTeam(), new TableEntry(match.getGuestTeam()));
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

        TreeMap<Team, Integer> sortedTeamToPointsMap = Maps.newTreeMap(new TeamValueComparator(teamToTableEntryMap));
        sortedTeamToPointsMap.putAll(teamToPointsMap);
        final Table table = new Table();
        int i = 1;
        for (Team team : sortedTeamToPointsMap.keySet()) {
            TableEntry tableEntry = teamToTableEntryMap.get(team);
            tableEntry.setPlace(i);
            i++;
            table.getEntries().add(tableEntry);
        }

        return table;
    }
}
