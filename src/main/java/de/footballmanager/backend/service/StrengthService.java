package de.footballmanager.backend.service;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.enumeration.Position;
import javafx.geometry.Pos;

import java.util.Map;

import static de.footballmanager.backend.enumeration.Position.*;

public class StrengthService {

    private static final Map<PositionMapping, Integer> POSITION_STRENGTH_COEFFICIENT_MAP = Maps.newHashMap();
    private static final PositionMapping GOALY_TO_GOALY = new PositionMapping(LEFT_STRIKER, LEFT_STOPPER);
    static {


        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, LEFT_STOPPER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, RIGHT_STOPPER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, CENTRAL_STOPPER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, LEFT_DEFENDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, RIGHT_DEFENDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, CENTRAL_DEFENSIVE_MIDFIELDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, LEFT_DEFENSIVE_MIDFIELDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, RIGHT_DEFENSIVE_MIDFIELDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, CENTRAL_OFFENSIVE_MIDFIELDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, LEFT_OFFENSIVE_MIDFIELDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, RIGHT_OFFENSIVE_MIDFIELDER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, LEFT_WINGER), 1);
        POSITION_STRENGTH_COEFFICIENT_MAP.put(new PositionMapping(LEFT_STRIKER, RIGHT_WINGER), 1);
    }

    public int getStrength(Map<Position, Player> positionToPlayer) {
        int teamStrength = 0;
        positionToPlayer.keySet().forEach(position -> {
            Player player = positionToPlayer.get(position);
        });
        return 100;
    }

    public int getPlayerStrengthOnPosition(Position position, Player player) {
        int coefficient = 100;
        if (!isSamePosition(position, player.getPosition())) {
            if (isNonGoalyInGoal(position, player)) {
                coefficient = 10;
            }
            return 100;
        }
        return coefficient / 100 * player.getStrength();
    }

    boolean isNonGoalyInGoal(Position position, Player player) {
        return position.equals(GOALY) && !player.getPosition().equals(GOALY);
    }

    boolean isGoalyInField(Position position, Player player) {
        return !position.equals(GOALY) && player.getPosition().equals(GOALY);
    }

    boolean isSamePosition(Position position1, Position position2) {
        return position1.equals(position2);
    }

    boolean isSameLevel(Position position1, Position position2) {
        String[] split1 = position1.name().split("_");
        String[] split2 = position2.name().split("_");
        return split1[split1.length - 1].equals(split2[split2.length - 1]);
    }

    boolean isWingToCentral(Position position1, Position position2) {
        return (containsValue(position1, "LEFT") || containsValue(position1, "RIGHT"))
                && containsValue(position2, "CENTRAL");
    }

    private boolean containsValue(Position position1, String left) {
        return position1.name().contains(left);
    }

    public static class PositionMapping {
        private Position currentPosition;
        private Position playersPosition;

        public PositionMapping(Position currentPosition, Position playersPosition) {
            this.currentPosition = currentPosition;
            this.playersPosition = playersPosition;
        }

        public Position getCurrentPosition() {
            return currentPosition;
        }

        public Position getPlayersPosition() {
            return playersPosition;
        }
    }

}
