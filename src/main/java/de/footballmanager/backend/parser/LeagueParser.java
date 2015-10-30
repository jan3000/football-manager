package de.footballmanager.backend.parser;

import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.Team;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

@Component
public class LeagueParser {



    public static League parse() throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(League.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (League) unmarshaller.unmarshal(new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("team.xml"))));
    }
}
