package de.footballmanager.backend.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.domain.PlayingSystem;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeamManagerService {

    private Map<String, Team> userToTeam = Maps.newHashMap();


    public void setStartEleven(MatchDay matchDay) {
        matchDay.getMatches().forEach(match -> {
            if (isTeamMangedByComputer(match.getHomeTeam())) {

            }
        });
    }

    boolean hasPlayerForSystem(Team team, PlayingSystem system) {
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

//    Map<Position, Player> getPositionPlayerMap(Team team) {
//        team.getPlayers();
//    }

    private boolean isTeamMangedByComputer(Team team) {
        return !userToTeam.values().contains(team);
    }

    public Map<String, Team> getUserToTeam() {
        return ImmutableMap.copyOf(userToTeam);
    }

    public void addUserToTeam(String userName, Team team) {
        this.userToTeam.put(userName, team);
    }

}
