package de.footballmanager.backend.domain;

import com.google.common.collect.Lists;
import de.footballmanager.backend.service.DateService;
import de.footballmanager.backend.util.TestUtil;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SeasonTest {

    private DateTime now;

    @Before
    public void setUp() {
        now = new DateTime();

    }

    @Test
    public void assertNameIsSet() {

        List<Team> teams = Lists.newArrayList(TestUtil.createTeam("team", PlayingSystem.SYSTEM_4_3_3));
        TimeTable timeTable = TestUtil.createTimeTable(teams);

        Season season = new Season(now, timeTable, teams);
        assertNotNull(season);
        assertNotNull(season.getName());
        assertEquals("17/18", season.getName());
    }
}
