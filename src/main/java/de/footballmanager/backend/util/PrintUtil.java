package de.footballmanager.backend.util;

import de.footballmanager.backend.domain.league.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PrintUtil {

    public static String print(TimeTable timeTable) {
        StringBuilder buildi = new StringBuilder();
        buildi.append("TimeTable\n");
        for (MatchDay matchDay : timeTable.getAllMatchDays()) {
            buildi.append(String.format("%s\n", StringUtils.repeat("-", 50)));
            buildi.append(String.format("MatchDay %s (%s):\n", matchDay.getMatchDayNumber(), matchDay.getDate()));
            for (Match match : matchDay.getMatches()) {
                buildi.append(String.format("%s", print(match)));
            }
        }
        return buildi.toString();
    }



    public static String print(Table table) {

        List<TableEntry> tableEntries = table.getTableEntriesSorted();
        StringBuffer buffer = new StringBuffer();
        int place = 1;
        buffer.append(getTableHead());
        for (TableEntry tableEntry : tableEntries) {
            buffer.append(place + ") " + print(tableEntry));
            place++;
        }
        return buffer.toString();
    }

    public static String getTableHead() {
        StringBuffer buffi = new StringBuffer();
        buffi.append(fillUpSpaces("") + "\t\t");
        buffi.append("Total" + "\t     ");
        buffi.append("Home" + "\t     ");
        buffi.append("Away" + "\t     ");
        buffi.append("Goals" + "\t\t");
        buffi.append("Points" + "\t\n");
        return buffi.toString();
    }

    public static String print(TableEntry tableEntry) {
        StringBuffer buffi = new StringBuffer();
        buffi.append(fillUpSpaces(tableEntry.getTeam()) + "\t");
        buffi.append(tableEntry.getTotalGamesWon() + " | ");
        buffi.append(tableEntry.getTotalGamesDraw() + " | ");
        buffi.append(tableEntry.getTotalGamesLost() + "\t");
        buffi.append(tableEntry.getHomeGamesWon() + " | ");
        buffi.append(tableEntry.getHomeGamesDraw() + " | ");
        buffi.append(tableEntry.getHomeGamesLost() + "\t");
        buffi.append(tableEntry.getAwayGamesWon() + " | ");
        buffi.append(tableEntry.getAwayGamesDraw() + " | ");
        buffi.append(tableEntry.getAwayGamesLost() + "\t");
        buffi.append(tableEntry.getTotalGoals() + " : ");
        buffi.append(tableEntry.getTotalReceivedGoals() + "\t\t");
        buffi.append(tableEntry.getPoints() + "\t\n");
        return buffi.toString();
    }



    public static String print(MatchDay matchDay) {
        StringBuilder buffi = new StringBuilder();
        buffi.append("MatchDay: ");
        buffi.append(matchDay.getMatchDayNumber());
        buffi.append("\n");
        for (Match match : matchDay.getMatches()) {
            buffi.append(print(match));
            buffi.append("\n");
        }
        return buffi.toString();
    }

    public static String printCompact(MatchDay matchDay) {
        StringBuilder buffi = new StringBuilder();
        buffi.append("MatchDay: ");
        buffi.append(matchDay.getMatchDayNumber());
        buffi.append("\n");
        for (Match match : matchDay.getMatches()) {
            buffi.append(printCompact(match));
            buffi.append("\n");
        }
        return buffi.toString();
    }


    public static String print(Match match) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s \t- \t%s \t%s : %s  (%s)", fillUpSpaces(match.getHomeTeam()),
                fillUpSpaces(match.getGuestTeam()),
                match.getResult().getHomeGoals(), match.getResult().getGuestGoals(), match.getHalfTimeResult().print()));
        for (Goal goal : match.getGoals()) {
            builder.append(String.format("\n%s. Minute\t%s", goal.getMinute(), goal.getNewResult().print()));
        }
        return builder.toString();
    }


    public static String printCompact(Match match) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s \t- \t%s \t%s : %s  (%s)", fillUpSpaces(match.getHomeTeam()),
                fillUpSpaces(match.getGuestTeam()),
                match.getResult().getHomeGoals(), match.getResult().getGuestGoals(), match.getHalfTimeResult().print()));
        return builder.toString();
    }

    private static String fillUpSpaces(String string) {
        String spaces = "                         ";
        return string + spaces.substring(string.length());
    }

}
