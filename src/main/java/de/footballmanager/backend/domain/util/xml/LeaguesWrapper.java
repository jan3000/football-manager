package de.footballmanager.backend.domain.util.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "leaguesWrapper")
public class LeaguesWrapper {

    private List<LeagueInitializer> leagues;

    @XmlElementWrapper(name = "leagues")
    @XmlElement(name = "league")
    public List<LeagueInitializer> getLeagues() {
        return leagues;
    }

    public void setLeagues(List<LeagueInitializer> leagues) {
        this.leagues = leagues;
    }
}
