package de.footballmanager.backend.domain;

import de.footballmanager.backend.enumeration.Position;
import de.footballmanager.backend.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static de.footballmanager.backend.enumeration.Position.LEFT_MIDFIELDER;
import static de.footballmanager.backend.util.TestUtil.createPlayer;
import static de.footballmanager.backend.util.TestUtil.createRunningMatch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatchTest {

    @Test
    public void changePlayer() {

        // given
        Match runningMatch = createRunningMatch();
        Map<Position, Player> positionPlayerMapHomeTeam = runningMatch.getPositionPlayerMapHomeTeam();
        Collection<Player> players = positionPlayerMapHomeTeam.values();

        Player out = players.iterator().next();
        Position positionForChange = out.getPosition();
        Player in = createPlayer("New", "Player", LEFT_MIDFIELDER);
        runningMatch.getHomeTeam().getPlayers().add(in);
        assertTrue(!players.contains(in));

        // when
        runningMatch.changePlayer(in, out, true);

        // then
        assertEquals(11, positionPlayerMapHomeTeam.entrySet().size());
        assertTrue(positionPlayerMapHomeTeam.values().contains(in));
        assertTrue(!positionPlayerMapHomeTeam.values().contains(out));
        assertEquals(in, positionPlayerMapHomeTeam.get(positionForChange));

        assertEquals(1, runningMatch.getPlayerChangesHomeTeam().size());
    }
}
