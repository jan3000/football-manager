package de.footballmanager.backend.domain.league;

import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.KindOfGoal;

public class Goal {

    private int minute;
    private String team;
    //    private Team team;
    private Player scorer;
    private Result newResult;
    private KindOfGoal kindOfGoal;

    public Goal() {
    }

    public Goal(final int minute, final String team, final Player scorer, final KindOfGoal kindOfGoal,
                final Result newResult) {
        super();
        this.minute = minute;
        this.team = team;
        this.scorer = scorer;
        this.kindOfGoal = kindOfGoal;
        this.newResult = newResult;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(final int minute) {
        this.minute = minute;
    }

    public Player getScorer() {
        return scorer;
    }

    public void setScorer(final Player scorer) {
        this.scorer = scorer;
    }

    public KindOfGoal getKindOfGoal() {
        return kindOfGoal;
    }

    public void setKindOfGoal(final KindOfGoal kindOfGoal) {
        this.kindOfGoal = kindOfGoal;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(final String team) {
        this.team = team;
    }

    public Result getNewResult() {
        return newResult;
    }

    public void setNewResult(final Result newResult) {
        this.newResult = newResult;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Goal [minute=");
        builder.append(minute);
        builder.append(", team=");
        builder.append(team);
        builder.append(", scorer=");
        builder.append(scorer);
        builder.append(", newResult=");
        builder.append(newResult);
        builder.append(", kindOfGoal=");
        builder.append(kindOfGoal);
        builder.append("]");
        return builder.toString();
    }

}
