package de.footballmanager.backend.domain.util.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "league")
public class LeagueInitializer {

    private String name;
    private List<ClubInitializer> clubInitializerList;

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "clubs")
    @XmlElement(name = "club")
    public List<ClubInitializer> getClubInitializerList() {
        return clubInitializerList;
    }

    public void setClubInitializerList(List<ClubInitializer> clubInitializerList) {
        this.clubInitializerList = clubInitializerList;
    }
}
