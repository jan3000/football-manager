package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamManagerService {

    private Map<String, Team> userToTeam = Maps.newHashMap();

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

    public void setBestPlayersForSystem(Team team, PlayingSystem playingSystem) {
//        playingSystem.getPositions().
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

    public Pair<PlayingSystem, Map<Position, Player>> getBestPlayersForBestSystem(Team team) {
        List<PlayingSystem> possibleSystems = getPossibleSystems(team);
        if (possibleSystems.isEmpty()) {

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
        return new Pair<PlayingSystem, Map<Position, Player>>(bestPlayingSystem, systemToBestElevenMap.get(bestPlayingSystem));
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
