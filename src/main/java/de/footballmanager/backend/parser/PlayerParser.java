package de.footballmanager.backend.parser;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PlayerParser {

    private static final Position[] ALL_POSITIONS = new Position[]{Position.DEFENSIVE_MIDFIELDER, Position.GOALY,
            Position.LEFT_DEFENDER, Position.LEFT_MIDFIELDER, Position.LEFT_WINGER, Position.OFFENSIVE_MIDFIELDER,
            Position.RIGHT_DEFENDER, Position.RIGHT_MIDFIELDER, Position.RIGHT_WINGER, Position.STOPPER,
            Position.STRIKER};
    private static final int MINIMAL_NUMBER_OF_PLAYERS = 20;
    private static final Path RESOURCE_DIRECTORY = Paths.get("src/main/resources");
    private static final Random RANDOM = new Random();

    private List<Position> positions;
    private List<Integer> strengths;
    private League league;

    public void parsePlayerForLeague(League league) {
        Preconditions.checkArgument(league != null);
        Preconditions.checkArgument(!league.getTeams().isEmpty());
        List<String> names = normalizeNames(readInNames("names.txt"));
        List<String> surnames = normalizeNames(readInNames("surnames.txt"));

        for (Team team : league.getTeams()) {
            int numberOfPlayers = getNumberOfPlayers();
            setPositionsOfPlayers(numberOfPlayers);

            for (int i = 0; i < numberOfPlayers; i++) {
                Player player = new Player();
                String firstName = names.get(RANDOM.nextInt(names.size()));
                String surName = surnames.get(RANDOM.nextInt(surnames.size()));

                player.setFirstname(firstName);
                player.setLastname(surName);
                player.setPosition(positions.get(i));
                player.setHomeCountry(CountryCode.DE);
                player.setStrength(getStrength(team));
                player.setDateOfBirth(getDate());

                System.out.println(player.print());
                team.getPlayers().add(player);
            }
        }
    }

    private DateTime getDate() {
        return new DateTime().minusYears(RANDOM.nextInt(18) + 18).minusMonths(RANDOM.nextInt(12)).minusDays(30);
    }

    private int getStrength(Team team) {
        int teamStrength = team.getStrength();
        int variablePart;
        if (teamStrength > 50) {
            variablePart = 100 - teamStrength;
        } else {
            variablePart = teamStrength;
        }
        return RANDOM.nextInt(variablePart) + teamStrength;
    }

    private int getNumberOfPlayers() {
        return RANDOM.nextInt(5) + MINIMAL_NUMBER_OF_PLAYERS;
    }

    private void setPositionsOfPlayers(int numberOfPlayers) {
        positions = Lists.newArrayListWithCapacity(numberOfPlayers);
        positions.add(Position.GOALY);
        positions.add(Position.GOALY);
        positions.add(Position.STOPPER);
        positions.add(Position.STOPPER);
        positions.add(Position.LEFT_DEFENDER);
        positions.add(Position.LEFT_DEFENDER);
        positions.add(Position.RIGHT_DEFENDER);
        positions.add(Position.RIGHT_DEFENDER);
        positions.add(Position.DEFENSIVE_MIDFIELDER);
        positions.add(Position.DEFENSIVE_MIDFIELDER);
        positions.add(Position.LEFT_MIDFIELDER);
        positions.add(Position.LEFT_MIDFIELDER);
        positions.add(Position.RIGHT_MIDFIELDER);
        positions.add(Position.OFFENSIVE_MIDFIELDER);
        positions.add(Position.OFFENSIVE_MIDFIELDER);
        positions.add(Position.LEFT_WINGER);
        positions.add(Position.RIGHT_WINGER);
        positions.add(Position.RIGHT_WINGER);
        positions.add(Position.STRIKER);
        positions.add(Position.STRIKER);

        for (int i = MINIMAL_NUMBER_OF_PLAYERS; i < numberOfPlayers; i++) {
            positions.add(ALL_POSITIONS[RANDOM.nextInt(ALL_POSITIONS.length)]);
        }
    }

    private List<String> normalizeNames(List<String> names) {
        System.out.println("normalizeNames: " + names.size());
        return names.stream()
                .filter(s -> s.length() > 1)
                .map(s -> s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.toList());
    }

    private List<String> readInNames(String fileWithNames) {
        List<String> names = Lists.newArrayList();
        Path path = RESOURCE_DIRECTORY.resolve(fileWithNames);
        try (Stream<String> namesStream = Files.lines(path)) {
            names = namesStream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    public static void main(String[] args) {
        PlayerParser playerParser = new PlayerParser();
        League league = new League();
        List<Team> teams = Lists.newArrayList();
        Team team = new Team("Hamburg");
        team.setStrength(80);
        teams.add(team);

        league.setTeams(teams);
        playerParser.parsePlayerForLeague(league);
    }


}
