package de.footballmanager.backend.parser;

import de.footballmanager.backend.domain.League;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

import static org.fest.assertions.Assertions.assertThat;

public class LeagueParserTest {

    @Test
    public void parse() throws JAXBException, FileNotFoundException {
        LeagueParser leagueParser = new LeagueParser();
        League league = leagueParser.parse("team.xml");
        assertThat(league).isNotNull();
        assertThat(league.getTeams().size()).isEqualTo(18);
    }
}
