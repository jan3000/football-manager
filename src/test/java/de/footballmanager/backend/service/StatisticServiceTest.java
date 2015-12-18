package de.footballmanager.backend.service;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;
import de.footballmanager.backend.util.TestUtil;
import org.junit.Test;

import java.util.List;

import static de.footballmanager.backend.util.TestUtil.TEAM_1;
import static de.footballmanager.backend.util.TestUtil.TEAM_2;
import static de.footballmanager.backend.util.TestUtil.TEAM_3;
import static de.footballmanager.backend.util.TestUtil.TEAM_4;
import static de.footballmanager.backend.util.TestUtil.buildMatch;

public class StatisticServiceTest {



    public void setUp() {
        TimeTable timeTable = new TimeTable();
        MatchDay matchDay1 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_2, TEAM_3, 3, 2)));
        MatchDay matchDay2 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_3, TEAM_1, 3, 2)));
        MatchDay matchDay3 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_1, TEAM_2, 2, 2)));
        MatchDay matchDay4 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_3, TEAM_2, 0, 2)));
        MatchDay matchDay5 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_1, TEAM_3, 1, 2)));
        MatchDay matchDay6 = new MatchDay(Lists.newArrayList(buildMatch(TEAM_2, TEAM_1, 4, 1)));
        timeTable.addMatchDays(Lists.newArrayList(matchDay1, matchDay2, matchDay3, matchDay4, matchDay5, matchDay6));
    }

    @Test
    public void getGoalDistribution() {
        StatisticService statisticService = new StatisticService();

        TimeTable timeTable = new TimeTable();

        MatchDay matchDay1 = new MatchDay();
        matchDay1.addMatch(new Match());
        List<MatchDay> matchDays = Lists.newArrayList();

        timeTable.addMatchDays(matchDays);
        statisticService.getGoalDistribution(timeTable, new Team(TEAM_1));
    }

}
