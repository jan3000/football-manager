package de.footballmanager.backend.service;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticService {

    public TeamStatistic getGoalDistribution(TimeTable timeTable, String teamName, Table currentTable) {
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

        teamStatistic.setCurrentTableEntry(currentTable.getEntryByTeamName(teamName));
        return teamStatistic;
    }

    private void addGoalsToTimeline(String teamName, Integer[] goals, Integer[] totalGoals, Integer[] receivedGoals,
                                    Integer[] receivedTotalGoals, Match match) {
        for (Goal goal : match.getGoals()) {
            if (goal.getTeam().getName().equals(teamName)) {
                goals[goal.getMinute() - 1]++;
                totalGoals[goal.getMinute() - 1]++;
            } else {
                receivedGoals[goal.getMinute() - 1]++;
                receivedTotalGoals[goal.getMinute() - 1]++;
            }
        }
    }

    public Integer[] getPlacementsInSeason(String teamName, int currentMatchDay, Map<Integer, Table> matchDayToTable) {
        Integer[] placements = new Integer[34];
        for (int i = 0; i < currentMatchDay - 1; i++) {
            placements[i] = matchDayToTable.get(i + 1).getEntryByTeamName(teamName).getPlace();
        }
        return placements;
    }


}
