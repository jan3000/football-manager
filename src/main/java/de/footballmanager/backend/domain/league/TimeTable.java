package de.footballmanager.backend.domain.league;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class TimeTable {

    private boolean closed = false;
    private int currentMatchDay = 1;
    private final List<MatchDay> matchDays;

    public TimeTable(List<MatchDay> allMatchDays) {
        this.matchDays = allMatchDays;
    }

    public int getNumberOfMatchDays() {
        return matchDays.size();
    }

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

    public void incrementCurrentMatchDay() {
        Preconditions.checkArgument(currentMatchDay < matchDays.size(),
                "current match day cannot be higher than number of matchDays");
        currentMatchDay++;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed() {
        this.closed = true;
    }

    public String print() {
        StringBuilder buildi = new StringBuilder();
        buildi.append("TimeTable\n");
        for (MatchDay matchDay : matchDays) {
            buildi.append(String.format("%s\n", StringUtils.repeat("-", 50)));
            buildi.append(String.format("MatchDay %s (%s):\n", matchDay.getMatchDayNumber(), matchDay.getDate()));
            for (Match match : matchDay.getMatches()) {
                buildi.append(String.format("%s", match.printMatch()));
            }
        }
        return buildi.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeTable timeTable = (TimeTable) o;
        return closed == timeTable.closed &&
                currentMatchDay == timeTable.currentMatchDay &&
                Objects.equals(matchDays, timeTable.matchDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(closed, currentMatchDay, matchDays);
    }

    @Override
    public String toString() {
        return "TimeTable{" +
                ", closed=" + closed +
                ", currentMatchDay=" + currentMatchDay +
                ", matchDays=" + matchDays +
                '}';
    }
}
