package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.*;

public class StatisticService {

    public static final int MINUTES = 90;

    public void getGoalDistribution(TimeTable timeTable, Team team) {
        Integer[] homeGoals = new Integer[MINUTES];
        Integer[] awayGoals = new Integer[MINUTES];
        Integer[] totalGoals = new Integer[MINUTES];
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                if (match.getHomeTeam().getName().equals(team.getName())) {
                    for (Goal goal : match.getGoals()) {
                        if (goal.getTeam().getName().equals(team.getName())) {
                            homeGoals[goal.getMinute()] = homeGoals[goal.getMinute()]++;
                            totalGoals[goal.getMinute()] = totalGoals[goal.getMinute()]++;
                        }
                    }
                }
                if (match.getGuestTeam().getName().equals(team.getName())) {
                    for (Goal goal : match.getGoals()) {
                        if (goal.getTeam().getName().equals(team.getName())) {
                            awayGoals[goal.getMinute()] = awayGoals[goal.getMinute()]++;
                            totalGoals[goal.getMinute()] = totalGoals[goal.getMinute()]++;
                        }
                    }
                }
            }
        }
    }


}
