package de.footballmanager.backend.engine;

import java.util.Random;

import de.footballmanager.backend.domain.Goal;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.Result;

public class ResultEngine {

    private static final double PROBABILITY_OF_GOAL_PER_MINUTE = 0.97;
    private static final int HOME_ADVANTAGE = 15;
    private static final int WIN_PROBABILITY_BALANCE_CONSTANT = 30;

    public static void calculateResult(final Match match) {
        int additionalTime = new Random().nextInt(5);
        for (int minute = 1; minute <= 90 + additionalTime; minute++) {

            if (isGoalInThisMinute(match.getHomeTeam().getStrength(), true)) {
                Goal goal = new Goal(minute, match.getHomeTeam(), null, null, new Result(match.getGoalsHomeTeam() + 1,
                        match.getGoalsGuestTeam()));
                match.increaseGoalsHomeTeam(goal);
            }

            if (isGoalInThisMinute(match.getGuestTeam().getStrength(), false)) {
                Goal goal = new Goal(minute, match.getGuestTeam(), null, null, new Result(match.getGoalsHomeTeam(),
                        match.getGoalsGuestTeam() + 1));
                match.increaseGoalsGuestTeam(goal);
            }
        }
        match.setHasEnded(true);
    }

    private static boolean isGoalInThisMinute(final int strength, final boolean isHomeTeam) {
        int probabilityToWin = WIN_PROBABILITY_BALANCE_CONSTANT + strength;
        if (isHomeTeam) {
            probabilityToWin += HOME_ADVANTAGE;
        }
        double factor = Math.random() * probabilityToWin;
        // System.out.println("   PROB: " + factor);
        if (factor > 50) {
            return Math.random() > (PROBABILITY_OF_GOAL_PER_MINUTE);
        } else {
            return false;
        }
    }
}
