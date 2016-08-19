package de.footballmanager.backend.enumeration;

public enum Position {


    GOALY(1), STOPPER(50), LEFT_DEFENDER(50), RIGHT_DEFENDER(50), DEFENSIVE_MIDFIELDER(60), OFFENSIVE_MIDFIELDER(60),
    RIGHT_MIDFIELDER(60), LEFT_MIDFIELDER(60), LEFT_WINGER(70), RIGHT_WINGER(70), STRIKER(100);


    int probabilityOfShootingGoal;

    Position(int probabilityOfShootingGoal) {
        this.probabilityOfShootingGoal = probabilityOfShootingGoal;
    }

    public int getProbabilityOfShootingGoal() {
        return probabilityOfShootingGoal;
    }
}
