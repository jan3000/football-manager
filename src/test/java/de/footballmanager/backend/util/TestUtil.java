package de.footballmanager.backend.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.TeamManagerService;
import de.footballmanager.backend.service.TrialAndErrorTimeTableService;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.footballmanager.backend.enumeration.Position.*;

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

    public static Match createRunningMatch() {
        return createMatch(createTeam(TEAM_1), createTeam(TEAM_2), false);
    }

    public static Match createMatch() {
        return createMatch(TEAM_1, TEAM_2, 0, 0);
    }

    public static Match createMatch(String team1, String team2, int homeGoals, int guestGoals) {
        return createMatch(createTeam(team1), createTeam(team2), homeGoals, guestGoals);
    }

    public static Match createMatch(Team team1, Team team2, int homeGoals, int guestGoals) {
        Match match = createMatch(team1, team2, true);
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

    public static Match createMatch(Team homeTeam, Team guestTeam, boolean isCreateFinishedMatch) {
        Match match = new Match();
        match.setHomeTeam(homeTeam);
        match.setGuestTeam(guestTeam);
        match.setPositionPlayerMapHomeTeam(createStartEleven(homeTeam));
        match.setPositionPlayerMapGuestTeam(createStartEleven(guestTeam));
        match.start();
        if (isCreateFinishedMatch) {
            IntStream.range(1, 90).forEach(i -> match.increaseMinute());
        }
        return match;
    }

    public static Map<Position, Player> createStartEleven(Team team) {
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        List<Player> players = team.getPlayers();
        positionPlayerMap.put(GOALY, players.get(0));
        positionPlayerMap.put(LEFT_DEFENDER, players.get(1));
        positionPlayerMap.put(LEFT_STOPPER, players.get(2));
        positionPlayerMap.put(RIGHT_STOPPER, players.get(3));
        positionPlayerMap.put(RIGHT_DEFENDER, players.get(4));
        positionPlayerMap.put(CENTRAL_DEFENSIVE_MIDFIELDER, players.get(5));
        positionPlayerMap.put(LEFT_MIDFIELDER, players.get(6));
        positionPlayerMap.put(RIGHT_MIDFIELDER, players.get(7));
        positionPlayerMap.put(CENTRAL_OFFENSIVE_MIDFIELDER, players.get(8));
        positionPlayerMap.put(LEFT_STRIKER, players.get(9));
        positionPlayerMap.put(RIGHT_STRIKER, players.get(10));
        return positionPlayerMap;
    }

    public static Map<Position, Player> createStartEleven(Team team, TeamManagerService.System system) {
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        List<Player> players = team.getPlayers();
        Iterator<Position> positionIterator = system.getPositions().iterator();
        positionPlayerMap.put(positionIterator.next(), players.get(0));
        positionPlayerMap.put(positionIterator.next(), players.get(1));
        positionPlayerMap.put(positionIterator.next(), players.get(2));
        positionPlayerMap.put(positionIterator.next(), players.get(3));
        positionPlayerMap.put(positionIterator.next(), players.get(4));
        positionPlayerMap.put(positionIterator.next(), players.get(5));
        positionPlayerMap.put(positionIterator.next(), players.get(6));
        positionPlayerMap.put(positionIterator.next(), players.get(7));
        positionPlayerMap.put(positionIterator.next(), players.get(8));
        positionPlayerMap.put(positionIterator.next(), players.get(9));
        positionPlayerMap.put(positionIterator.next(), players.get(10));
        return positionPlayerMap;
    }

    private static List<Position> positions = Lists.newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER);

    public static Team createTeam(String name) {
        Team team = new Team(name);
        team.setStrength(88);
        List<Player> players = Lists.newArrayList();

        IntStream.range(0, 11).forEach(i -> players.add(createPlayer("Mr.", String.valueOf(i), positions.get(i))));
        team.setPlayers(players);
        team.setName(name);
        return team;
    }

    public static Team createTeam(String name, int strength) {
        Team team = createTeam(name);
        createStartEleven(team);
        team.setStrength(strength);
        return team;
    }

    public static Player createPlayer(String firstName, String lastName, Position position) {
        return new Player.Builder(firstName, lastName).setPosition(position).build();
    }

    public static Player createPlayer(Position position, int strength) {
        return new Player.Builder("name", "name").setPosition(position).setStrength(strength).build();
    }

    public static Result createResult(int homeGoals, int guestGoals) {
        return new Result(homeGoals, guestGoals);
    }
}
