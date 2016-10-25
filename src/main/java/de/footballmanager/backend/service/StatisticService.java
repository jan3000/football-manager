package de.footballmanager.backend.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.footballmanager.backend.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class StatisticService {

    public TeamStatistic getTeamStatistics(TimeTable timeTable, String teamName, Table currentTable, Map<Integer, Table> matchDayToTable) {
        TeamStatistic teamStatistic = new TeamStatistic(teamName);
        Integer[] homeGoals = teamStatistic.getHomeGoals();
        Integer[] awayGoals = teamStatistic.getAwayGoals();
        Integer[] totalGoals = teamStatistic.getTotalGoals();
        Integer[] receivedHomeGoals = teamStatistic.getReceivedHomeGoals();
        Integer[] receivedAwayGoals = teamStatistic.getReceivedAwayGoals();
        Integer[] receivedTotalGoals = teamStatistic.getReceivedTotalGoals();

        Set<Player> scorers = Sets.newHashSet();
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {

                scorers.addAll(match.getGoals().stream()
                        .filter(goal -> teamName.equals(goal.getTeam().getName()))
                        .map(Goal::getScorer)
                        .collect(toSet()));


                if (isHomeTeam(teamName, match)) {
                    addGoalsToTimeline(teamName, homeGoals, totalGoals, receivedHomeGoals, receivedTotalGoals, match);

                }
                if (isGuestTeam(teamName, match)) {
                    addGoalsToTimeline(teamName, awayGoals, totalGoals, receivedAwayGoals, receivedTotalGoals, match);
                }
            }
        }

        teamStatistic.setCurrentTableEntry(currentTable.getEntryByTeamName(teamName));

        teamStatistic.setPlacementsInSeason(getPlacementsInSeason(teamName, matchDayToTable, timeTable));


        teamStatistic.setScorers(scorers);

        return teamStatistic;
    }

    private boolean isGuestTeam(String teamName, Match match) {
        return match.getGuestTeam().getName().equals(teamName);
    }

    private boolean isHomeTeam(String teamName, Match match) {
        return match.getHomeTeam().getName().equals(teamName);
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

    public Integer[] getPlacementsInSeason(String teamName, Map<Integer, Table> matchDayToTable, TimeTable timeTable) {
        int numberOfMatchDays = timeTable.getAllMatchDays().size();
        Integer[] placements = new Integer[numberOfMatchDays];
        int currentMatchDay = timeTable.getCurrentMatchDay();
        int limit = currentMatchDay - 1;
        if (timeTable.isClosed()) {
            limit = currentMatchDay;
        }
        for (int i = 0; i < limit; i++) {
            placements[i] = matchDayToTable.get(i + 1).getEntryByTeamName(teamName).getPlace();
        }
        return placements;
    }


    public List<ScorerStatistic> getScorerTable(List<Team> teams, TimeTable timeTable) {
        List<ScorerStatistic> scorerStatistics = Lists.newArrayList();
        for (Team team : teams) {
            Map<Player, Integer> scorersToGoals = getScorers(team.getName(), timeTable);
            for (Player player : scorersToGoals.keySet()) {
                scorerStatistics.add(new ScorerStatistic(player.getFullname(), team.getName(),
                        scorersToGoals.get(player)));
            }
        }

        return scorerStatistics.stream().sorted().collect(toList());
    }


    private Map<Player, Integer> getScorers(String teamName, TimeTable timeTable) {
        Map<Player, Integer> scorerToGoals = Maps.newHashMap();
        ImmutableList<MatchDay> allMatchDays = timeTable.getAllMatchDays();
        for (MatchDay matchDay : allMatchDays) {
            if (matchDay.isFinished()) {
                Match matchOfTeam = matchDay.getMatchOfTeam(teamName);
                for (Goal goal : matchOfTeam.getGoals()) {
                    if (goal.getTeam().getName().equals(teamName)) {
                        Player scorer = goal.getScorer();
                        if (!scorerToGoals.containsKey(scorer)) {
                            scorerToGoals.put(scorer, 1);
                        } else {
                            scorerToGoals.put(scorer, scorerToGoals.get(scorer) + 1);
                        }
                    }
                }
            }
        }
        return scorerToGoals;
    }


}
