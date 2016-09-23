package de.footballmanager.backend.domain;

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
    public static final PlayingSystem SYSTEM_4_4_2_DIAMOND = new PlayingSystem(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_4_4_2 = new PlayingSystem(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_STRIKER, RIGHT_STRIKER));
    public static final PlayingSystem SYSTEM_4_2_3_1 = new PlayingSystem(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, LEFT_DEFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, CENTRAL_STRIKER));
    public static final PlayingSystem SYSTEM_4_3_3 = new PlayingSystem(newArrayList(GOALY, LEFT_DEFENDER, LEFT_STOPPER, RIGHT_STOPPER,
            RIGHT_DEFENDER, CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            LEFT_WINGER, RIGHT_WINGER, CENTRAL_STRIKER));
    public static final PlayingSystem SYSTEM_3_4_3 = new PlayingSystem(newArrayList(GOALY, CENTRAL_STOPPER, LEFT_STOPPER, RIGHT_STOPPER,
            CENTRAL_DEFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, RIGHT_MIDFIELDER,
            CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_WINGER, RIGHT_WINGER, CENTRAL_STRIKER));

    static {
        STANDARD_SYSTEMS.addAll(newArrayList(SYSTEM_3_4_3, SYSTEM_4_2_3_1, SYSTEM_4_3_3, SYSTEM_4_4_2,
                SYSTEM_4_4_2_DIAMOND));
    }

    

    private List<Position> positions = Lists.newArrayListWithCapacity(11);

    private PlayingSystem(List<Position> positions) {
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
        PlayingSystem system = (PlayingSystem) o;
        return Objects.equals(positions, system.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positions);
    }

}
