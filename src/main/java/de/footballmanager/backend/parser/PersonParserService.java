package de.footballmanager.backend.parser;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import de.footballmanager.backend.domain.league.League;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.service.ClubService;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PersonParserService {


    private static final Position[] ALL_POSITIONS = Position.values();
    static final int MINIMAL_NUMBER_OF_PLAYERS = 20;
    private static final Path RESOURCE_DIRECTORY = Paths.get("src/main/resources");
    private static final Random RANDOM = new Random();

    private List<Position> positions;
    private List<Integer> strengths;
    private League league;

    public void parsePlayerForLeague(League league, String firstNameFilePath, String lastNameFilePath) {
        Preconditions.checkArgument(league != null);
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(league.getTeams()), "teams must be set in league");
        List<String> names = normalizeNames(readInNames(firstNameFilePath));
        List<String> surnames = normalizeNames(readInNames(lastNameFilePath));

        for (Team team : league.getTeams()) {
            int numberOfPlayers = getNumberOfPlayers();
            setPositionsOfPlayers(numberOfPlayers);

            for (int i = 0; i < numberOfPlayers; i++) {
                String firstName = getRandomName(names);
                String surName = getRandomName(surnames);

                Player player = new Player.Builder(firstName, surName).setPosition(positions.get(i)).build();
                player.setHomeCountry(CountryCode.DE);
                player.setStrength(getStrength(team));
                player.setDateOfBirth(getDate());

                team.getPlayers().add(player);
            }
        }
    }

    public String getName(String nameFilePath) {
        List<String> names = normalizeNames(readInNames(nameFilePath));
        return getRandomName(names);
    }

    private String getRandomName(List<String> names) {
        return names.get(RANDOM.nextInt(names.size()));
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
        positions.add(Position.CENTRAL_STOPPER);
        positions.add(Position.LEFT_STOPPER);
        positions.add(Position.LEFT_DEFENDER);
        positions.add(Position.LEFT_DEFENDER);
        positions.add(Position.RIGHT_DEFENDER);
        positions.add(Position.RIGHT_DEFENDER);
        positions.add(Position.CENTRAL_DEFENSIVE_MIDFIELDER);
        positions.add(Position.RIGHT_DEFENSIVE_MIDFIELDER);
        positions.add(Position.LEFT_MIDFIELDER);
        positions.add(Position.LEFT_MIDFIELDER);
        positions.add(Position.RIGHT_MIDFIELDER);
        positions.add(Position.CENTRAL_OFFENSIVE_MIDFIELDER);
        positions.add(Position.CENTRAL_OFFENSIVE_MIDFIELDER);
        positions.add(Position.LEFT_WINGER);
        positions.add(Position.RIGHT_WINGER);
        positions.add(Position.RIGHT_WINGER);
        positions.add(Position.RIGHT_STRIKER);
        positions.add(Position.CENTRAL_STRIKER);

        for (int i = MINIMAL_NUMBER_OF_PLAYERS; i < numberOfPlayers; i++) {
            positions.add(ALL_POSITIONS[RANDOM.nextInt(ALL_POSITIONS.length)]);
        }
    }

    private List<String> normalizeNames(List<String> names) {
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


}
