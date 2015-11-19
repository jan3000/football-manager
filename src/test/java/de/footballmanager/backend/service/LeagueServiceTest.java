package de.footballmanager.backend.service;


import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;
import org.easymock.EasyMock;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;

public class LeagueServiceTest {

    private LeagueService leagueService;

    @Before
    public void setUp() {
        leagueService = new LeagueService();
    }

    @Test
    public void runNextMinute() {

        TimeTable timeTable = new TimeTable();
        MatchDay matchDay = new MatchDay();
        matchDay.getMatches().add(new Match());
        timeTable.addMatchDays(Lists.newArrayList(matchDay));

        ResultService resultService = createMock(ResultService.class);
        resultService.calculateNextMinute(eq((List)Lists.newArrayList(matchDay)));
        expectLastCall().once();

        replay(resultService);

        ReflectionTestUtils.setField(leagueService, "timeTable", timeTable);
        ReflectionTestUtils.setField(leagueService, "resultService", resultService);
        MatchDay returnedMatchDay = leagueService.runNextMinute();
        assertThat(returnedMatchDay).isNotNull();
        assertThat(leagueService.getCurrentMatchDay()).isEqualTo(1);

        verify(resultService);
    }

    @Test
    public void runNextMinuteLastMinute() {
        leagueService.runNextMinute();

        TimeTable timeTable = new TimeTable();
        MatchDay matchDay = new MatchDay();
        Match match = new Match();
        match.setFinished(true);
        matchDay.getMatches().add(match);
        timeTable.getAllMatchDays().add(matchDay);

        ResultService resultService = createMock(ResultService.class);
        resultService.calculateNextMinute(eq((List)Lists.newArrayList(matchDay)));
        expectLastCall().once();


        replay(resultService);


        leagueService.runNextMinute();
        assertThat(leagueService.getCurrentMatchDay()).isEqualTo(2);

        verify(resultService);
    }

}
