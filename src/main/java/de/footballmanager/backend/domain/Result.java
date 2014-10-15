package de.footballmanager.backend.domain;

import de.footballmanager.backend.enumeration.ResultType;

public class Result {

    int homeGoals;
    int guestGoals;

    public Result(final int homeGoals, final int guestGoals) {
        super();
        this.homeGoals = homeGoals;
        this.guestGoals = guestGoals;
    }

    public ResultType getResultType() {
        if (homeGoals > guestGoals) {
            return ResultType.HOME_WON;
        } else if (homeGoals < guestGoals) {
            return ResultType.GUEST_WON;
        } else {
            return ResultType.DRAW;
        }
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void increaseHomeGoal() {
        homeGoals = homeGoals + 1;
    }

    public void increaseGuestGoal() {
        guestGoals = guestGoals + 1;
    }

    public int getGuestGoals() {
        return guestGoals;
    }

    public String print() {
        return homeGoals + " : " + guestGoals;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + guestGoals;
        result = prime * result + homeGoals;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Result other = (Result) obj;
        if (guestGoals != other.guestGoals) {
            return false;
        }
        if (homeGoals != other.homeGoals) {
            return false;
        }
        return true;
    }

}
