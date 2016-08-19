package de.footballmanager.backend.domain;

import com.google.common.base.Preconditions;

public class ScorerStatistic implements Comparable {
    private String player;
    private String team;
    private int goals;

    public ScorerStatistic(String player, String team, int goals) {
        this.player = player;
        this.team = team;
        this.goals = goals;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    @Override
    public int compareTo(Object o) {
        Preconditions.checkArgument(o instanceof ScorerStatistic, "object i not of type ScorerStatistic");
        ScorerStatistic that = (ScorerStatistic) o;
        if (that.getGoals() > this.getGoals()) {
            return 1;
        } else if (that.getGoals() < this.getGoals()) {
            return -1;
        } else {
            return 0;
        }

    }
}
