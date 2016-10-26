package de.footballmanager.backend.domain.club;


import de.footballmanager.backend.domain.finance.Finance;
import de.footballmanager.backend.domain.infrastructure.Stadium;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Trainer;
import de.footballmanager.backend.domain.statistics.ClubHistory;

public class Club {

    private final String name;
    private Finance finance;
    private Stadium stadium;

    private Manager manager;
    private Trainer trainer;
    private Team team;

    private ClubHistory clubHistory = new ClubHistory();

    public Club(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Finance getFinance() {
        return finance;
    }

    public void setFinance(Finance finance) {
        this.finance = finance;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    public Manager getManager() {
        return manager;
    }

    /**
     * Do not use directly, use ClubService.setManager()
     */
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public ClubHistory getClubHistory() {
        return clubHistory;
    }

    public void setClubHistory(ClubHistory clubHistory) {
        this.clubHistory = clubHistory;
    }
}
