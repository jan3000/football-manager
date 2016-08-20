package de.footballmanager.backend.parser;

import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.Team;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.footballmanager.backend.parser.PlayerParser.MINIMAL_NUMBER_OF_PLAYERS;
import static de.footballmanager.backend.util.TestUtil.createLeague;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerParserTest {

    @Test
    public void parsePlayerForLeague() {

        // given
        PlayerParser playerParser = new PlayerParser();
        League league = createLeague();
        removeAllPlayersFromTeams(league);
        List<Team> teams = league.getTeams();
        assertFalse(areTeamsHavingMoreThan20Players(teams));

        // when
        playerParser.parsePlayerForLeague(league);

        // then
        assertTrue(areTeamsHavingMoreThan20Players(teams));
        assertTrue(doAllPlayersHaveNames(teams));
        assertTrue(doAllPlayersHaveAPosition(teams));

    }

    private boolean doAllPlayersHaveAPosition(List<Team> teams) {
        return teams.stream()
                .filter(
                        team -> team.getPlayers().stream()
                                .filter(player -> player.getPosition() != null)
                                .collect(Collectors.toList())
                                .isEmpty())
                .collect(Collectors.toList())
                .isEmpty();
    }

    private boolean areTeamsHavingMoreThan20Players(List<Team> teams) {
        return teams.stream()
                .filter(team -> team.getPlayers().size() < MINIMAL_NUMBER_OF_PLAYERS)
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean doAllPlayersHaveNames(List<Team> teams) {
        return teams.stream()
                .filter(team ->
                        CollectionUtils.isNotEmpty(
                                team.getPlayers().stream()
                                        .filter(player -> StringUtils.isEmpty(player.getFullname()))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList()).isEmpty();
    }

    private void removeAllPlayersFromTeams(League league) {
        league.getTeams().forEach(team -> team.setPlayers(Lists.newArrayList()));
    }

}
