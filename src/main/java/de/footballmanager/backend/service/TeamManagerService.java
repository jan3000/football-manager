package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamManagerService {

    @Autowired
    private StrengthService strengthService;

    private static final Comparator<Player> MAX_STRENGTH_COMPARATOR = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            return o1.getStrength() - o2.getStrength();
        }
    };

    public void setTeamManager(Manager manager, Team team) {
        Preconditions.checkNotNull(manager, "manager must be set");
        Preconditions.checkNotNull(team, "team must be set");
        team.setManager(manager);
    }

    public void setStartElevenIfComputerManaged(MatchDay matchDay) {
        matchDay.getMatches().forEach(match -> {
            Team homeTeam = match.getHomeTeam();
            Team guestTeam = match.getGuestTeam();
            Lists.newArrayList(homeTeam, guestTeam).forEach(team -> {
                if (isTeamMangedByComputer(team)) {
                    Pair<PlayingSystem, Map<Position, Player>> pair = getBestPlayersForBestSystem(team);
                    match.setPositionPlayerMapHomeTeam(pair.getSecond());
                }
            });
        });
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
                .collect(Collectors.toList());
    }

    public Map<Position, Player> setBestPlayersForSystem(Team team, PlayingSystem playingSystem) {
        Preconditions.checkNotNull(playingSystem, "playingSystem must be set");
        Preconditions.checkArgument(team.getPlayers().size() > 7, "team must have at least 8 players");
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        List<Player> players = Lists.newArrayList(team.getPlayers());

        // first fill in players with exact matching positions
        ListMultimap<Position, Player> positionToPlayerMap = getPositionToPlayerMap(team);
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
        Assert.isTrue(positionPlayerMap.size() == team.getPlayers().size() || positionPlayerMap.size() == 11);
        Assert.isTrue(!positionPlayerMap.values().contains(null));
        return positionPlayerMap;

    }

    private int getPlayerStrengthOnPosition(Position position, Player player) {
        System.out.println("getPlayerStrengthOnPosition: " + player);
        System.out.println("getPlayerStrengthOnPosition: " + position);
        return strengthService.getPlayerStrengthOnPosition(position, player);
    }


    public ListMultimap<Position, Player> getPositionToPlayerMap(Team team) {
        ListMultimap<Position, Player> positionToPlayers = ArrayListMultimap.create();
        team.getPlayers().forEach(player -> positionToPlayers.put(player.getPosition(), player));
        return positionToPlayers;
    }

    public Optional<Player> getPlayerByName(Team team, String firstName, String lastName) {
        return team.getPlayers().stream()
                .filter(player -> player.getFirstname().equals(firstName) && player.getLastname().equals(lastName))
                .findFirst();
    }



    // TODO: later if position not set find best matching player for missing position
    public Map<Position, Player> getBestPlayersForSystem(Team team, PlayingSystem playingSystem) {
        Preconditions.checkArgument(team.getPlayers().size() > 11, "team must have at least 11 players");
        Preconditions.checkArgument(playingSystem != null, "playingSystem must be set");
        Map<Position, Player> positionPlayerMap = Maps.newHashMap();
        ListMultimap<Position, Player> positionToPlayerMap = getPositionToPlayerMap(team);
        List<Position> positions = playingSystem.getPositions();
        positions.forEach(position -> {
            List<Player> players = positionToPlayerMap.get(position);
            Player player = players.stream().max(MAX_STRENGTH_COMPARATOR).orElse(players.get(0));
            players.remove(player);
            positionPlayerMap.put(position, player);
        });
        return positionPlayerMap;
    }

    // TODO implement
    public void getBestAlternative(Team team, List<PlayingSystem> playingSystems) {
        List<Player> players = team.getPlayers();
        Map<PlayingSystem, Integer> systemIntegerMap = Maps.newHashMap();
        playingSystems.forEach(playingSystem -> {
            Map<Position, Player> positionPlayerMap = setBestPlayersForSystem(team, playingSystem);

        });
    }

    public Pair<PlayingSystem, Map<Position, Player>> getBestPlayersForBestSystem(Team team) {
        List<PlayingSystem> possibleSystems = getPossibleSystems(team);
        if (possibleSystems.isEmpty()) {
            Map<Position, Player> positionPlayerMap = setBestPlayersForSystem(team, PlayingSystem.SYSTEM_4_3_3);// TODO: best players for a list of systems, better coach -> more systems
            return new Pair<>(PlayingSystem.SYSTEM_4_3_3, positionPlayerMap);
        }
        Map<PlayingSystem, Integer> systemStrengthMap = Maps.newHashMap();
        Map<PlayingSystem, Map<Position, Player>> systemToBestElevenMap = Maps.newHashMap();
        possibleSystems.forEach(playingSystem -> {
            Map<Position, Player> positionPlayerMap = getBestPlayersForSystem(team, playingSystem);
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


//    Map<Position, Player> getPositionPlayerMap(Team team) {
//        team.getPlayers();
//    }

    private boolean isTeamMangedByComputer(Team team) {
        Preconditions.checkNotNull(team.getManager(), "no manager set for team {}", team.getName());
        return team.getManager().isComputerManaged();
    }

}
