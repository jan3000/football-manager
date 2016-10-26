package de.footballmanager.backend.parser;

import com.google.common.base.Preconditions;
import de.footballmanager.backend.domain.util.xml.LeaguesWrapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class LeagueParser {

    public LeaguesWrapper parse(String teamsFilePath) throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(LeaguesWrapper.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                teamsFilePath);
        Preconditions.checkNotNull(resourceAsStream, "no file found: ", teamsFilePath);
        return (LeaguesWrapper) unmarshaller.unmarshal(new BufferedReader(new InputStreamReader(
                resourceAsStream)));
    }
}
