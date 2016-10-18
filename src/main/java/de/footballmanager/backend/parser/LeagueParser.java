package de.footballmanager.backend.parser;

import de.footballmanager.backend.domain.League;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

@Service
public class LeagueParser {

    public League parse(String teamsFilePath) throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(League.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (League) unmarshaller.unmarshal(new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(teamsFilePath))));
    }
}
