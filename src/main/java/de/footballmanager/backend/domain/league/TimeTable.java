package de.footballmanager.backend.domain.league;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

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
        Preconditions.checkArgument(matchDayNumber <= matchDays.size(), "matchDayNumber must not exceed " +
                matchDays.size());
        return matchDays.get(matchDayNumber - 1);
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
