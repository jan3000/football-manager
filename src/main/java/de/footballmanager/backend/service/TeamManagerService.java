package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.util.Pair;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class TeamManagerService {

    @Autowired
    private StrengthService strengthService;
    @Autowired
    private ClubService clubService;

    private static final Comparator<Player> MAX_STRENGTH_COMPARATOR = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            return o1.getStrength() - o2.getStrength();
        }
    };

    public void setTeamManager(Manager manager, Team team) {
        Preconditions.checkNotNull(manager, "manager must be set");
        Preconditions.checkNotNull(team, "team must be set");
        clubService.setManager(team.getName(), manager);
    }


    public Map<Position, Player> getCurrentlyPlayingPlayers(Match match, Team team) {
        Preconditions.checkArgument(match.containsTeam(team), "team not contained in match: ", team);
        if (match.isHomeTeam(team)) {
            return match.getPositionPlayerMapHomeTeam();
        } else if (match.isGuestTeam(team)) {
            return match.getPositionPlayerMapGuestTeam();
        }
        throw new IllegalStateException("team neither home nor guest team: " + team);
    }

    public List<Player> getSubstituteBench(Match match, Team team) {
        Map<Position, Player> currentlyPlayingPlayers = getCurrentlyPlayingPlayers(match, team);
        return team.getPlayers().stream()
                .filter(player -> !currentlyPlayingPlayers.containsValue(player))
                .collect(toList());
    }

    public void setStartEleven(Match match, Team team, Map<Position, Player> positionToPlayerMap) {
        Preconditions.checkArgument(match.containsTeam(team), "team not contained in match: ", team);
        if (match.isHomeTeam(team)) {
            match.setPositionPlayerMapHomeTeam(positionToPlayerMap);
        } else if (match.isGuestTeam(team)) {
            match.setPositionPlayerMapGuestTeam(positionToPlayerMap);
        } else {
            throw new IllegalStateException("team neither home nor guest team: " + team);
        }
    }



    public boolean hasPlayerForSystem(Team team, PlayingSystem system) {
        List<Position> positionsInTeam = Lists.newArrayList();
        List<Player> players = team.getPlayers();
        players.forEach(player -> positionsInTeam.add(player.getPosition()));

        return positionsInTeam.containsAll(system.getPositions());
    }

    public List<PlayingSystem> getPossibleSystems(Team team) {
        return PlayingSystem.STANDARD_SYSTEMS.stream()
                .filter(playingSystem -> hasPlayerForSystem(team, playingSystem))
                .collect(toList());
    }

    public Map<Position, Player> setBestPlayersForSystems(List<PlayingSystem> playingSystems, List<Player> playerList) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(playingSystems), "playingSystems must be set");
        Preconditions.checkArgument(playerList.size() > 7, "team must have at least 8 players");
        Map<Integer, Map<Position, Player>> strengthToPositionPlayerMap = Maps.newHashMap();
        playingSystems.forEach(playingSystem -> {
            Map<Position, Player> positionPlayerMap = setBestPlayersForSystems(playingSystem, playerList);
            int strength = strengthService.getStrength(positionPlayerMap);
            strengthToPositionPlayerMap.put(strength, positionPlayerMap);
        });
        List<Integer> strengths = Lists.newArrayList(strengthToPositionPlayerMap.keySet());
        strengths.sort((o1, o2) -> o1 < o2 ? 1 : -1);
        return strengthToPositionPlayerMap.get(strengths.get(0));
    }

        public Map<Position, Player> setBestPlayersForSystems(PlayingSystem playingSystem, List<Player> playerList) {
        Preconditions.checkNotNull(playingSystem, "playingSystem must be set");
        Preconditions.checkArgument(playerList.size() > 7, "team must have at least 8 players");
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        List<Player> players = Lists.newArrayList(playerList);

        // first fill in players with exact matching positions
        ListMultimap<Position, Player> positionToPlayerMap = getPositionToPlayerMap(playerList);
        playingSystem.getPositions().forEach(position -> {
            List<Player> playersForPosition = positionToPlayerMap.get(position);
            if (!playersForPosition.isEmpty()) {
                Player playerForPosition = playersForPosition.stream().max(
                        (p1, p2) -> getPlayerStrengthOnPosition(position, p1) > getPlayerStrengthOnPosition(position, p2) ? 1 : -1)
                        .orElse(players.get(new Random().nextInt(players.size())));
                positionPlayerMap.put(position, playerForPosition);
                players.remove(playerForPosition);
            }
        });

        // fill missing positions
        playingSystem.getPositions().forEach(position -> {
            if (!positionPlayerMap.containsKey(position)) {
                Player bestPlayer = players.stream().max(
                        (p1, p2) -> getPlayerStrengthOnPosition(position, p1) > getPlayerStrengthOnPosition(position, p2) ? 1 : -1)
                        .orElse(players.get(new Random().nextInt(players.size())));
                positionPlayerMap.put(position, bestPlayer);
                players.remove(bestPlayer);
            }
        });
        // post conditions
        Assert.isTrue(positionPlayerMap.size() == playerList.size() || positionPlayerMap.size() == 11);
        Assert.isTrue(!positionPlayerMap.values().contains(null));
        return positionPlayerMap;

    }

    private int getPlayerStrengthOnPosition(Position position, Player player) {
        return strengthService.getPlayerStrengthOnPosition(position, player);
    }

    public List<Player> getPlayerByPosition(List<Player> players, Position position) {
        return getPositionToPlayerMap(players).get(position);
    }

    public ListMultimap<Position, Player> getPositionToPlayerMap(List<Player> players) {
        ListMultimap<Position, Player> positionToPlayers = ArrayListMultimap.create();
        players.forEach(player -> positionToPlayers.put(player.getPosition(), player));
        return positionToPlayers;
    }

    public Optional<Player> getPlayerByName(Team team, String firstName, String lastName) {
        return team.getPlayers().stream()
                .filter(player -> player.getFirstName().equals(firstName) && player.getLastName().equals(lastName))
                .findFirst();
    }

    /**
     * Position is kept
     */
    public void changePlayer(Match match, Team team, Player in, Player out) {
        match.changePlayer(team, in, out);
    }

    /**
     * Players are kept
     */
    public void changePlayingSystem(Match match, Team team, PlayingSystem newSystem) {
        Preconditions.checkArgument(match.containsTeam(team), "team not contained in match: ", team);
        if (match.isHomeTeam(team)) {
            Map<Position, Player> positionPlayerMap = match.getPositionPlayerMapHomeTeam();
            Collection<Player> players = positionPlayerMap.values();
            Map<Position, Player> positionPlayerMapAfterChange = setBestPlayersForSystems(newSystem, Lists.newArrayList(players));
            match.setPositionPlayerMapHomeTeam(positionPlayerMapAfterChange);
        } else if (match.isGuestTeam(team)) {
            Map<Position, Player> positionPlayerMap = match.getPositionPlayerMapGuestTeam();
            Collection<Player> players = positionPlayerMap.values();
            Map<Position, Player> positionPlayerMapAfterChange = setBestPlayersForSystems(newSystem, Lists.newArrayList(players));
            match.setPositionPlayerMapGuestTeam(positionPlayerMapAfterChange);
        } else {
            throw new IllegalStateException("team neither home nor guest team: " + team);
        }
    }

    public PlayingSystem getPlayingSystem(Match match, Team team) {
        Map<Position, Player> positionPlayerMap;
        if (match.isHomeTeam(team)) {
            positionPlayerMap = match.getPositionPlayerMapHomeTeam();
        } else if (match.isGuestTeam(team)) {
            positionPlayerMap = match.getPositionPlayerMapGuestTeam();
        } else {
            throw new IllegalStateException("team neither home nor guest team: " + team);
        }

        Set<Position> positions = positionPlayerMap.keySet();
        return PlayingSystem.STANDARD_SYSTEMS.stream()
                .filter(playingSystem -> {
                    return playingSystem.getPositions().containsAll(positions);
                })
        .findFirst().orElse(null);

    }


    /**
     * Note: Do not use directly. Use getBestPlayersForBestSystem instead.
     * @param playingSystem
     * @param players
     * @return
     */
    Map<Position, Player> getBestPlayersForSystem(PlayingSystem playingSystem, List<Player> players) {
        Preconditions.checkArgument(players.size() >= 11, "team must have at least 11 players");
        Preconditions.checkArgument(playingSystem != null, "playingSystem must be set");
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        ListMultimap<Position, Player> positionToPlayerMap = getPositionToPlayerMap(players);
        List<Position> positions = playingSystem.getPositions();
        positions.forEach(position -> {
            List<Player> playersOnCurrentPosition = positionToPlayerMap.get(position);
            Player player = playersOnCurrentPosition.stream().max(MAX_STRENGTH_COMPARATOR).orElse(playersOnCurrentPosition.get(0));
            playersOnCurrentPosition.remove(player);
            positionPlayerMap.put(position, player);
        });
        return positionPlayerMap;
    }

    // TODO implement
    public void getBestAlternative(Team team, List<PlayingSystem> playingSystems) {
        List<Player> players = team.getPlayers();
        Map<PlayingSystem, Integer> systemIntegerMap = Maps.newHashMap();
        playingSystems.forEach(playingSystem -> {
            Map<Position, Player> positionPlayerMap = setBestPlayersForSystems(playingSystem, team.getPlayers());

        });
    }

    public Pair<PlayingSystem, Map<Position, Player>> getBestPlayersForBestSystem(Team team) {
        List<PlayingSystem> possibleSystems = getPossibleSystems(team);
        if (possibleSystems.isEmpty()) {
            Map<Position, Player> positionPlayerMap = setBestPlayersForSystems(PlayingSystem.SYSTEM_4_3_3, team.getPlayers());// TODO: best players for a list of systems, better coach -> more systems
            return new Pair<>(PlayingSystem.SYSTEM_4_3_3, positionPlayerMap);
        }
        Map<PlayingSystem, Integer> systemStrengthMap = Maps.newHashMap();
        Map<PlayingSystem, Map<Position, Player>> systemToBestElevenMap = Maps.newHashMap();
        possibleSystems.forEach(playingSystem -> {
            Map<Position, Player> positionPlayerMap = getBestPlayersForSystem(playingSystem, team.getPlayers());
            systemToBestElevenMap.put(playingSystem, positionPlayerMap);
            systemStrengthMap.put(playingSystem, getTeamStrength(positionPlayerMap.values()));
        });
        PlayingSystem bestPlayingSystem = systemStrengthMap
                .entrySet()
                .stream()
                .max((entry1, entry2) -> {
                    return entry1.getValue() > entry2.getValue() ? 1 : -1;
                }).get()
                .getKey();
        return new Pair<>(bestPlayingSystem, systemToBestElevenMap.get(bestPlayingSystem));
    }

    public int getTeamStrength(Collection<Player> players) {
        Preconditions.checkArgument(players.size() < 12, "number of players must be not more than 11");
        return new Double(Math.floor(players.stream().mapToInt(Player::getStrength).sum() / players.size())).intValue();
    }

}
