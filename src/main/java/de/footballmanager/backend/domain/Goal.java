package de.footballmanager.backend.domain;

import de.footballmanager.backend.enumeration.KindOfGoal;

public class Goal {

    int minute;
    Team team;
    Player scorer;
    Result newResult;
    KindOfGoal kindOfGoal;

    public Goal(final int minute, final Team team, final Player scorer, final KindOfGoal kindOfGoal,
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(final Team team) {
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
