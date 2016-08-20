package de.footballmanager.backend.util;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.domain.Result;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;

import java.util.List;
import java.util.stream.IntStream;

public class TestUtil {


    public static final String TEAM_1 = "team1";
    public static final String TEAM_2 = "team2";
    public static final String TEAM_3 = "team3";
    public static final String TEAM_4 = "team4";


    public static Match createMatch(String team1, String team2, int homeGoals, int guestGoals) {
        Match match = new Match();
        match.setFinished(true);
        match.setHomeTeam(new Team(team1));
        match.setGuestTeam(new Team(team2));
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

    public static Team createTeam(String name) {
        Team team = new Team(name);
        List<Player> players = Lists.newArrayList();

        IntStream.range(1, 11).forEach(i -> players.add(createPlayer("Mr.", String.valueOf(i))));
        team.setPlayers(players);
        team.setName(name);
        return  team;
    }

    public static Player createPlayer(String firstName, String lastName) {
        return new Player.Builder(firstName, lastName).setPosition(Position.LEFT_MIDFIELDER).build();
    }

    public static Result createResult(int homeGoals, int guestGoals) {
        return new Result(homeGoals, guestGoals);
    }
}
