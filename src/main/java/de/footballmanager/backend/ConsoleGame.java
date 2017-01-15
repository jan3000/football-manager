package de.footballmanager.backend;

import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Table;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class ConsoleGame {

    public static final String BUNDESLIGA = "Bundesliga";

    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        System.out.println("---------------------------------------------");
        System.out.println("-------- Football Manager 30000 -------------");
        System.out.println("---------------------------------------------");

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        LeagueService leagueService = applicationContext.getBean(LeagueService.class);
        applicationContext.getBean()

        leagueService.createLeagues("team.xml", "names.txt", "surnames.txt");
        List<Team> teams = leagueService.getTeams(BUNDESLIGA);
        System.out.println("\n\nLeague: " + BUNDESLIGA);
        for (Team team : teams) {
            System.out.println("\t " + team.getName());
        }


        MatchDay currentMatchDay = leagueService.getCurrentMatchDay(BUNDESLIGA);
        System.out.println("Match Day: " + currentMatchDay.getDate() + ", " + currentMatchDay.getMatchDayNumber());
        for (Match match : currentMatchDay.getMatches()) {
            System.out.println("\t " + match.printMatch());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Run next match day? [y/n]");
        String goOn = scanner.nextLine();
        if (goOn.equals("Y")) {
            leagueService.startNextMatchDay(BUNDESLIGA);
            IntStream.range(1, 90).forEach(i -> leagueService.runNextMinute(BUNDESLIGA));
            Table table = leagueService.getTable(BUNDESLIGA, 1);
            MatchDay currentMatchDay1 = leagueService.getCurrentMatchDay(BUNDESLIGA);
            currentMatchDay1.print();

        }


    }



    public static void printList(List<Object> items)  {
        for (Object item : items) {
            System.out.println(item.toString());
        }
    }
}
