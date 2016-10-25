package de.footballmanager.backend.service;

import com.google.common.collect.ImmutableList;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;

import java.util.List;

public abstract class TimeTableService {


    public abstract TimeTable createTimeTable(List<Team> teams);

    public boolean isTimeTableFinished(TimeTable timeTable) {
        ImmutableList<MatchDay> allMatchDays = timeTable.getAllMatchDays();
        return timeTable.getCurrentMatchDay() == allMatchDays.size()
                && allMatchDays.get(allMatchDays.size() -1).isFinished();
    }
}
