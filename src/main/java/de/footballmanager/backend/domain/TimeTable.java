package de.footballmanager.backend.domain;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TimeTable {

    private int currentMatchDay = 0;
    private final List<MatchDay> matchDays = Lists.newArrayList();

    public ImmutableList<MatchDay> getAllMatchDays() {
        return ImmutableList.copyOf(matchDays);
    }

    public MatchDay getMatchDay(final int matchDayNumber) {
        Preconditions.checkArgument(matchDayNumber >= 1, "matchDay must be greater 0");
        Preconditions.checkArgument(matchDayNumber <= matchDays.size() - 1, "matchDay must be greater 1");
        MatchDay matchDay = matchDays.get(matchDayNumber - 1);
        System.out.println("matchDay: " + matchDay);
        return matchDay;
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
