package de.footballmanager.backend.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.TrialAndErrorTimeTableService;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class TestUtil {


    public static final String TEAM_1 = "team1";
    public static final String TEAM_2 = "team2";
    public static final String TEAM_3 = "team3";
    public static final String TEAM_4 = "team4";

    public static League createLeague() {
        League league = new League();
        List<Team> teams = Lists.newArrayList();
        IntStream.range(1, 10).forEach(i -> teams.add(createTeam("Team" + i, PlayingSystem.SYSTEM_4_4_2)));
        league.setTeams(teams);
        league.setTimeTable(createTimeTable(teams));
        return league;
    }

    public static TimeTable createTimeTable(List<Team> teams) {
        TrialAndErrorTimeTableService timeTableService = new TrialAndErrorTimeTableService();
        return timeTableService.createTimeTable(teams);
    }

    public static Match createRunningMatch() {
        return createMatch(createTeam(TEAM_1, PlayingSystem.SYSTEM_4_4_2), createTeam(TEAM_2, PlayingSystem.SYSTEM_4_4_2), false);
    }

    public static Match createMatch() {
        return createMatch(TEAM_1, TEAM_2, 0, 0);
    }

    public static Match createMatch(String team1, String team2, int homeGoals, int guestGoals) {
        return createMatch(createTeam(team1, PlayingSystem.SYSTEM_4_4_2), createTeam(team2, PlayingSystem.SYSTEM_4_4_2), homeGoals, guestGoals);
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
        match.setPositionPlayerMapHomeTeam(createStartEleven(homeTeam, PlayingSystem.SYSTEM_4_4_2));
        match.setPositionPlayerMapGuestTeam(createStartEleven(guestTeam, PlayingSystem.SYSTEM_4_4_2));
        match.start();
        if (isCreateFinishedMatch) {
            IntStream.range(1, 90).forEach(i -> match.increaseMinute());
        }
        return match;
    }

    public static Map<Position, Player> createStartEleven(Team team, PlayingSystem playingSystem) {
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        setPlayerPositions(team, playingSystem);

        List<Player> players = team.getPlayers();
        Iterator<Player> iterator = players.iterator();
        IntStream.range(0,11).forEach(i -> {
            Player player = iterator.next();
            positionPlayerMap.put(player.getPosition(), player);
        });
        return positionPlayerMap;
    }

    /**
     * Adds position of system to the first 11 players, for the others goaly position
     * @param team
     * @param system
     */
    public static void setPlayerPositions(Team team, PlayingSystem system) {
        List<Player> players = team.getPlayers();
        Iterator<Position> positionIterator = system.getPositions().iterator();
        IntStream.range(0,11).forEach(i -> players.get(i).setPosition(positionIterator.next()));
        if (players.size() > 11) {
            IntStream.range(11, players.size()).forEach(i -> {
                players.get(i).setPosition(Position.GOALY);
            });
        }
    }

    public static Team createTeam(String name, PlayingSystem playingSystem) {
        Team team = new Team(name);
        team.setStrength(88);
        List<Player> players = Lists.newArrayList();
        IntStream.range(0, 22).forEach(i -> players.add(createPlayer("Mr.", String.valueOf(i))));
        team.setPlayers(players);
        team.setName(name);
        createStartEleven(team, playingSystem);
        return team;
    }

    public static Player createPlayer(String firstName, String lastName) {
        return new Player.Builder(firstName, lastName).build();
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
