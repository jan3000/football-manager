package de.footballmanager.backend.domain;

public class TeamStatistic {

    private static final int MINUTES = 90;

    private final String teamName;
    private TableEntry currentTableEntry;

    private final Integer[] homeGoals = new Integer[MINUTES];
    private final Integer[] awayGoals = new Integer[MINUTES];
    private final Integer[] totalGoals = new Integer[MINUTES];

    private final Integer[] receivedHomeGoals = new Integer[MINUTES];
    private final Integer[] receivedAwayGoals = new Integer[MINUTES];
    private final Integer[] receivedTotalGoals = new Integer[MINUTES];


    private Integer[] placementsInSeason = new Integer[34];



    public TeamStatistic(String teamName) {
        this.teamName = teamName;
        for (int i = 0; i < MINUTES; i++) {
            homeGoals[i] = 0;
            awayGoals[i] = 0;
            totalGoals[i] = 0;
            receivedHomeGoals[i] = 0;
            receivedAwayGoals[i] = 0;
            receivedTotalGoals[i] = 0;

        }
    }

    public String getTeamName() {
        return teamName;
    }

    public TableEntry getCurrentTableEntry() {
        return currentTableEntry;
    }

    public void setCurrentTableEntry(TableEntry currentTableEntry) {
        this.currentTableEntry = currentTableEntry;
    }

    public Integer[] getPlacementsInSeason() {
        return placementsInSeason;
    }
    public Integer[] getHomeGoals() {
        return homeGoals;
    }

    public Integer[] getAwayGoals() {
        return awayGoals;
    }

    public Integer[] getTotalGoals() {
        return totalGoals;
    }

    public Integer[] getReceivedHomeGoals() {
        return receivedHomeGoals;
    }

    public Integer[] getReceivedAwayGoals() {
        return receivedAwayGoals;
    }

    public Integer[] getReceivedTotalGoals() {
        return receivedTotalGoals;
    }
}
