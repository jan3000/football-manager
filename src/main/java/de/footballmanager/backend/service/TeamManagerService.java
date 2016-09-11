package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static de.footballmanager.backend.enumeration.Position.*;

@Service
public class TeamManagerService {

    private Map<String, Team> userToTeam = Maps.newHashMap();

    public static final Set<System> STANDARD_SYSTEMS = Sets.newHashSet();
    public static final System SYSTEM_4_4_2_DIAMOND = new System(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final System SYSTEM_4_4_2 = new System(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final System SYSTEM_4_2_3_1 = new System(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, CENTRAL_STRIKER));
    public static final System SYSTEM_4_3_3 = new System(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            LEFT_WINGER, RIGHT_WINGER, CENTRAL_STRIKER));
    public static final System SYSTEM_3_4_3 = new System(newArrayList(GOALY, CENTRAL_STOPPER, LEFT_STOPPER, RIGHT_STOPPER,
            CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_WINGER, RIGHT_WINGER, CENTRAL_STRIKER));

    static {
        STANDARD_SYSTEMS.addAll(newArrayList(SYSTEM_3_4_3, SYSTEM_4_2_3_1, SYSTEM_4_3_3, SYSTEM_4_4_2,
                SYSTEM_4_4_2_DIAMOND));
    }


    public void setStartEleven(MatchDay matchDay) {
        matchDay.getMatches().forEach(match -> {
            if (isTeamMangedByComputer(match.getHomeTeam())) {

            }
        });
    }

    public boolean hasPlayerForSystem(Team team, System system) {
        List<Position> positionsInTeam = Lists.newArrayList();
        List<Player> players = team.getPlayers();
        players.forEach(player -> positionsInTeam.add(player.getPosition()));

        return positionsInTeam.containsAll(system.getPositions());
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

    public static class System {
        List<Position> positions = Lists.newArrayListWithCapacity(11);

        public System(List<Position> positions) {
            Preconditions.checkArgument(positions.size() == 11, "A system must contain 11 positions");
            this.positions = positions;
        }

        public List<Position> getPositions() {
            return positions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            System system = (System) o;
            return Objects.equals(positions, system.positions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(positions);
        }
    }
}
