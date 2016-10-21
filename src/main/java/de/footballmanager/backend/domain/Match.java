package de.footballmanager.backend.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.enumeration.ResultType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Match {

    private static final int MINUTES_OF_GAME = 90;
    private static final int MINUTES_HALF_TIME = 45;
    private Team homeTeam;
    private Team guestTeam;

    private Map<Position, Player> positionPlayerMapHomeTeam = new MaxSizeHashMap<>(11);
    private Map<Position, Player> positionPlayerMapGuestTeam = new MaxSizeHashMap<>(11);

    private int minute = 1;
    private int additionalTime = 0;
    private List<Player> yellowCards;
    private List<Player> redCards;
    private List<PlayerChange> playerChangesHomeTeam = Lists.newArrayListWithCapacity(3);
    private List<PlayerChange> playerChangesGuestTeam = Lists.newArrayListWithCapacity(3);

    private final List<Goal> goals = Lists.newArrayList();
    private final Result halfTime = new Result(0, 0);
    private Result result = new Result(0, 0);
    private boolean isFinished = false;
    private boolean isStarted = false;

    public Match() {}

    public Match(final Team homeTeam, final Team guestTeam) {
        super();
        this.homeTeam = homeTeam;
        this.guestTeam = guestTeam;
    }

    public boolean isHomeTeam(Team team) {
        return homeTeam.equals(team);
    }

    public boolean isGuestTeam(Team team) {
        return guestTeam.equals(team);
    }

    private void validateIsMatchPrepared() {
        Preconditions.checkNotNull(homeTeam, "home team not set");
        Preconditions.checkNotNull(guestTeam, "guest team not set");

        Preconditions.checkArgument(positionPlayerMapHomeTeam.size() == 11,
                "start eleven of home team not set correctly, size: ", positionPlayerMapHomeTeam.size());
        Preconditions.checkArgument(positionPlayerMapGuestTeam.size() == 11,
                "start eleven of guest team not set correctly, size: ", positionPlayerMapGuestTeam.size());

    }

    public void start() {

        validateIsMatchPrepared();
        isStarted = true;
    }

    public void increaseMinute() {
        validateMatchIsRunning();
        minute++;
        if (minute >= MINUTES_OF_GAME + additionalTime) {
            setFinished(true);
        }
    }

    public void increaseGoalsHomeTeam(final Goal goal) {
        validateMatchIsRunning();
        if (goal.getMinute() <= MINUTES_HALF_TIME) {
            halfTime.increaseHomeGoal();
        }
        result.increaseHomeGoal();
        addGoal(goal);
    }

    private void validateMatchIsRunning() {
        Preconditions.checkArgument(isStarted, "match not started yet");
        Preconditions.checkArgument(!isFinished(), "match already isFinished");
    }

    public void increaseGoalsGuestTeam(final Goal goal) {
        validateMatchIsRunning();
        if (goal.getMinute() <= MINUTES_HALF_TIME) {
            halfTime.increaseGuestGoal();
        }
        result.increaseGuestGoal();
        addGoal(goal);
    }


    private boolean areTeamsSet() {
        return guestTeam != null && homeTeam != null;
    }


    public boolean containsTeam(final Team team) {
        return areTeamsSet() && (homeTeam.equals(team) || guestTeam.equals(team));
    }

    public void changePlayer(Team team, Player in, Player out) {
        Preconditions.checkArgument(this.containsTeam(team), "cannot change player for not contained team: ", team);
        if (isHomeTeam(team)) {
            changePlayerHome(in, out, positionPlayerMapHomeTeam, homeTeam, playerChangesHomeTeam);
        } else if (isGuestTeam(team)) {
            changePlayerHome(in, out, positionPlayerMapGuestTeam, guestTeam, playerChangesGuestTeam);
        }
    }

    private void changePlayerHome(Player in, Player out, Map<Position, Player> positionPlayerMap, Team team, List<PlayerChange> playerChanges) {
        Preconditions.checkArgument(positionPlayerMap.values().contains(out), String.format("coming out player {%s} not member of current players", out));
        Preconditions.checkArgument(team.getPlayers().contains(in), String.format("coming in player {%s} not member of team", in));
        Preconditions.checkArgument(!positionPlayerMap.values().contains(in), String.format("coming in player {%s} already playing", in));
        Preconditions.checkState(playerChanges.size() < 3, "max number of player changes already reached");
        positionPlayerMap.entrySet().forEach(positionToPlayer -> {
            Position position = positionToPlayer.getKey();
            if (position.equals(out.getPosition())) {
                System.out.println("changed " + position);
                positionPlayerMap.put(position, in);
                playerChanges.add(new PlayerChange(minute, in, out));
            }
        });
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

    public Map<Position, Player> getPositionPlayerMapHomeTeam() {
        return positionPlayerMapHomeTeam;
    }

    public void setPositionPlayerMapHomeTeam(Map<Position, Player> positionPlayerMapHomeTeam) {
        Preconditions.checkState(!isStarted, "startEleven cannot be set if match already started");
        Preconditions.checkState(!isFinished, "startEleven cannot be set if match already finished");
        List<Player> playersNotPartOfTeam = positionPlayerMapHomeTeam.values().stream()
                .filter(player -> !homeTeam.getPlayers().contains(player))
                .collect(Collectors.toList());
        if (!playersNotPartOfTeam.isEmpty()) {
            System.out.println(playersNotPartOfTeam);
        }
        Preconditions.checkArgument(playersNotPartOfTeam.isEmpty(), "players must be part of the team");
        Preconditions.checkArgument(Sets.newHashSet(positionPlayerMapHomeTeam.values()).size() == 11, "players must be different");

        this.positionPlayerMapHomeTeam = positionPlayerMapHomeTeam;

    }

    public Map<Position, Player> getPositionPlayerMapGuestTeam() {
        return ImmutableMap.copyOf(positionPlayerMapGuestTeam);
    }

    public void setPositionPlayerMapGuestTeam(Map<Position, Player> positionPlayerMapGuestTeam) {
        this.positionPlayerMapGuestTeam = positionPlayerMapGuestTeam;
    }

    //    public void getAdditionalTime() throws NotSupportedException {
//        throw new NotSupportedException();
//    }

    private void setFinished(final boolean finished) {
        Preconditions.checkState(isStarted, "match cannot be finished if it has not been started");
        Preconditions.checkArgument(minute >= MINUTES_OF_GAME + additionalTime, String.format("do not finish match before 90 minutes passed: {%s}", minute));
        this.isFinished = finished;
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


    public int getMinute() {
        return minute;
    }

    public void addGoal(final Goal goal) {
        goals.add(goal);
    }

    public List<Goal> getGoals() {
        return goals;
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
        return isFinished;
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

    class PlayerChange {
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
