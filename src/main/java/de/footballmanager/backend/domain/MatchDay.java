package de.footballmanager.backend.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public class MatchDay {


    private DateTime date;
    private int matchDayNumber;
    private List<Match> matches = Lists.newArrayList();

    public MatchDay() {
        super();
    }

    public MatchDay(final List<Match> matches) {
        super();
        this.matches = matches;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(final DateTime date) {
        this.date = date;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public int getNumberOfMatches() {
        return getMatches().size();
    }

    public void setMatches(final List<Match> matches) {
        this.matches = matches;
    }

    public boolean containsMatch(final Match match) {
        return matches.contains(match);
    }

    public boolean containsTeam(final Team team) {
        for (Match match : matches) {
            if (match.containsTeam(team)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHomeTeam(final Team team) {
        for (Match match : matches) {
            if (match.getHomeTeam().equals(team)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGuestTeam(final Team team) {
        for (Match match : matches) {
            if (match.getGuestTeam().equals(team)) {
                return true;
            }
        }
        return false;
    }

    public String toStringAllMatches() {
        StringBuilder builder = new StringBuilder();
        for (Match match : matches) {
            builder.append(match.printMatch());
            builder.append("\n");
        }
        return builder.toString();
    }

    public int getMatchDayNumber() {
        return matchDayNumber;
    }

    public void setMatchDayNumber(final int matchDayNumber) {
        this.matchDayNumber = matchDayNumber;
    }

    public void addMatch(final Match match) {
        matches.add(match);
    }

    public boolean isFinished() {
        return matches.stream().allMatch(Match::isFinished);
    }

    public Match getMatchOfTeam(String teamName) {
        return matches.stream()
                .filter(m -> m.getHomeTeam().getName().equals(teamName) || m.getGuestTeam().getName().equals(teamName))
                .collect(Collectors.toList()).get(0);
    }

    public String print() {
        StringBuilder buffi = new StringBuilder();
        buffi.append("MatchDay: ");
        buffi.append(matchDayNumber);
        buffi.append("\n");
        for (Match match : matches) {
            buffi.append(match.printMatch());
            buffi.append("\n");
        }
        return buffi.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MatchDay [date=");
        builder.append(date);
        builder.append(", matchDayNumber=");
        builder.append(matchDayNumber);
        builder.append(", matches=");
        builder.append(matches);
        builder.append("]");
        return builder.toString();
    }

}
