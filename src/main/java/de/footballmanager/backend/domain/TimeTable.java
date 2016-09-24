package de.footballmanager.backend.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

public class TimeTable {

    private DateTime startDateOfSeason;
    private int currentMatchDay = 1;
    private final List<MatchDay> matchDays = Lists.newArrayList();

    public ImmutableList<MatchDay> getAllMatchDays() {
        return ImmutableList.copyOf(matchDays);
    }

    public MatchDay getMatchDay(final int matchDayNumber) {
        Preconditions.checkArgument(matchDayNumber >= 1, "matchDay must be greater 0");
        Preconditions.checkArgument(matchDayNumber <= matchDays.size(), "matchDay number must not exceed " +
                matchDays.size());
        return matchDays.get(matchDayNumber - 1);
    }

    public Match getMatch(int matchDayNumber, String teamName) {
        MatchDay matchDay = getMatchDay(matchDayNumber);
        return matchDay.getMatchOfTeam(teamName);
    }

    public int getCurrentMatchDay() {
        return currentMatchDay;
    }

    public void setCurrentMatchDay(int currentMatchDay) {
        this.currentMatchDay = currentMatchDay;
    }

    public void incrementCurrentMatchDay() {
        currentMatchDay++;
    }


    public void addMatchDays(final List<MatchDay> matchDays) {
        this.matchDays.addAll(matchDays);
    }

    public String print() {
        StringBuilder buildi = new StringBuilder();
        buildi.append("TimeTable\n");
        for (MatchDay matchDay : matchDays) {
            buildi.append(String.format("%s\n", StringUtils.repeat("-", 50)));
            buildi.append(String.format("MatchDay %s:\n", matchDay.getMatchDayNumber()));
            for (Match match : matchDay.getMatches()) {
                buildi.append(String.format("%s", match.printMatch()));
            }
        }
        return buildi.toString();
    }
}
