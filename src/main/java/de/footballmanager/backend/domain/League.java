package de.footballmanager.backend.domain;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.footballmanager.backend.comparator.TeamValueComparator;
import de.footballmanager.backend.engine.TrialAndErrorTimeTableEngine;

public class League {

    private List<Team> teams;
    private TimeTable timeTable;

    public League() {
        super();
    }

    public League(final List<Team> teams) {
        super();
        Preconditions.checkNotNull("teams must be set to create a league", teams);
        this.teams = teams;
        timeTable = TrialAndErrorTimeTableEngine.createTimeTable(teams);

    }

    public String printCurrentTable() {
        Map<Team, Integer> teamToPointsMap = Maps.newHashMap();
        Map<Team, TableEntry> teamToTableEntryMap = Maps.newHashMap();

        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                if (!teamToPointsMap.containsKey(match.getHomeTeam())) {
                    teamToPointsMap.put(match.getHomeTeam(), 0);
                    teamToTableEntryMap.put(match.getHomeTeam(), new TableEntry());
                }
                if (!teamToPointsMap.containsKey(match.getGuestTeam())) {
                    teamToPointsMap.put(match.getGuestTeam(), 0);
                    teamToTableEntryMap.put(match.getGuestTeam(), new TableEntry());
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
                    homeTableEntry.setHomeGamesWon(homeTableEntry.getHomeGamesWon() + 1);
                    guestTableEntry.setAwayGamesLost(guestTableEntry.getAwayGamesLost() + 1);
                    break;
                case DRAW:
                    teamToPointsMap.put(match.getHomeTeam(), teamToPointsMap.get(match.getHomeTeam()) + 1);
                    teamToPointsMap.put(match.getGuestTeam(), teamToPointsMap.get(match.getGuestTeam()) + 1);
                    homeTableEntry.setHomeGamesDraw(homeTableEntry.getHomeGamesDraw() + 1);
                    guestTableEntry.setAwayGamesDraw(guestTableEntry.getAwayGamesDraw() + 1);
                    break;
                case GUEST_WON:
                    teamToPointsMap.put(match.getGuestTeam(), teamToPointsMap.get(match.getGuestTeam()) + 3);
                    homeTableEntry.setHomeGamesLost(homeTableEntry.getHomeGamesLost() + 1);
                    guestTableEntry.setAwayGamesWon(guestTableEntry.getAwayGamesWon() + 1);
                    break;
                default:
                    System.out.println("ERRORRRR");
                    break;
                }
            }
        }

        TreeMap<Team, Integer> sortedTeamToPointsMap = Maps.newTreeMap(new TeamValueComparator(teamToPointsMap));
        sortedTeamToPointsMap.putAll(teamToPointsMap);

        StringBuffer buffi = new StringBuffer();
        buffi.append("\n");
        for (Entry<Team, Integer> teamToPoints : sortedTeamToPointsMap.entrySet()) {
            buffi.append(teamToTableEntryMap.get(teamToPoints.getKey()).print());
            buffi.append(teamToPoints + "\n");

        }
        return buffi.toString();
    }

    protected int getNumberOfMatchesOnOneDay() {
        return getNumberOfTeams() / 2;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(final TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(final List<Team> teams) {
        this.teams = teams;
    }

    public int getNumberOfTeams() {
        return teams.size();
    }

    public int getNumberOfMatchDays() {
        return (getNumberOfTeams() - 1) * 2;
    }

}
