package de.footballmanager.backend;

import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.Table;
import de.footballmanager.backend.service.InitializationService;
import de.footballmanager.backend.service.KIService;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.util.PrintUtil;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        InitializationService initializationService = applicationContext.getBean(InitializationService.class);
        LeagueService leagueService = applicationContext.getBean(LeagueService.class);
        KIService kiService = applicationContext.getBean(KIService.class);

        initializationService.createLeagues("club.xml", "names.txt", "surnames.txt");
        List<Team> teams = leagueService.getTeams(BUNDESLIGA);
        System.out.println("\n\nLeague: " + BUNDESLIGA);
        for (Team team : teams) {
            System.out.println("\t " + team.getName());
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Run next match day? [y/n]");
            String goOn = scanner.nextLine();
            if (goOn.equals("y")) {
                kiService.handleNextMatchDay(BUNDESLIGA);
                int currentMatchDayNumber = leagueService.getCurrentMatchDayNumber(BUNDESLIGA);
                System.out.println("Table of matchday: " + currentMatchDayNumber);
                MatchDay currentMatchDay1 = leagueService.getMatchDay(BUNDESLIGA, currentMatchDayNumber - 1);
                System.out.println(PrintUtil.printCompact(currentMatchDay1));
                System.out.println(PrintUtil.print(leagueService.getTable(BUNDESLIGA, currentMatchDayNumber -1 )));
                System.out.println("---------------------------------------------------");
            }
        }


    }



    public static void printList(List<Object> items)  {
        for (Object item : items) {
            System.out.println(item.toString());
        }
    }
}
