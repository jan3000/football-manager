package de.footballmanager.backend.domain.league;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.serializer.CustomDateSerializer;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;


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

    @JsonSerialize(using = CustomDateSerializer.class)
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
