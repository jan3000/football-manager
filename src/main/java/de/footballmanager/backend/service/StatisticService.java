package de.footballmanager.backend.service;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticService {


    @Autowired
    private LeagueService leagueService;

    Map<String, List<Integer>> teamToPlace = Maps.newHashMap();

    public TeamStatistic getTeamStatistics(TimeTable timeTable, String teamName) {
        TeamStatistic teamStatistic = new TeamStatistic(teamName);
        Integer[] homeGoals = teamStatistic.getHomeGoals();
        Integer[] awayGoals = teamStatistic.getAwayGoals();
        Integer[] totalGoals = teamStatistic.getTotalGoals();
        Integer[] receivedHomeGoals = teamStatistic.getReceivedHomeGoals();
        Integer[] receivedAwayGoals = teamStatistic.getReceivedAwayGoals();
        Integer[] receivedTotalGoals = teamStatistic.getReceivedTotalGoals();
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                if (match.getHomeTeam().getName().equals(teamName)) {
                    addGoalsToTimeline(teamName, homeGoals, totalGoals, receivedHomeGoals, receivedTotalGoals, match);
                }
                if (match.getGuestTeam().getName().equals(teamName)) {
                    addGoalsToTimeline(teamName, awayGoals, totalGoals, receivedAwayGoals, receivedTotalGoals, match);
                }
            }
        }

        Table table = leagueService.getCurrentTable();
        teamStatistic.setCurrentTableEntry(table.getEntryByTeamName(teamName));

        int currentMatchDay = leagueService.getCurrentMatchDay();
        for (int i = 0; i < currentMatchDay; i++) {
            teamStatistic.getPlacementsInSeason()[i] = leagueService.getTable(i+1).getEntryByTeamName(teamName).getPlace();
        }


        return teamStatistic;
    }

    private void addGoalsToTimeline(String teamName, Integer[] goals, Integer[] totalGoals, Integer[] receivedGoals,
                                    Integer[] receivedTotalGoals, Match match) {
        for (Goal goal : match.getGoals()) {
            if (goal.getTeam().getName().equals(teamName)) {
                goals[goal.getMinute() - 1]++;
                totalGoals[goal.getMinute() -1]++;
            } else {
                receivedGoals[goal.getMinute() - 1]++;
                receivedTotalGoals[goal.getMinute() - 1]++;
            }
        }
    }


}
