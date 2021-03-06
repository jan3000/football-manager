package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.TimeTable;
import de.footballmanager.backend.exception.TimeTableCreationStuckException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class TrialAndErrorTimeTableService extends TimeTableService {

    @Autowired
    private DateService dateService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private ClubService clubService;

    private static final int DURATION_WINTER_BREAK = 6;

    private List<DateTime> getMatchDates(DateTime startDate, int numberOfTeams) {

        int numberOfMatchDays = numberOfTeams - 1;
        List<DateTime> dates = Lists.newArrayListWithCapacity(numberOfMatchDays * 2);
        IntStream.range(0, numberOfMatchDays).forEach(i -> dates.add(startDate.plusWeeks(i)));
        final DateTime startDateSecondHalf = startDate.plusWeeks(numberOfMatchDays).plusWeeks(DURATION_WINTER_BREAK);
        IntStream.range(0, numberOfMatchDays).forEach(i -> dates.add(startDateSecondHalf.plusWeeks(i)));
        return dates;
    }

    @Override
    public TimeTable createTimeTable(final List<Team> teams, DateTime startDate) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(teams),
                "if you like to create a timeTable, please pass some teams");
        Preconditions.checkNotNull(startDate, "startDate must be set for time table creation");
        startDate = dateService.setDayTime(startDate, 15, 30);

        List<Match> allFirstRoundMatches = buildAllMatchesOfFirstRound(teams);
        List<MatchDay> firstRoundMatchDays = buildAllPossibleMatchDayPermutationsRetry(teams, allFirstRoundMatches);
        List<MatchDay> secondRoundMatchDays = getSecondRoundMatches(firstRoundMatchDays);

        List<MatchDay> allMatchDays = Lists.newArrayList(firstRoundMatchDays);
        allMatchDays.addAll(secondRoundMatchDays);
        List<DateTime> matchDates = getMatchDates(startDate, teams.size());
        allMatchDays.forEach(match -> {
            DateTime remove = matchDates.remove(0);
            match.setDate(remove);
        });
        return new TimeTable(allMatchDays);
    }


    List<MatchDay> getSecondRoundMatches(final List<MatchDay> firstRoundMatches) {
        Preconditions.checkNotNull(firstRoundMatches, "firstRoundMatches must be set to add secondRoundMatches");

        List<MatchDay> secondRoundMatchDays = Lists.newArrayList();
        int numberOfMatchDaysFirstRound = firstRoundMatches.size();
        int counter = 1;
        for (MatchDay matchDay : firstRoundMatches) {
            MatchDay returnGameMatchDay = new MatchDay();
            returnGameMatchDay.setMatchDayNumber(numberOfMatchDaysFirstRound + counter);
            for (Match match : matchDay.getMatches()) {
                returnGameMatchDay.addMatch(switchTeamsOfMatch(match));
            }
            secondRoundMatchDays.add(returnGameMatchDay);
            counter++;
        }
        return secondRoundMatchDays;
    }

    protected List<MatchDay> buildAllPossibleMatchDayPermutationsRetry(final List<Team> teams,
                                                                       final List<Match> allFirstRoundMatches) {

        List<MatchDay> result = null;
        boolean goOn = true;
        while (goOn) {
            try {
                result = buildAllPossibleMatchDayPermutations(teams, allFirstRoundMatches);
                goOn = false;
            } catch (TimeTableCreationStuckException e) {
                goOn = true;
            }
        }
        return result;
    }

    protected List<MatchDay> buildAllPossibleMatchDayPermutations(final List<Team> teams,
                                                                  final List<Match> allFirstRoundMatches) throws TimeTableCreationStuckException {
        int numberOfMatchDays = getNumberOfMatchDaysOfOneRound(teams);
        MatchDay[] newMatchDays = new MatchDay[numberOfMatchDays];

        // add all second round matches
        List<Match> allPossibleMatches = Lists.newArrayList();
        for (Match match : allFirstRoundMatches) {
            allPossibleMatches.add(match);
            allPossibleMatches.add(switchTeamsOfMatch(match));
        }

        for (int i = 1; i <= newMatchDays.length; i++) {
            newMatchDays[i - 1] = addNextMatchDayBasedOnScoring(newMatchDays, allPossibleMatches, teams.size() / 2);
            removeMatchesOfMatchDayFromList(allPossibleMatches, newMatchDays[i - 1]);

        }

        return Arrays.asList(newMatchDays);
    }

    protected MatchDay addNextMatchDayBasedOnScoring(final MatchDay[] formerMatchDays,
                                                     final List<Match> stillAvailableMatches, final int numberOfMatchesPerMatchDay)
            throws TimeTableCreationStuckException {

        // get number of new match day
        int numberOfFormerMatchDays = 0;
        for (MatchDay matchDay : formerMatchDays) {
            if (matchDay != null) {
                numberOfFormerMatchDays++;
            }
        }
        MatchDay newMatchDay = new MatchDay();
        newMatchDay.setMatchDayNumber(numberOfFormerMatchDays + 1);
        MatchDay formerMatchDay = null;
        if (numberOfFormerMatchDays > 0) {
            formerMatchDay = formerMatchDays[numberOfFormerMatchDays - 1];
        }

        // create second matchDay as the one with lowest score from all existing
        // matches
        Map<Match, Integer> matchToScore = calculateScoreMapping(stillAvailableMatches, formerMatchDay);

        int minimalValue = 100;
        int maxValue = 0;
        for (Match match : matchToScore.keySet()) {
            if (matchToScore.get(match) < minimalValue) {
                minimalValue = matchToScore.get(match);
            }
            if (matchToScore.get(match) > maxValue) {
                maxValue = matchToScore.get(match);
            }
        }
        int counter = 0;
        int resetCounter = 0;
        while (newMatchDay.getMatches().size() < numberOfMatchesPerMatchDay) {
            if (counter > maxValue) {
                resetCounter++;
                if (resetCounter > 100) {
                    throw new TimeTableCreationStuckException(String.format("stuck after reset %s on day %s",
                            resetCounter, newMatchDay.getMatchDayNumber()));
                }
                newMatchDay.getMatches().clear();
                counter = 0;

            }
            addMatchesWithMinimalScore(newMatchDay, matchToScore, minimalValue + counter);
            counter++;
        }

        return newMatchDay;
    }

    protected void addMatchesWithMinimalScore(final MatchDay newMatchDay,
                                              final Map<Match, Integer> matchToScore, final int minimalValue) {
        // get all matches with minimal value
        List<Match> matchesWithMinimalScore = getMatchesWithSameScore(matchToScore, minimalValue);
        addMatchesToMatchDayIfNotContainedAlready(newMatchDay, matchesWithMinimalScore);
    }

    protected List<Match> getMatchesWithSameScore(final Map<Match, Integer> matchToScore, final int score) {
        List<Match> matchesWithMinimalScore = Lists.newArrayList();
        List<Entry<Match, Integer>> matchToScoreList = Lists.newArrayList(matchToScore.entrySet());
        Random random = new Random();
        List<Integer> alreadyUsedRandomNumbers = Lists.newArrayList();

        int randomNumber = random.nextInt(matchToScoreList.size());
        for (int i = 0; i < matchToScoreList.size(); i++) {
            while (alreadyUsedRandomNumbers.contains(randomNumber)) {
                randomNumber = random.nextInt(matchToScoreList.size());
            }
            alreadyUsedRandomNumbers.add(randomNumber);
            if (matchToScoreList.get(randomNumber).getValue() <= score) {
                matchesWithMinimalScore.add(matchToScoreList.get(randomNumber).getKey());
            }
        }
        return matchesWithMinimalScore;
    }

    protected void addMatchesToMatchDayIfNotContainedAlready(final MatchDay matchDay,
                                                             final List<Match> possibleMatchesToAdd) {
        Preconditions.checkNotNull(matchDay, "matchDay must be set");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(possibleMatchesToAdd));
        for (Match match : possibleMatchesToAdd) {
            if (isTeamNotInMatchDay(matchDay, match.getHomeTeam())
                    && isTeamNotInMatchDay(matchDay, match.getGuestTeam())) {
                matchDay.addMatch(match);
            }
        }
    }

    protected boolean isTeamNotInMatchDay(final MatchDay matchDay, final String teamName) {
        Preconditions.checkNotNull(matchDay, "matchDay should not be null in isTeamNotInMatchDay");
        return !matchService.containsTeam(matchDay, teamName);
    }

    protected Map<Match, Integer> calculateScoreMapping(final List<Match> allPossibleMatches,
                                                        final MatchDay formerMatchDay) {
        Map<Match, Integer> matchToScore = Maps.newHashMap();
        for (Match match : allPossibleMatches) {

            matchToScore.put(match, 0);
            if (formerMatchDay != null) {
                if (isHomeTeam(formerMatchDay, match.getHomeTeam())) {
                    matchToScore.put(match, matchToScore.get(match) + 1);
                }

                if (isGuestTeam(formerMatchDay, match.getGuestTeam())) {
                    matchToScore.put(match, matchToScore.get(match) + 1);
                }
            }

        }
        return matchToScore;
    }

    protected boolean isHomeTeam(final MatchDay matchDay, final String teamName) {
        Team team = clubService.getTeam(teamName);
        for (Match match : matchDay.getMatches()) {
            if (match.getHomeTeam().equals(team)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isGuestTeam(final MatchDay matchDay, final String teamName) {
        Team team = clubService.getTeam(teamName);
        for (Match match : matchDay.getMatches()) {
            if (match.getGuestTeam().equals(team)) {
                return true;
            }
        }
        return false;
    }

    protected void removeMatchesOfMatchDayFromList(final List<Match> matches, final MatchDay matchDayToRemove) {
        for (Match match : matchDayToRemove.getMatches()) {
            matches.remove(match);
            matches.remove(switchTeamsOfMatch(match));
        }
    }

    protected Match switchTeamsOfMatch(final Match match) {
        Match switchedMatch = new Match();
        switchedMatch.setHomeTeam(match.getGuestTeam());
        switchedMatch.setGuestTeam(match.getHomeTeam());
        return switchedMatch;
    }

    protected List<Match> buildAllMatchesOfFirstRound(final List<Team> teams) {
        List<Match> firstRoundMatches = Lists.newArrayList();
        for (int i = 0; i < teams.size() - 1; i++) {
            for (int h = 1; h < teams.size(); h++) {
                if (h > i) {
                    Match match = new Match();
                    match.setHomeTeam(teams.get(i).getName());
                    match.setGuestTeam(teams.get(h).getName());
                    firstRoundMatches.add(match);
                }
            }
        }
        return firstRoundMatches;
    }

    protected int getTotalNumberOfMatchDays(final List<Team> teams) {
        return getNumberOfMatchDaysOfOneRound(teams) * 2;
    }

    protected int getNumberOfMatchDaysOfOneRound(final List<Team> teams) {
        return teams.size() - 1;
    }
}
