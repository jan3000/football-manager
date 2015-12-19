package de.footballmanager.backend.service;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticService {

    Map<String, List<Integer>> teamToPlace = Maps.newHashMap();

    public TeamStatistic getGoalDistribution(TimeTable timeTable, String teamName) {
        TeamStatistic teamStatistic = new TeamStatistic(teamName);
        Integer[] homeGoals = teamStatistic.getHomeGoals();
        Integer[] awayGoals = teamStatistic.getAwayGoals();
        Integer[] totalGoals = teamStatistic.getTotalGoals();
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                if (match.getHomeTeam().getName().equals(teamName)) {
                    for (Goal goal : match.getGoals()) {
                        if (goal.getTeam().getName().equals(teamName)) {
                            homeGoals[goal.getMinute() - 1]++;
                            totalGoals[goal.getMinute() -1]++;
                        }
                    }
                }
                if (match.getGuestTeam().getName().equals(teamName)) {
                    for (Goal goal : match.getGoals()) {
                        if (goal.getTeam().getName().equals(teamName)) {
                            awayGoals[goal.getMinute() -1]++;
                            totalGoals[goal.getMinute() -1]++;
                        }
                    }
                }
            }
        }
        return teamStatistic;
    }


}
