package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.enumeration.Position;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.footballmanager.backend.enumeration.Position.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class StrengthServiceTest {

    private StrengthService strengthService;

    @Before
    public void setUp() {
        strengthService = new StrengthService();
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
