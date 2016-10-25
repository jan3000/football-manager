package de.footballmanager.backend.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "leaguesWrapper")
@XmlType(propOrder = {"leagues"})
public class LeaguesWrapper {

    private List<League> leagues;

    @XmlElementWrapper(name = "leagues")
    @XmlElement(name = "league")
    public List<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(List<League> leagues) {
        this.leagues = leagues;
    }
}
