package de.footballmanager.backend.api;


import de.footballmanager.backend.domain.Player;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.enumeration.Position;

import java.util.List;
import java.util.Map;

public abstract interface FootballManagerFacade {


    // Team screen
    public Team getTeam(String name);


    // Match Day
    public void setStart11(Map<Position, Player> positionToStart11);

    // Statistics


    public void initGame() ;
    
    
    public void calculateMatchDay() ;
    

    public void showTable() ;


    public void showFormerMatchDay();


    public void showTeamDetails() ;


    public void changeTeamDetails();


}
