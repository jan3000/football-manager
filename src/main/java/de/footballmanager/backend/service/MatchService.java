package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.*;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.enumeration.ResultType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    private ClubService clubService;

    private static final int MINUTES_OF_GAME = 90;
    private static final int MINUTES_HALF_TIME = 45;


    private void validateIsMatchPrepared(Match match) {
        Preconditions.checkNotNull(match.getHomeTeam(), "home team not set");
        Preconditions.checkNotNull(match.getGuestTeam(), "guest team not set");

        Preconditions.checkArgument(match.getPositionPlayerMapHomeTeam().size() == 11,
                "start eleven of home team not set correctly, size: ", match.getPositionPlayerMapHomeTeam().size());
        Preconditions.checkArgument(match.getPositionPlayerMapGuestTeam().size() == 11,
                "start eleven of guest team not set correctly, size: ", match.getPositionPlayerMapGuestTeam().size());

    }

    public void start(Match match) {
        validateIsMatchPrepared(match);
        match.setStarted(true);
    }

    public void increaseMinute(Match match) {
        validateMatchIsRunning(match);
        int minute = match.getMinute();
        match.setMinute(minute + 1);
        if (minute >= MINUTES_OF_GAME + match.getAdditionalTime()) {
            setFinished(match, true);
        }
    }

    public void increaseGoalsHomeTeam(Match match, final Goal goal) {
        validateMatchIsRunning(match);
        if (isFirstHalf(goal)) {
            match.getHalfTimeResult().increaseHomeGoal();
        }
        match.getResult().increaseHomeGoal();
        match.addGoal(goal);
    }

    private boolean isFirstHalf(Goal goal) {
        return goal.getMinute() <= MINUTES_HALF_TIME;
    }

    private void validateMatchIsRunning(Match match) {
        Preconditions.checkArgument(match.isStarted(), "match not started yet");
        Preconditions.checkArgument(!match.isFinished(), "match is already finished");
    }

    public void increaseGoalsGuestTeam(Match match, final Goal goal) {
        validateMatchIsRunning(match);
        if (isFirstHalf(goal)) {
            match.getHalfTimeResult().increaseGuestGoal();
        }
        match.getResult().increaseGuestGoal();
        match.addGoal(goal);
    }


    private boolean areTeamsSet(Match match) {
        return StringUtils.isNotBlank(match.getGuestTeam()) && StringUtils.isNotBlank(match.getHomeTeam());
    }


    public boolean containsTeam(Match  match, final String team) {
        return areTeamsSet(match) && (match.getHomeTeam().equals(team) || match.getGuestTeam().equals(team));
    }

    public boolean isHomeTeam(Match match, String team) {
        return match.getHomeTeam().equals(team);
    }

    public boolean isGuestTeam(Match match, String team) {
        return match.getGuestTeam().equals(team);
    }


    /**
     * Position is kept
     */
    public void changePlayer(Match match, String team, Player in, Player out) {
        Preconditions.checkArgument(containsTeam(match, team), "cannot change player for not contained team: ", team);
        if (isHomeTeam(match , team)) {
            changePlayer(in, out, match.getPositionPlayerMapHomeTeam(), match.getHomeTeam(), match.getPlayerChangesHomeTeam(), match.getMinute());
        } else if (isGuestTeam(match, team)) {
            changePlayer(in, out, match.getPositionPlayerMapGuestTeam(), match.getGuestTeam(), match.getPlayerChangesGuestTeam(), match.getMinute());
        }
    }


    private void changePlayer(Player in, Player out, Map<Position, Player> positionPlayerMap, String teamName, List<Match.PlayerChange> playerChanges, int minute) {
        Preconditions.checkArgument(positionPlayerMap.values().contains(out), String.format("coming out player {%s} not member of current players", out));

        Team team = clubService.getTeam(teamName);
        Preconditions.checkArgument(team.getPlayers().contains(in), String.format("coming in player {%s} not member of team", in));


        Preconditions.checkArgument(!positionPlayerMap.values().contains(in), String.format("coming in player {%s} already playing", in));
        Preconditions.checkState(playerChanges.size() < 3, "max number of player changes already reached");
        positionPlayerMap.entrySet().forEach(positionToPlayer -> {
            Position position = positionToPlayer.getKey();
            if (position.equals(out.getPosition())) {
                System.out.println("changed " + position);
                positionPlayerMap.put(position, in);
                playerChanges.add(new Match.PlayerChange(minute, in, out));
            }
        });
    }


    public void setPositionPlayerMapHomeTeam(Match match, Map<Position, Player> positionPlayerMapHomeTeam) {
        Preconditions.checkState(!match.isFinished(), "startEleven cannot be set if match already finished");
        Preconditions.checkArgument(positionPlayerMapHomeTeam.size() == 11, "11 players must be set");
        List<Player> playersNotPartOfTeam = positionPlayerMapHomeTeam.values().stream()
                .filter(player -> !clubService.getTeam(match.getHomeTeam()).getPlayers().contains(player))
                .collect(Collectors.toList());
        Preconditions.checkArgument(playersNotPartOfTeam.isEmpty(), "players must be part of the team");
        Preconditions.checkArgument(Sets.newHashSet(positionPlayerMapHomeTeam.values()).size() == 11, "players must be different");

        match.setPositionPlayerMapHomeTeam(positionPlayerMapHomeTeam);

    }


    private void setFinished(Match match, final boolean finished) {
        Preconditions.checkState(match.isStarted(), "match cannot be finished if it has not been started");
        Preconditions.checkArgument(match.getMinute()>= MINUTES_OF_GAME + match.getAdditionalTime(), String.format("do not finish match before 90 minutes passed: {%s}", match.getMinute()));
        match.setFinished(finished);
    }
    public ResultType getResultType(Match match) {
        Result result = match.getResult();
        if (result.getHomeGoals() > result.getGuestGoals()) {
            return ResultType.HOME_WON;
        } else if (result.getHomeGoals() < result.getGuestGoals()) {
            return ResultType.GUEST_WON;
        } else {
            return ResultType.DRAW;
        }
    }

    public String getHomeTeam(Match match) {
        return match.getHomeTeam();
    }

    public String getGuestTeam(Match match) {
        return match.getGuestTeam();
    }

    public boolean containsTeam(MatchDay matchDay, final Team team) {
        for (Match match : matchDay.getMatches()) {
            if (containsTeam(match, team.getName())) {
                return true;
            }
        }
        return false;
    }

    public Match getMatchOfTeam(MatchDay matchDay, String teamName) {
        return matchDay.getMatches().stream()
                .filter(m -> m.getHomeTeam().equals(teamName) || m.getGuestTeam().equals(teamName))
                .collect(Collectors.toList()).get(0);
    }


    public Match getMatch(TimeTable timeTable, int matchDayNumber, String teamName) {
        MatchDay matchDay = timeTable.getMatchDay(matchDayNumber);
        return getMatchOfTeam(matchDay, teamName);
    }
}
