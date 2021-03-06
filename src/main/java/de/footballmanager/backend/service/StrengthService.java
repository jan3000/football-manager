package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.enumeration.Position;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.footballmanager.backend.enumeration.Position.GOALY;

@Service
public class StrengthService {

    static final int COEFFICIENT_WRONG_GOALY = 90;
    static final int COEFFICIENT_WING_TO_CENTRAL = 18;
    static final int COEFFICIENT_SAME_LEVEL = 15;
    static final int COEFFICIENT_NOT_SAME_LEVEL = 40;
    static final int COEFFICIENT_OPPOSITE_SIDE = 20;
    private static final String LEFT = "LEFT";
    private static final String RIGHT = "RIGHT";
    private static final String CENTRAL = "CENTRAL";

    public int getStrength(Map<Position, Player> positionToPlayer) {
        int sum = positionToPlayer.keySet().stream()
                .mapToInt(position -> getPlayerStrengthOnPosition(position, positionToPlayer.get(position)))
                .sum();
        return new Double(sum / positionToPlayer.size()).intValue();
    }

    int getPlayerStrengthOnPosition(Position position, Player player) {
        double coefficient = 100;
        if (!isSamePosition(position, player.getPosition())) {
            if (isNonGoalyInGoal(position, player) || isGoalyInField(position, player)) {
                coefficient = coefficient - COEFFICIENT_WRONG_GOALY;
                return new Double(coefficient / 100d * player.getStrength()).intValue();
            }
            if (isWingToCentral(position, player.getPosition()) || isCentralToWing(position, player.getPosition())) {
                coefficient = coefficient - COEFFICIENT_WING_TO_CENTRAL;
            }
            if (isSameLevel(position, player.getPosition())) {
                coefficient = coefficient - COEFFICIENT_SAME_LEVEL;
            } else {
                coefficient = coefficient - COEFFICIENT_NOT_SAME_LEVEL;
            }
            if (isOppositeSide(position, player)) {
                coefficient = coefficient - COEFFICIENT_OPPOSITE_SIDE;
            }
        }

        return new Double(coefficient / 100 * player.getStrength()).intValue();
    }

    boolean isOppositeSide(Position position, Player player) {
        return (containsValue(position, LEFT) && containsValue(player.getPosition(), RIGHT)) ||
                (containsValue(position, RIGHT) && containsValue(player.getPosition(), LEFT));
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
        return (containsValue(position1, LEFT) || containsValue(position1, RIGHT))
                && containsValue(position2, CENTRAL);
    }

    boolean isCentralToWing(Position position1, Position position2) {
        return (containsValue(position2, LEFT) || containsValue(position2, RIGHT))
                && containsValue(position1, CENTRAL);
    }

    private boolean containsValue(Position position1, String left) {
        return position1.name().contains(left);
    }

}
