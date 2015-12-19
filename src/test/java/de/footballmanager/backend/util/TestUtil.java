package de.footballmanager.backend.util;

import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.Result;
import de.footballmanager.backend.domain.Team;

public class TestUtil {


    public static final String TEAM_1 = "team1";
    public static final String TEAM_2 = "team2";
    public static final String TEAM_3 = "team3";
    public static final String TEAM_4 = "team4";


    public static Match buildMatch(String team1, String team2, int homeGoals, int guestGoals) {
        Match match = new Match();
        match.setFinished(true);
        match.setHomeTeam(new Team(team1));
        match.setGuestTeam(new Team(team2));
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

}
