package de.footballmanager.backend.enumeration;

public enum Position {


    GOALY(1), STOPPER(20), LEFT_DEFENDER(20), RIGHT_DEFENDER(20), DEFENSIVE_MIDFIELDER(40), OFFENSIVE_MIDFIELDER(40),
    RIGHT_MIDFIELDER(40), LEFT_MIDFIELDER(40), LEFT_WINGER(70), RIGHT_WINGER(70), STRIKER(100);


    int probabilityOfShootingGoal;

    Position(int probabilityOfShootingGoal) {
        this.probabilityOfShootingGoal = probabilityOfShootingGoal;
    }

    public int getProbabilityOfShootingGoal() {
        return probabilityOfShootingGoal;
    }
}
