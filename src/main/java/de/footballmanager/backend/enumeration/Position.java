package de.footballmanager.backend.enumeration;

public enum Position {


    GOALY(1),
    CENTRAL_STOPPER(50), LEFT_STOPPER(50), RIGHT_STOPPER(50),
    LEFT_DEFENDER(50), RIGHT_DEFENDER(50),
    CENTRAL_DEFENSIVE_MIDFIELDER(60),LEFT_DEFENSIVE_MIDFIELDER(60),RIGHT_DEFENSIVE_MIDFIELDER(60),
    CENTRAL_OFFENSIVE_MIDFIELDER(60),LEFT_OFFENSIVE_MIDFIELDER(60),RIGHT_OFFENSIVE_MIDFIELDER(60),
    RIGHT_MIDFIELDER(60), LEFT_MIDFIELDER(60),
    LEFT_WINGER(70), RIGHT_WINGER(70),
    CENTRAL_STRIKER(100), LEFT_STRIKER(100), RIGHT_STRIKER(100);


    int probabilityOfShootingGoal;

    Position(int probabilityOfShootingGoal) {
        this.probabilityOfShootingGoal = probabilityOfShootingGoal;
    }

    public int getProbabilityOfShootingGoal() {
        return probabilityOfShootingGoal;
    }
}
