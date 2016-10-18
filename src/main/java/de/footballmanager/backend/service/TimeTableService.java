package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;

import java.util.List;

public interface TimeTableService {
    TimeTable createTimeTable(List<Team> teams);
}
