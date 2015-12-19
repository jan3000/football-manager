package de.footballmanager.backend.domain;

public class TeamStatistic {

    private static final int MINUTES = 90;

    private final String teamName;
    private final Integer[] homeGoals = new Integer[MINUTES];
    private final Integer[] awayGoals = new Integer[MINUTES];
    private final Integer[] totalGoals = new Integer[MINUTES];


    public TeamStatistic(String teamName) {
        this.teamName = teamName;
        for (int i = 0; i < MINUTES; i++) {
            homeGoals[i] = 0;
            awayGoals[i] = 0;
            totalGoals[i] = 0;

        }
    }

    public String getTeamName() {
        return teamName;
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
}
