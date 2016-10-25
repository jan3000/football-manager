package de.footballmanager.backend.parser;

import de.footballmanager.backend.domain.LeaguesWrapper;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

import static org.fest.assertions.Assertions.assertThat;

public class LeagueParserTest {

    @Test
    public void parse() throws JAXBException, FileNotFoundException {
        LeagueParser leagueParser = new LeagueParser();
        LeaguesWrapper leaguesWrapper = leagueParser.parse("team.xml");
        assertThat(leaguesWrapper).isNotNull();
        assertThat(leaguesWrapper.getLeagues().size()).isEqualTo(1);
        assertThat(leaguesWrapper.getLeagues().get(0).getTeams().size()).isEqualTo(18);
    }
}
