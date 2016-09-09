package de.footballmanager.backend.util;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.TrialAndErrorTimeTableService;

import java.util.List;
import java.util.stream.IntStream;

public class TestUtil {


    public static final String TEAM_1 = "team1";
    public static final String TEAM_2 = "team2";
    public static final String TEAM_3 = "team3";
    public static final String TEAM_4 = "team4";

    public static League createLeague() {
        League league = new League();
        List<Team> teams = Lists.newArrayList();
        IntStream.range(1, 10).forEach(i -> teams.add(createTeam("Team" + i)));
        league.setTeams(teams);
        league.setTimeTable(createTimeTable(teams));
        return league;
    }

    public static TimeTable createTimeTable(List<Team> teams) {
        TrialAndErrorTimeTableService timeTableService = new TrialAndErrorTimeTableService();
        return timeTableService.createTimeTable(teams);
    }

    public static Match createMatch(String team1, String team2, int homeGoals, int guestGoals) {
        return createMatch(new Team(team1), new Team(team2), homeGoals, guestGoals);
    }

    public static Match createMatch(Team team1, Team team2, int homeGoals, int guestGoals) {
        Match match = createMatch(team1, team2, true);
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

    public static Match createMatch(Team team1, Team team2, boolean isFinished) {
        Match match = new Match();
        match.setFinished(isFinished);
        match.setHomeTeam(team1);
        match.setGuestTeam(team2);
        return match;
    }

    public static Team createTeam(String name) {
        Team team = new Team(name);
        team.setStrength(88);
        List<Player> players = Lists.newArrayList();

        IntStream.range(1, 11).forEach(i -> players.add(createPlayer("Mr.", String.valueOf(i))));
        team.setPlayers(players);
        team.setName(name);
        return team;
    }

    public static Team createTeam(String name, int strength) {
        Team team = createTeam(name);
        team.setStrength(strength);
        return team;
    }

    public static Player createPlayer(String firstName, String lastName) {
        return new Player.Builder(firstName, lastName).setPosition(Position.LEFT_MIDFIELDER).build();
    }

    public static Result createResult(int homeGoals, int guestGoals) {
        return new Result(homeGoals, guestGoals);
    }
}
