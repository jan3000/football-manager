package de.footballmanager.backend.enumeration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.footballmanager.backend.enumeration.Position;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static de.footballmanager.backend.enumeration.Position.*;

public final class PlayingSystem {


    public static final Set<PlayingSystem> STANDARD_SYSTEMS = Sets.newHashSet();
    public static final PlayingSystem SYSTEM_4_4_2_DIAMOND = new PlayingSystem("442_diamond", newArrayList(GOALY,
            LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_4_4_2 = new PlayingSystem("442", newArrayList(GOALY, LEFT_DEFENDER,
            LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER,
            LEFT_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_4_2_3_1 = new PlayingSystem("4231", newArrayList(GOALY, LEFT_DEFENDER,
            LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, CENTRAL_STRIKER));
    public static final PlayingSystem SYSTEM_4_3_3 = new PlayingSystem("433", newArrayList(GOALY, LEFT_DEFENDER,
            LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            LEFT_WINGER, RIGHT_WINGER, CENTRAL_STRIKER));
    public static final PlayingSystem SYSTEM_3_4_3 = new PlayingSystem("343", newArrayList(GOALY, CENTRAL_STOPPER,
            LEFT_STOPPER, RIGHT_STOPPER,
            CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_WINGER, RIGHT_WINGER, CENTRAL_STRIKER));
    public static final PlayingSystem SYSTEM_4_2_4 = new PlayingSystem("424", newArrayList(GOALY, LEFT_DEFENDER,
            LEFT_STOPPER, RIGHT_STOPPER, RIGHT_DEFENDER,
            CENTRAL_DEFENSIVE_MIDFIELDER, CENTRAL_OFFENSIVE_MIDFIELDER,
            LEFT_WINGER, RIGHT_WINGER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_5_3_2 = new PlayingSystem("532", newArrayList(GOALY, CENTRAL_STOPPER,
            LEFT_STOPPER, RIGHT_STOPPER, LEFT_DEFENDER, RIGHT_DEFENDER,
            LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, CENTRAL_OFFENSIVE_MIDFIELDER,
            LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_3_5_2 = new PlayingSystem("352", newArrayList(GOALY, CENTRAL_STOPPER,
            LEFT_STOPPER, RIGHT_STOPPER,
            LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER,
            LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_5_4_1 = new PlayingSystem("541", newArrayList(GOALY, CENTRAL_STOPPER,
            LEFT_STOPPER, RIGHT_STOPPER, LEFT_DEFENDER, RIGHT_DEFENDER,
            CENTRAL_DEFENSIVE_MIDFIELDER, CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_STRIKER));


    static {
        STANDARD_SYSTEMS.addAll(newArrayList(SYSTEM_3_4_3, SYSTEM_4_2_3_1, SYSTEM_4_3_3, SYSTEM_4_4_2,
                SYSTEM_4_4_2_DIAMOND, SYSTEM_4_2_4, SYSTEM_5_3_2, SYSTEM_3_5_2, SYSTEM_5_4_1));
    }

    
    private String name;
    private List<Position> positions = Lists.newArrayListWithCapacity(11);


    private PlayingSystem(String name, List<Position> positions) {
        Preconditions.checkArgument(positions.size() == 11, "A system must contain 11 positions");
        this.positions = positions;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public static PlayingSystem getPlayingSystem(Set<Position> positions) {
        Preconditions.checkArgument(positions.size() == 11, "11 positions must be passed to determine the system");
        return STANDARD_SYSTEMS.stream()
                .filter(playingSystem -> playingSystem.getPositions().containsAll(positions))
                .findFirst().orElseGet(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayingSystem that = (PlayingSystem) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(positions, that.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, positions);
    }

    @Override
    public String toString() {
        return "PlayingSystem{" +
                "name='" + name + '\'' +

                '}';
    }
}
