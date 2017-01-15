package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.footballmanager.backend.domain.club.Club;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Trainer;
import de.footballmanager.backend.domain.util.PersonEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClubService {

    @Autowired
    private DateService dateService;

    private Map<String, Club> clubNameToClub = Maps.newHashMap();

    public void registerClub(Club club) {
        clubNameToClub.put(club.getName(), club);
    }

    public Club getClub(String clubName) {
        Club club = clubNameToClub.get(clubName);
        Preconditions.checkNotNull(club, "no club found with name: ", clubName);
        return club;
    }

    public Team getTeam(String clubName) {
        return getClub(clubName).getTeam();
    }

    public Trainer getTrainer(String clubName) {
        return getClub(clubName).getTrainer();
    }

    public void setTrainer(String clubName, Trainer trainer) {
        Trainer currentTrainer = getTrainer(clubName);
        Club club = getClub(clubName);

        DateTime today = dateService.getToday();
        if (currentTrainer != null) {
            List<PersonEvent<Trainer>> trainers = club.getClubHistory().getTrainers();
            trainers.get(trainers.size() - 1).setEndDate(today);
        }

        PersonEvent<Trainer> trainerPersonEvent = new PersonEvent<>(today, null, trainer);
        club.getClubHistory().addTrainer(trainerPersonEvent);
        club.setTrainer(trainer);
    }

    public Manager getManager(String clubName) {
        return getClub(clubName).getManager();
    }

    public void setManager(String clubName, Manager manager) {
        Manager currentManager = getManager(clubName);
        Club club = getClub(clubName);

        DateTime today = dateService.getToday();
        if (currentManager != null) {
            List<PersonEvent<Manager>> managers = club.getClubHistory().getManagers();
            managers.get(managers.size() - 1).setEndDate(today);
        }

        PersonEvent<Manager> managerPersonEvent = new PersonEvent<>(today, null, manager);
        club.getClubHistory().addManager(managerPersonEvent);
        club.setManager(manager);
    }


}
