package de.footballmanager.backend.service;

import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.enumeration.Position;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static de.footballmanager.backend.enumeration.Position.*;
import static de.footballmanager.backend.service.StrengthService.*;
import static de.footballmanager.backend.util.TestUtil.createPlayer;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class StrengthServiceTest {

    private StrengthService strengthService;

    @Before
    public void setUp() {
        strengthService = new StrengthService();
    }

    @Test
    public void getStrength() {
        HashMap<Position, Player> positionToPlayer = Maps.newHashMap();
        positionToPlayer.put(GOALY, createPlayer(GOALY, 100));
        positionToPlayer.put(CENTRAL_STOPPER, createPlayer(CENTRAL_STOPPER, 100));
        positionToPlayer.put(CENTRAL_DEFENSIVE_MIDFIELDER, createPlayer(CENTRAL_DEFENSIVE_MIDFIELDER, 100));
        assertEquals(300, strengthService.getStrength(positionToPlayer));
    }


    @Test
    @Parameters(method = "paramsForGetPlayerStrengthOnPosition")
    public void getPlayerStrengthOnPosition(Position position1, Position playerPosition, int expectedStrength) {
        Player player = new Player.Builder("James", "First").setPosition(playerPosition).build();
        player.setStrength(100);
        int playerStrengthOnPosition = strengthService.getPlayerStrengthOnPosition(position1, player);
        assertEquals(expectedStrength, playerStrengthOnPosition);
    }

    private Object[] paramsForGetPlayerStrengthOnPosition() {
        return new Object[]{
                new Object[]{GOALY, GOALY, 100},
                new Object[]{LEFT_DEFENDER, LEFT_DEFENDER, 100},
                new Object[]{GOALY, LEFT_DEFENDER, 100 - COEFFICIENT_WRONG_GOALY},
                new Object[]{CENTRAL_STRIKER, GOALY, 100 - COEFFICIENT_WRONG_GOALY},
                new Object[]{CENTRAL_STRIKER, CENTRAL_STOPPER, 100 - COEFFICIENT_NOT_SAME_LEVEL},
                new Object[]{CENTRAL_STRIKER, LEFT_STRIKER, 100 - COEFFICIENT_WING_TO_CENTRAL - COEFFICIENT_SAME_LEVEL},
                new Object[]{LEFT_STRIKER, CENTRAL_STRIKER, 100 - COEFFICIENT_WING_TO_CENTRAL - COEFFICIENT_SAME_LEVEL},
                new Object[]{CENTRAL_STRIKER, LEFT_DEFENDER, 100 - COEFFICIENT_NOT_SAME_LEVEL - COEFFICIENT_WING_TO_CENTRAL},
        };
    }


    @Test
    @Parameters(method = "paramsForIsSamePosition")
    public void isSamePosition(Position position1, Position position2, boolean isTrue) {
        assertEquals(isTrue, strengthService.isSamePosition(position1, position2));
    }

    private Object[] paramsForIsSamePosition() {
        return new Object[]{
                new Object[]{GOALY, GOALY, true},
                new Object[]{GOALY, CENTRAL_DEFENSIVE_MIDFIELDER, false},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, GOALY, false}
        };
    }

    @Test
    @Parameters(method = "paramsForIsGoalyInField")
    public void isGoalyInField(Position position1, Position playerPosition, boolean isTrue) {
        Player player = new Player.Builder("James", "First").setPosition(playerPosition).build();
        assertEquals(isTrue, strengthService.isGoalyInField(position1, player));
    }

    private Object[] paramsForIsGoalyInField() {
        return new Object[]{
                new Object[]{GOALY, GOALY, false},
                new Object[]{GOALY, CENTRAL_DEFENSIVE_MIDFIELDER, false},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, GOALY, true}
        };
    }

    @Test
    @Parameters(method = "paramsForIsSameLevel")
    public void isSameLevel(Position position1, Position position2, boolean isTrue) {
        assertEquals(isTrue, strengthService.isSameLevel(position1, position2));
    }

    private Object[] paramsForIsSameLevel() {
        return new Object[]{
                new Object[]{GOALY, GOALY, true},
                new Object[]{CENTRAL_STOPPER, LEFT_STOPPER, true},
                new Object[]{CENTRAL_STOPPER, RIGHT_STOPPER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_MIDFIELDER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, RIGHT_MIDFIELDER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, RIGHT_DEFENSIVE_MIDFIELDER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_DEFENSIVE_MIDFIELDER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, CENTRAL_DEFENSIVE_MIDFIELDER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_OFFENSIVE_MIDFIELDER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, RIGHT_OFFENSIVE_MIDFIELDER, true},
                new Object[]{RIGHT_STRIKER, LEFT_STRIKER, true},
                new Object[]{RIGHT_STRIKER, CENTRAL_STRIKER, true},
                new Object[]{LEFT_WINGER, RIGHT_WINGER, true},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, CENTRAL_STRIKER, false},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, RIGHT_STOPPER, false},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, LEFT_DEFENDER, false},
        };
    }

    @Test
    @Parameters(method = "parametersForIsWingToCentral")
    public void isWingToCentral(Position position1, Position position2, boolean isTrue) {
        assertEquals(isTrue, strengthService.isWingToCentral(position1, position2));
    }

    private Object[] parametersForIsWingToCentral() {
        return new Object[]{
                new Object[]{LEFT_DEFENDER, CENTRAL_OFFENSIVE_MIDFIELDER, true},
                new Object[]{LEFT_DEFENDER, LEFT_MIDFIELDER, false},
                new Object[]{LEFT_DEFENDER, GOALY, false},
                new Object[]{CENTRAL_OFFENSIVE_MIDFIELDER, CENTRAL_DEFENSIVE_MIDFIELDER, false}
        };
    }


    @Test
    @Parameters(method = "parametersForIsNonGoalyInGoal")
    public void isNonGoalyInGoal(Position position, Position playerPosition, boolean isTrue) {
        Player player = new Player.Builder("James", "Last").setPosition(playerPosition).build();
        assertEquals(isTrue, strengthService.isNonGoalyInGoal(position, player));
    }

    private Object[] parametersForIsNonGoalyInGoal() {
        return new Object[]{
                new Object[]{GOALY, GOALY, false},
                new Object[]{GOALY, CENTRAL_DEFENSIVE_MIDFIELDER, true},
                new Object[]{CENTRAL_STRIKER, CENTRAL_DEFENSIVE_MIDFIELDER, false},
        };
    }


}
