package de.footballmanager.backend.parser;

import de.footballmanager.backend.domain.Team;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TeamParser {

    public static void parse() throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Team.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Team team = (Team) unmarshaller.unmarshal(new FileReader("team.xml"));
        System.out.println("333333333333333 " + team.getName());
    }
}
