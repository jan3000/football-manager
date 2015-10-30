package de.footballmanager.backend.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class RunEngine {

    public static void main(final String[] args) throws JAXBException, FileNotFoundException {
        System.out.println(StringUtils.repeat("-", 100));
        System.out.println("Football-Manager 2013");
        System.out.println(StringUtils.repeat("-", 100));

        JAXBContext jaxbContext = JAXBContext.newInstance(League.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        League league = (League) unmarshaller.unmarshal(new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("team.xml"))));

        for (Team team : league.getTeams()) {
            System.out.println(team);
        }

//        for (MatchDay matchDay : league.getTimeTable().getAllMatchDays()) {
//            for (Match match : matchDay.getMatches()) {
//                ResultEngine.calculateResult(match);
//            }
//
//        }
//        System.out.println(league.getTimeTable().print());
//        System.out.println(league.printCurrentTable());
    }
}
