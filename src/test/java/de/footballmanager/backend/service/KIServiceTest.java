package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.club.Club;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.util.Pair;
import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_3_4_3;
import static de.footballmanager.backend.util.TestUtil.createClub;
import static de.footballmanager.backend.util.TestUtil.createMatchDay;
import static de.footballmanager.backend.util.TestUtil.createStartElevenMatchingGivenSystem;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class KIServiceTest {

    private KIService kiService;
    private TeamManagerService teamManagerService;

    @Before
    public void setUp() {
        kiService = new KIService();
        teamManagerService = createMock(TeamManagerService.class);
    }

    @Test
    public void handleSetStartEleven() {
        // given
        MatchDay matchDay = createMatchDay(false);

        Match match1 = matchDay.getMatches().get(0);
        Match match2 = matchDay.getMatches().get(1);

        Map<Position, Player> startEleven1 = createStartElevenMatchingGivenSystem(match1.getHomeTeam(), SYSTEM_3_4_3);
        Map<Position, Player> startEleven2 = createStartElevenMatchingGivenSystem(match1.getGuestTeam(), SYSTEM_3_4_3);
        Map<Position, Player> startEleven3 = createStartElevenMatchingGivenSystem(match2.getHomeTeam(), SYSTEM_3_4_3);
        Map<Position, Player> startEleven4 = createStartElevenMatchingGivenSystem(match2.getGuestTeam(), SYSTEM_3_4_3);

        Team team1 = match1.getHomeTeam();
        Team team2 = match1.getGuestTeam();
        Team team3 = match2.getHomeTeam();
        Team team4 = match2.getGuestTeam();

        ClubService clubService = createMock(ClubService.class);
        expect(clubService.getClub(team1.getName())).andReturn(createClub("club1", true)).once();
        expect(clubService.getClub(team2.getName())).andReturn(createClub("club2", true)).once();
        expect(clubService.getClub(team3.getName())).andReturn(createClub("club3", true)).once();
        expect(clubService.getClub(team4.getName())).andReturn(createClub("club4", true)).once();
        setField(kiService, "clubService", clubService);

        expect(teamManagerService.getBestPlayersForBestSystem(team1)).andReturn(new Pair<>(SYSTEM_3_4_3, startEleven1)).once();
        expect(teamManagerService.getBestPlayersForBestSystem(team2)).andReturn(new Pair<>(SYSTEM_3_4_3, startEleven2)).once();
        expect(teamManagerService.getBestPlayersForBestSystem(team3)).andReturn(new Pair<>(SYSTEM_3_4_3, startEleven3)).once();
        expect(teamManagerService.getBestPlayersForBestSystem(team4)).andReturn(new Pair<>(SYSTEM_3_4_3, startEleven4)).once();
        setField(kiService, "teamManagerService", teamManagerService);
        replay(clubService, teamManagerService);

        // when
        kiService.handleSetStartEleven(matchDay);

        // then
        assertNotNull(matchDay);
        assertNotNull(startEleven1);
        assertEquals(startEleven1, matchDay.getMatches().get(0).getPositionPlayerMapHomeTeam());
        assertNotNull(startEleven2);
        assertEquals(startEleven2, matchDay.getMatches().get(0).getPositionPlayerMapGuestTeam());
        assertNotNull(startEleven3);
        assertEquals(startEleven3, matchDay.getMatches().get(1).getPositionPlayerMapHomeTeam());
        assertNotNull(startEleven4);
        assertEquals(startEleven4, matchDay.getMatches().get(1).getPositionPlayerMapGuestTeam());

        verify(clubService, teamManagerService);
    }
}
