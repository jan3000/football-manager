package de.footballmanager.backend.service;

import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.util.Pair;
import de.footballmanager.backend.enumeration.Position;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_3_4_3;
import static de.footballmanager.backend.enumeration.PlayingSystem.SYSTEM_4_4_2;
import static de.footballmanager.backend.util.TestUtil.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class KIServiceTest {

    private KIService kiService;
    private TeamManagerService teamManagerService;
    private static Team team1;
    private static Team team2;
    private static Team team3;
    private static Team team4;

    @Before
    public void setUp() {
        kiService = new KIService();
        teamManagerService = createMock(TeamManagerService.class);
    }

    private static MatchDay createMatchDay(boolean setStartEleven) {
        MatchDay matchDay = new MatchDay();
        team1 = createTeam(TEAM_NAME_1, SYSTEM_4_4_2, setStartEleven);
        team2 = createTeam(TEAM_NAME_2, SYSTEM_4_4_2, setStartEleven);
        team3 = createTeam(TEAM_NAME_3, SYSTEM_4_4_2, setStartEleven);
        team4 = createTeam(TEAM_NAME_4, SYSTEM_4_4_2, setStartEleven);
        Match match1 = createMatch(team1, team2);
        matchDay.addMatch(match1);
        Match match2 = createMatch(team3, team4);
        matchDay.addMatch(match2);
        matchDay.setDate(new DateTime());
        matchDay.setMatchDayNumber(1);
        return matchDay;
    }


    @Test
    public void handleSetStartEleven() {
        // given
        MatchDay matchDay = createMatchDay(false);

        Match match1 = matchDay.getMatches().get(0);
        Match match2 = matchDay.getMatches().get(1);

        Map<Position, Player> startEleven1 = createStartElevenMatchingGivenSystem(team1, SYSTEM_3_4_3);
        Map<Position, Player> startEleven2 = createStartElevenMatchingGivenSystem(team2, SYSTEM_3_4_3);
        Map<Position, Player> startEleven3 = createStartElevenMatchingGivenSystem(team3, SYSTEM_3_4_3);
        Map<Position, Player> startEleven4 = createStartElevenMatchingGivenSystem(team4, SYSTEM_3_4_3);

        ClubService clubService = createMock(ClubService.class);
        expect(clubService.getTeam(team1.getName())).andReturn(team1).once();
        expect(clubService.getTeam(team2.getName())).andReturn(team2).once();
        expect(clubService.getTeam(team3.getName())).andReturn(team3).once();
        expect(clubService.getTeam(team4.getName())).andReturn(team4).once();
        expect(clubService.getClub(team1.getName())).andReturn(createClub("club1", true)).once();
        expect(clubService.getClub(team2.getName())).andReturn(createClub("club2", true)).once();
        expect(clubService.getClub(team3.getName())).andReturn(createClub("club3", true)).once();
        expect(clubService.getClub(team4.getName())).andReturn(createClub("club4", true)).once();
        setField(kiService, "clubService", clubService);

        MatchService matchService = createMock(MatchService.class);
        expect(matchService.getHomeTeam(match1)).andReturn(team1.getName());
        expect(matchService.getGuestTeam(match1)).andReturn(match1.getGuestTeam());
        expect(matchService.getHomeTeam(match2)).andReturn(match2.getHomeTeam());
        expect(matchService.getGuestTeam(match2)).andReturn(match2.getGuestTeam());
        setField(kiService, "matchService", matchService);

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
