package de.footballmanager.backend.domain.league;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.util.MaxSizeHashMap;
import de.footballmanager.backend.enumeration.Position;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Match {


    private String homeTeam;
    private String guestTeam;

    private Map<Position, Player> positionPlayerMapHomeTeam = new MaxSizeHashMap<>(11);
    private Map<Position, Player> positionPlayerMapGuestTeam = new MaxSizeHashMap<>(11);

    private int additionalTime = 0;
    private List<Player> yellowCards;
    private List<Player> redCards;
    private List<PlayerChange> playerChangesHomeTeam = Lists.newArrayListWithCapacity(3);
    private List<PlayerChange> playerChangesGuestTeam = Lists.newArrayListWithCapacity(3);

    private final List<Goal> goals = Lists.newArrayList();
    private final Result halfTimeResult = new Result(0, 0);
    private Result result = new Result(0, 0);
    private boolean isFinished = false;
    private boolean isStarted = false;

    private int minute = 1;

    public void setPositionPlayerMapHomeTeam(Map<Position, Player> positionPlayerMapHomeTeam) {
        this.positionPlayerMapHomeTeam = positionPlayerMapHomeTeam;
    }

    public List<Player> getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(List<Player> yellowCards) {
        this.yellowCards = yellowCards;
    }

    public List<Player> getRedCards() {
        return redCards;
    }

    public void setRedCards(List<Player> redCards) {
        this.redCards = redCards;
    }

    public void setPlayerChangesHomeTeam(List<PlayerChange> playerChangesHomeTeam) {
        this.playerChangesHomeTeam = playerChangesHomeTeam;
    }

    public void setPlayerChangesGuestTeam(List<PlayerChange> playerChangesGuestTeam) {
        this.playerChangesGuestTeam = playerChangesGuestTeam;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getAdditionalTime() {

        return additionalTime;
    }


    public Match() {
    }

    public Match(final String homeTeam, final String guestTeam) {
        super();
        this.homeTeam = homeTeam;
        this.guestTeam = guestTeam;
    }


    public List<PlayerChange> getPlayerChangesHomeTeam() {
        return playerChangesHomeTeam;
    }

    public List<PlayerChange> getPlayerChangesGuestTeam() {
        return playerChangesGuestTeam;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public Map<Position, Player> getPositionPlayerMapHomeTeam() {
        return positionPlayerMapHomeTeam;
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

    public int getGoalsHomeTeam() {
        return result.getHomeGoals();
    }

    public int getGoalsGuestTeam() {
        return result.getGuestGoals();
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(final String homeTeam) {
        Preconditions.checkArgument(positionPlayerMapHomeTeam.size() == 0,
                "home team cannot be changed if start eleven is set already");
        this.homeTeam = homeTeam;
    }

    public String getGuestTeam() {
        return guestTeam;
    }

    public void setGuestTeam(final String guestTeam) {
        Preconditions.checkArgument(positionPlayerMapGuestTeam.size() == 0,
                "guest team cannot be changed if start eleven is set already");
        this.guestTeam = guestTeam;
    }

    public void setAdditionalTime(int additionalTime) {
        this.additionalTime = additionalTime;
    }


    public int getMinute() {
        return minute;
    }


    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void addGoal(final Goal goal) {
        goals.add(goal);
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }

    public Result getHalfTimeResult() {
        return halfTimeResult;
    }

    public boolean isFinished() {
        return isFinished;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return minute == match.minute &&
                additionalTime == match.additionalTime &&
                isFinished == match.isFinished &&
                isStarted == match.isStarted &&
                Objects.equals(homeTeam, match.homeTeam) &&
                Objects.equals(guestTeam, match.guestTeam) &&
                Objects.equals(positionPlayerMapHomeTeam, match.positionPlayerMapHomeTeam) &&
                Objects.equals(positionPlayerMapGuestTeam, match.positionPlayerMapGuestTeam) &&
                Objects.equals(yellowCards, match.yellowCards) &&
                Objects.equals(redCards, match.redCards) &&
                Objects.equals(playerChangesHomeTeam, match.playerChangesHomeTeam) &&
                Objects.equals(playerChangesGuestTeam, match.playerChangesGuestTeam) &&
                Objects.equals(goals, match.goals) &&
                Objects.equals(halfTimeResult, match.halfTimeResult) &&
                Objects.equals(result, match.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, guestTeam, positionPlayerMapHomeTeam, positionPlayerMapGuestTeam, minute, additionalTime, yellowCards, redCards, playerChangesHomeTeam, playerChangesGuestTeam, goals, halfTimeResult, result, isFinished, isStarted);
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

    public static class PlayerChange {
        private int minute;
        private Player in;
        private Player out;

        public PlayerChange(int minute, Player in, Player out) {
            this.minute = minute;
            this.in = in;
            this.out = out;
        }

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
