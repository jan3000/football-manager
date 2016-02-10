package de.footballmanager.backend.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

    private static final double PROBABILITY_OF_GOAL_PER_MINUTE = 0.97;
    private static final int HOME_ADVANTAGE = 15;
    private static final int WIN_PROBABILITY_BALANCE_CONSTANT = 30;
    public static final Random RANDOM = new Random();

    public void calculateResult(final Match match) {
        int additionalTime = new Random().nextInt(5);
        match.setAdditionalTime(additionalTime);
        for (int minute = 1; minute <= 90 + additionalTime; minute++) {

            simulateMatchMinute(match, minute);
        }
        match.setFinished(true);
    }

    private void simulateMatchMinute(Match match, int minute) {
        if (isGoalInThisMinute(match.getHomeTeam().getStrength(), true)) {
            Player goalMaker = getGoalMaker(match.getHomeTeam());
            System.out.println("GOAL: " + goalMaker.print());
            Goal goal = new Goal(minute, match.getHomeTeam(), goalMaker, null, new Result(match.getGoalsHomeTeam() + 1,
                    match.getGoalsGuestTeam()));
            match.increaseGoalsHomeTeam(goal);
        }

        if (isGoalInThisMinute(match.getGuestTeam().getStrength(), false)) {
            Player goalMaker = getGoalMaker(match.getGuestTeam());
            Goal goal = new Goal(minute, match.getGuestTeam(), goalMaker, null, new Result(match.getGoalsHomeTeam(),
                    match.getGoalsGuestTeam() + 1));
            match.increaseGoalsGuestTeam(goal);
        }
        match.increaseMinute();

        if(match.getMinute() == 90) {
            System.out.println("FINISHED");
            match.setFinished(true);
        }
        // TODO calculate additionalTime
        // TODO cards, injuries, changes
    }

    private Player getGoalMaker(Team team) {
        Map<Integer, Player> scoreToPlayer = Maps.newHashMap();
        List<Player> players = team.getPlayers();
        for (Player player : players) {
            Position position = player.getPosition();
            int score = RANDOM.nextInt(position.getProbabilityOfShootingGoal()) * player.getStrength();
            scoreToPlayer.put(score, player);
        }
        return scoreToPlayer.get(getMaxKey(scoreToPlayer));
    }

    private Integer getMaxKey(Map<Integer, Player> scoreToPlayer) {
        return scoreToPlayer.keySet().stream().max(Comparator.naturalOrder()).get();
    }

    public void calculateNextMinute(List<Match> matches) {
        for (Match match : matches) {
            if (!match.isFinished()) {
                simulateMatchMinute(match, match.getMinute());
            }
        }
    }

    private boolean isGoalInThisMinute(final int strength, final boolean isHomeTeam) {
        int probabilityToWin = WIN_PROBABILITY_BALANCE_CONSTANT + strength;
        if (isHomeTeam) {
            probabilityToWin += HOME_ADVANTAGE;
        }
        double factor = Math.random() * probabilityToWin;
        return factor > 50 && Math.random() > (PROBABILITY_OF_GOAL_PER_MINUTE);
    }
}
