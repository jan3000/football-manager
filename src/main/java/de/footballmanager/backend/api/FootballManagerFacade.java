package de.footballmanager.backend.api;


import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;

import java.util.Map;

public abstract interface FootballManagerFacade {


    // Team screen
    public Team getTeam(String name);


    // Match Day
    public void setStartElevenHome(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven);

    public void setStartElevenGuest(int matchDayNumber, String teamName, Map<Position, Player> positionToStartEleven);

    // Statistics


    public void initGame();


    public void calculateMatchDay();


    public void showTable();


    public void showFormerMatchDay();


    public void showTeamDetails();


    public void changeTeamDetails();


}
