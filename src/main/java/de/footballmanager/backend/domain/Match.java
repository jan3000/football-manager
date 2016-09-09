package de.footballmanager.backend.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.enumeration.ResultType;

import java.util.List;
import java.util.Map;

public class Match {

    private Team homeTeam;
    private Team guestTeam;

    private Map<Position, Player> positionPlayerMapHomeTeam = Maps.newHashMap();
    private Map<Position, Player> positionPlayerMapGuestTeam = Maps.newHashMap();

    private int minute = 1;
    private int additionalTime = 0;
    private List<Player> yellowCards;
    private List<Player> redCards;
    private List<PlayerChange> playerChanges;

    private final Result halfTime = new Result(0, 0);
    private Result result = new Result(0, 0);
    private boolean finished = false;
    private final List<Goal> goals = Lists.newArrayList();

    public Match() {
    }

    public Match(final Team homeTeam, final Team guestTeam) {
        super();
        this.homeTeam = homeTeam;
        this.guestTeam = guestTeam;
    }

    public ResultType getResultType() {
        if (result.getHomeGoals() > result.getGuestGoals()) {
            return ResultType.HOME_WON;
        } else if (result.getHomeGoals() < result.getGuestGoals()) {
            return ResultType.GUEST_WON;
        } else {
            return ResultType.DRAW;
        }
    }

    public void increaseGoalsHomeTeam(final Goal goal) {
        if (goal.getMinute() <= 45) {
            halfTime.increaseHomeGoal();
        }
        result.increaseHomeGoal();
        addGoal(goal);
    }

    public void increaseGoalsGuestTeam(final Goal goal) {
        if (goal.getMinute() <= 45) {
            halfTime.increaseGuestGoal();
        }
        result.increaseGuestGoal();
        addGoal(goal);
    }

    public Map<Position, Player> getPositionPlayerMapHomeTeam() {
        return positionPlayerMapHomeTeam;
    }

    public void setPositionPlayerMapHomeTeam(Map<Position, Player> positionPlayerMapHomeTeam) {
        this.positionPlayerMapHomeTeam = positionPlayerMapHomeTeam;
    }

    public Map<Position, Player> getPositionPlayerMapGuestTeam() {
        return positionPlayerMapGuestTeam;
    }

    public void setPositionPlayerMapGuestTeam(Map<Position, Player> positionPlayerMapGuestTeam) {
        this.positionPlayerMapGuestTeam = positionPlayerMapGuestTeam;
    }

    //    public void getAdditionalTime() throws NotSupportedException {
//        throw new NotSupportedException();
//    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public int getGoalsHomeTeam() {
        return result.getHomeGoals();
    }

    public int getGoalsGuestTeam() {
        return result.getGuestGoals();
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(final Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getGuestTeam() {
        return guestTeam;
    }

    public void setGuestTeam(final Team guestTeam) {
        this.guestTeam = guestTeam;
    }

    public void setAdditionalTime(int additionalTime) {
        this.additionalTime = additionalTime;
    }

    public void increaseMinute() {
        minute++;
    }

    public int getMinute() {
        return minute;
    }

    public boolean containsTeam(final Team team) {
        if (isValid()) {
            return homeTeam.equals(team) || guestTeam.equals(team);
        }
        return false;
    }

    public void addGoal(final Goal goal) {
        goals.add(goal);
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public String printMatch() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s \t- \t%s \t%s : %s  (%s)", homeTeam.getName(), guestTeam.getName(),
                result.getHomeGoals(), result.getGuestGoals(), halfTime.print()));
        for (Goal goal : goals) {
            builder.append(String.format("\n%s. Minute\t%s", goal.getMinute(), goal.getNewResult().print()));
        }
        builder.append("\n");
        return builder.toString();
    }

    public boolean isValid() {
        return guestTeam != null && homeTeam != null;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }

    public Result getHalfTime() {
        return halfTime;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guestTeam == null) ? 0 : guestTeam.hashCode());
        result = prime * result + ((homeTeam == null) ? 0 : homeTeam.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Match other = (Match) obj;

        if (guestTeam == null) {
            if (other.guestTeam != null) {
                return false;
            }
        } else if (!guestTeam.equals(other.guestTeam)) {
            return false;
        }
        if (homeTeam == null) {
            if (other.homeTeam != null) {
                return false;
            }
        } else if (!homeTeam.equals(other.homeTeam)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Match [homeTeam=");
        builder.append(homeTeam);
        builder.append(", guestTeam=");
        builder.append(guestTeam);
        builder.append(", result=");
        builder.append(result);
        builder.append(", goals=");
        builder.append(goals);
        builder.append("]");
        return builder.toString();
    }

    private class PlayerChange {
        private int minute;
        private Player in;
        private Player out;

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public Player getIn() {
            return in;
        }

        public void setIn(Player in) {
            this.in = in;
        }

        public Player getOut() {
            return out;
        }

        public void setOut(Player out) {
            this.out = out;
        }
    }

}
