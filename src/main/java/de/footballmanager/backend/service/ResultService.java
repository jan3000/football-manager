package de.footballmanager.backend.service;

import java.util.List;
import java.util.Random;

import de.footballmanager.backend.domain.Goal;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.Result;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

    private static final double PROBABILITY_OF_GOAL_PER_MINUTE = 0.97;
    private static final int HOME_ADVANTAGE = 15;
    private static final int WIN_PROBABILITY_BALANCE_CONSTANT = 30;

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
            Goal goal = new Goal(minute, match.getHomeTeam(), null, null, new Result(match.getGoalsHomeTeam() + 1,
                    match.getGoalsGuestTeam()));
            match.increaseGoalsHomeTeam(goal);
        }

        if (isGoalInThisMinute(match.getGuestTeam().getStrength(), false)) {
            Goal goal = new Goal(minute, match.getGuestTeam(), null, null, new Result(match.getGoalsHomeTeam(),
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
        // System.out.println("   PROB: " + factor);
        if (factor > 50) {
            return Math.random() > (PROBABILITY_OF_GOAL_PER_MINUTE);
        } else {
            return false;
        }
    }
}
