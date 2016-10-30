package de.footballmanager.backend.parser;

import de.footballmanager.backend.domain.util.xml.ClubInitializer;
import de.footballmanager.backend.domain.util.xml.LeagueInitializer;
import de.footballmanager.backend.domain.util.xml.LeaguesWrapper;
import de.footballmanager.backend.domain.util.xml.StadiumInitializer;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class LeagueParserTest {

    @Test
    public void parse() throws JAXBException, FileNotFoundException {
        LeagueParser leagueParser = new LeagueParser();
        LeaguesWrapper leaguesWrapper = leagueParser.parse("club.xml");
        assertThat(leaguesWrapper).isNotNull();
        assertThat(leaguesWrapper.getLeagues().size()).isEqualTo(2);
        LeagueInitializer leagueInitializer = leaguesWrapper.getLeagues().get(0);
        assertThat(leagueInitializer.getName()).isEqualTo("Bundesliga");
        assertThat(leagueInitializer.getNumberOfPromotions()).isEqualTo(0);
        List<ClubInitializer> clubInitializerList = leagueInitializer.getClubInitializerList();
        assertThat(clubInitializerList.size()).isEqualTo(2);

        LeagueInitializer leagueInitializer2 = leaguesWrapper.getLeagues().get(1);
        assertThat(leagueInitializer2.getName()).isEqualTo("2. Bundesliga");
        List<ClubInitializer> clubInitializerList2 = leagueInitializer2.getClubInitializerList();
        assertThat(clubInitializerList2.size()).isEqualTo(2);

        ClubInitializer clubInitializer1 = clubInitializerList.get(0);
        assertThat(clubInitializer1).isNotNull();
        assertThat(clubInitializer1.getName()).isEqualTo("Hamburger SV");
        assertThat(clubInitializer1.getStrength()).isEqualTo(78);
        StadiumInitializer stadiumInitializer1 = clubInitializer1.getStadiumInitializer();
        assertThat(stadiumInitializer1.getStadiumName()).isEqualTo("Volksparkstadion");
        assertThat(stadiumInitializer1.getStance()).isNotNull();
        assertThat(stadiumInitializer1.getRoofedStance()).isNotNull();
        assertThat(stadiumInitializer1.getSeats()).isNotNull();
        assertThat(stadiumInitializer1.getRoofedSeats()).isNotNull();

        ClubInitializer clubInitializer2 = clubInitializerList.get(1);
        assertThat(clubInitializer2).isNotNull();
        assertThat(clubInitializer2.getName()).isEqualTo("Borussia Dortmund 09");
        assertThat(clubInitializer2.getStrength()).isEqualTo(89);
    }
}
