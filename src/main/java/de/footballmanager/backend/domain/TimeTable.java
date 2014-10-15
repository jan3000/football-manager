package de.footballmanager.backend.domain;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TimeTable {

    private final List<MatchDay> matchDays = Lists.newArrayList();

    public int getNumberOfMatchesOnOneMatchDay() {
        throw new UnsupportedOperationException();
    }

    public int getNumberOfMatchDays() {
        throw new UnsupportedOperationException();
    }

    public ImmutableList<MatchDay> getAllMatchDays() {
        return ImmutableList.copyOf(matchDays);
    }

    public MatchDay getMatchDay(final int matchDayNumber) {
        throw new UnsupportedOperationException();
    }

    public void addMatchDays(final List<MatchDay> matchDays) {
        this.matchDays.addAll(matchDays);
    }

    public String print() {
        StringBuffer buffi = new StringBuffer();
        buffi.append("TimeTable\n");
        for (MatchDay matchDay : matchDays) {
            buffi.append(String.format("%s\n", StringUtils.repeat("-", 50)));
            buffi.append(String.format("MatchDay %s:\n", matchDay.getMatchDayNumber()));
            for (Match match : matchDay.getMatches()) {
                buffi.append(String.format("%s\n", match.printMatch()));
            }
        }
        return buffi.toString();
    }
}
