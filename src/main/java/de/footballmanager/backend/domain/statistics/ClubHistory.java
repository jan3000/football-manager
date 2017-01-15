package de.footballmanager.backend.domain.statistics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Trainer;
import de.footballmanager.backend.domain.util.PersonEvent;

import java.util.List;

public class ClubHistory {

    private List<PersonEvent<Manager>> managers = Lists.newArrayList();
    private List<PersonEvent<Trainer>> trainers = Lists.newArrayList();

    public List<PersonEvent<Manager>> getManagers() {
        return ImmutableList.copyOf(managers);
    }

    public void addManager(PersonEvent<Manager> formerManager) {
        this.managers.add(formerManager);
    }

    public List<PersonEvent<Trainer>> getTrainers() {
        return ImmutableList.copyOf(trainers);
    }

    public void addTrainer(PersonEvent<Trainer> trainer) {
        this.trainers.add(trainer);
    }
}
