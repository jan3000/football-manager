package de.footballmanager.backend.domain;

public class TableEntry {

    private int place;
    private Team team;
    private int points;
    private int homeGoals;
    private int awayGoals;
    private int receivedHomeGoals;
    private int receivedAwayGoals;

    private int homeGamesWon;
    private int homeGamesDraw;
    private int homeGamesLost;
    private int awayGamesWon;
    private int awayGamesDraw;
    private int awayGamesLost;

    public TableEntry(Team team) {
        this.team = team;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

//    public int getPoints() {
//        return getTotalGamesWon() * 3 + getTotalGamesDraw();
//    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(final int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(final int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public int getReceivedHomeGoals() {
        return receivedHomeGoals;
    }

    public void setReceivedHomeGoals(final int receivedHomeGoals) {
        this.receivedHomeGoals = receivedHomeGoals;
    }

    public int getReceivedAwayGoals() {
        return receivedAwayGoals;
    }

    public void setReceivedAwayGoals(final int receivedAwayGoals) {
        this.receivedAwayGoals = receivedAwayGoals;
    }

    public int getHomeGamesWon() {
        return homeGamesWon;
    }

    public void setHomeGamesWon(final int homeGamesWon) {
        this.homeGamesWon = homeGamesWon;
    }

    public int getHomeGamesDraw() {
        return homeGamesDraw;
    }

    public void setHomeGamesDraw(final int homeGamesDraw) {
        this.homeGamesDraw = homeGamesDraw;
    }

    public int getHomeGamesLost() {
        return homeGamesLost;
    }

    public void setHomeGamesLost(final int homeGamesLost) {
        this.homeGamesLost = homeGamesLost;
    }

    public int getAwayGamesWon() {
        return awayGamesWon;
    }

    public void setAwayGamesWon(final int awayGamesWon) {
        this.awayGamesWon = awayGamesWon;
    }

    public int getAwayGamesDraw() {
        return awayGamesDraw;
    }

    public String print() {
        StringBuffer buffi = new StringBuffer();
        buffi.append(getTotalGamesWon() + " | ");
        buffi.append(getTotalGamesDraw() + " | ");
        buffi.append(getTotalGamesLost() + "\t");
        buffi.append(getHomeGamesWon() + " | ");
        buffi.append(getHomeGamesDraw() + " | ");
        buffi.append(getHomeGamesLost() + "\t");
        buffi.append(getAwayGamesWon() + " | ");
        buffi.append(getAwayGamesDraw() + " | ");
        buffi.append(getAwayGamesLost() + "\t");
        buffi.append(getTotalGoals() + " : ");
        buffi.append(getTotalReceivedGoals() + "\t\t");
        buffi.append(getPoints() + "\t");
        return buffi.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TableEntry [points=");
        builder.append(getPoints());
        builder.append(", homeGoals=");
        builder.append(homeGoals);
        builder.append(", awayGoals=");
        builder.append(awayGoals);
        builder.append(", receivedHomeGoals=");
        builder.append(receivedHomeGoals);
        builder.append(", receivedAwayGoals=");
        builder.append(receivedAwayGoals);
        builder.append(", homeGamesWon=");
        builder.append(homeGamesWon);
        builder.append(", homeGamesDraw=");
        builder.append(homeGamesDraw);
        builder.append(", homeGamesLost=");
        builder.append(homeGamesLost);
        builder.append(", awayGamesWon=");
        builder.append(awayGamesWon);
        builder.append(", awayGamesDraw=");
        builder.append(awayGamesDraw);
        builder.append(", awayGamesLost=");
        builder.append(awayGamesLost);
        builder.append("]");
        return builder.toString();
    }

    public void setAwayGamesDraw(final int awayGamesDraw) {
        this.awayGamesDraw = awayGamesDraw;
    }

    public int getAwayGamesLost() {
        return awayGamesLost;
    }

    public void setAwayGamesLost(final int awayGamesLost) {
        this.awayGamesLost = awayGamesLost;
    }

    public int getTotalGoals() {
        return homeGoals + awayGoals;
    }

    public int getTotalReceivedGoals() {
        return receivedHomeGoals + receivedAwayGoals;
    }

    public int getTotalGamesWon() {
        return homeGamesWon + awayGamesWon;
    }

    public int getTotalGamesDraw() {
        return homeGamesDraw + awayGamesDraw;
    }

    public int getTotalGamesLost() {
        return homeGamesLost + awayGamesLost;
    }

}
