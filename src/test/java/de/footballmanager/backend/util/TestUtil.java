package de.footballmanager.backend.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Club;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.League;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.Result;
import de.footballmanager.backend.domain.league.TimeTable;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.DateService;
import de.footballmanager.backend.service.TrialAndErrorTimeTableService;
import org.joda.time.DateTime;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_4_4_2;
import static org.easymock.EasyMock.*;

public class TestUtil {


    public static final String TEAM_NAME_1 = "team1";
    public static final String TEAM_NAME_2 = "team2";
    public static final String TEAM_NAME_3 = "team3";
    public static final String TEAM_NAME_4 = "team4";
    public static final int DEFAULT_STRENGTH = 88;

    public static League createLeague(String leagueName, int numberOfTeams) {
        return createLeague(leagueName, numberOfTeams, 0);
    }

    public static League createLeague(String leagueName, int numberOfTeams, int numberOfPromotions) {
        List<Team> teams = Lists.newArrayList();
        IntStream.range(1, numberOfTeams).forEach(i -> teams.add(createTeam("Team" + i, SYSTEM_4_4_2)));
        return new League(leagueName, teams, numberOfPromotions);
    }

    public static League createLeague(String leagueName, List<String> teamNames, int numberOfPromotions) {
        List<Team> teams = Lists.newArrayList();
        teamNames.forEach(teamName -> teams.add(createTeam(teamName, SYSTEM_4_4_2)));
        return new League(leagueName, teams, numberOfPromotions);
    }

    public static TimeTable createTimeTable(List<Team> teams) {
        DateTime now = DateTime.now();
        TrialAndErrorTimeTableService timeTableService = new TrialAndErrorTimeTableService();
        DateService dateService = createMock(DateService.class);
        expect(dateService.setDayTime(now, 15, 30)).andReturn(now);
        replay(dateService);
        ReflectionTestUtils.setField(timeTableService, "dateService", dateService);
        return timeTableService.createTimeTable(teams, now);
    }


    public static Club createClub(String name, boolean computerManaged) {
        Club club = new Club("name");
        Manager manager = createManager(computerManaged);
        club.setManager(manager);
        return club;
    }

    public static Manager createManager(boolean computerManaged) {
        Manager manager = new Manager("Joe", "McGeiz");
        manager.setComputerManaged(computerManaged);
        return manager;
    }

    public static Match createRunningMatch(Team homeTeam, Team guestTeam) {
        return createMatch(homeTeam, guestTeam);
    }

    public static Match createMatch(Team homeTeam, Team guestTeam) {
        return createMatch(0, 0, SYSTEM_4_4_2, SYSTEM_4_4_2, homeTeam, guestTeam);
    }

    // TODO check if system set in team instance is enough and that homeSystem and guestSystem can be removed maybe!
    public static Match createMatch(PlayingSystem homeSystem, PlayingSystem guestSystem, Team homeTeam, Team guestTeam) {
        return createMatch(0, 0, homeSystem, guestSystem, homeTeam, guestTeam);
    }

    public static Match createMatch(int homeGoals, int guestGoals,
                                    PlayingSystem homeSystem, PlayingSystem guestSystem, Team homeTeam, Team guestTeam) {
        return createMatch(homeTeam, guestTeam, homeGoals, guestGoals, homeSystem, guestSystem);
    }

    public static Match createFinishedMatch(String teamNameHome, String teamNameGuest, int homeGoals, int guestGoals,
                                            PlayingSystem homeSystem, PlayingSystem guestSystem) {
        Team homeTeam = createTeam(teamNameHome, homeSystem);
        Team guestTeam = createTeam(teamNameGuest, guestSystem);
        return createFinishedMatch(homeTeam, guestTeam, homeGoals, guestGoals, homeSystem, guestSystem);
    }

    public static Match createMatch(Team team1, Team team2, int homeGoals, int guestGoals,
                                    PlayingSystem homeSystem, PlayingSystem guestSystem) {
        Match match = createMatch(team1, team2, homeSystem, guestSystem);
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

    public static Match createFinishedMatch(Team team1, Team team2, int homeGoals, int guestGoals,
                                            PlayingSystem homeSystem, PlayingSystem guestSystem) {
        Match match = createMatch(team1, team2, homeSystem, guestSystem);
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

    public static Match createMatch(Team team1, Team team2, int homeGoals, int guestGoals) {
        Match match = createMatch(team1, team2);
        match.setResult(new Result(homeGoals, guestGoals));
        return match;
    }

    public static Match createMatch(Team homeTeam, Team guestTeam, PlayingSystem systemHome, PlayingSystem systemGuest) {
        Match match = new Match();
        match.setHomeTeam(homeTeam.getName());
        match.setGuestTeam(guestTeam.getName());
        match.setPositionPlayerMapHomeTeam(createStartElevenMatchingGivenSystem(homeTeam, systemHome));
        match.setPositionPlayerMapGuestTeam(createStartElevenMatchingGivenSystem(guestTeam, systemGuest));
        return match;
    }

    public static Map<Position, Player> createStartElevenMatchingGivenSystem(Team team, PlayingSystem playingSystem) {
        Preconditions.checkArgument(team.getPlayers().size() > 10, "at least 11 players needed in team");
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        setPlayerPositions(team, playingSystem);

        List<Player> players = team.getPlayers();
        Iterator<Player> iterator = players.iterator();
        IntStream.range(0, 11).forEach(i -> {
            Player player = iterator.next();
            positionPlayerMap.put(player.getPosition(), player);
        });
        return positionPlayerMap;
    }

    /**
     * Adds position of system to the first 11 players, for the others goaly position
     *
     * @param team
     * @param system
     */
    public static void setPlayerPositions(Team team, PlayingSystem system) {
        List<Player> players = team.getPlayers();
        Iterator<Position> positionIterator = system.getPositions().iterator();
        IntStream.range(0, 11).forEach(i -> players.get(i).setPosition(positionIterator.next()));
        if (players.size() > 11) {
            IntStream.range(11, players.size()).forEach(i -> {
                players.get(i).setPosition(Position.GOALY);
            });
        }
    }

    public static Team createTeam(String name, PlayingSystem playingSystem, int numberOfPlayers) {
        return createTeam(name, playingSystem, numberOfPlayers, true);
    }


    public static Team createTeam(String name, PlayingSystem playingSystem, int numberOfPlayers, boolean setStartEleven) {
        Team team = new Team(name);
        team.setStrength(DEFAULT_STRENGTH);
        List<Player> players = Lists.newArrayList();
        IntStream.range(0, numberOfPlayers).forEach(i -> {
            Player player = createPlayer("Mr.", String.valueOf(i));
            player.setStrength(DEFAULT_STRENGTH);
            players.add(player);
        });
        team.setPlayers(players);
        team.setName(name);
        if (setStartEleven) {
            createStartElevenMatchingGivenSystem(team, playingSystem);
        }
        return team;
    }

    public static Team createTeam(String name) {
        return createTeam(name, PlayingSystem.SYSTEM_4_4_2, 22, true);
    }

    public static Team createTeam(String name, PlayingSystem playingSystem) {
        return createTeam(name, playingSystem, 22, true);
    }

    public static Team createTeam(String name, PlayingSystem playingSystem, boolean setStartEleven) {
        return createTeam(name, playingSystem, 22, setStartEleven);
    }

    public static Player createPlayer(String firstName, String lastName) {
        return new Player.Builder(firstName, lastName).build();
    }

    public static Player createPlayer(String firstName, String lastName, Position position) {
        return new Player.Builder(firstName, lastName).setPosition(position).build();
    }

    public static Player createPlayer(String firstName, String lastName, Position position, int strength) {
        return new Player.Builder(firstName, lastName).setPosition(position).setStrength(strength).build();
    }

    public static Player createPlayer(Position position, int strength) {
        return new Player.Builder("name", "name").setPosition(position).setStrength(strength).build();
    }

    public static Result createResult(int homeGoals, int guestGoals) {
        return new Result(homeGoals, guestGoals);
    }
}
