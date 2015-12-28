package de.footballmanager.backend.controller;

import de.footballmanager.backend.domain.*;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

@Path("/home")
@RequestScoped //  bean lives as long as a single HTTP request-response cycle
public class HomeController {

    @Autowired
    private LeagueService leagueService;
    @Autowired
    private StatisticService statisticService;

    @GET
    @Path("teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Team> getTeams() throws JAXBException, FileNotFoundException {
        System.out.println("000 getTeams");
        return leagueService.getTeams();
    }


    @GET
    @Path("timeTable/{matchDay}")
    @Produces(MediaType.APPLICATION_JSON)
    public MatchDay getTimeTableForMatchDay(@PathParam("matchDay") int matchDay) {
        System.out.println("000 getTimeTableForMatchDay: " + matchDay);
        return leagueService.getTimeTableForMatchDay(matchDay);
    }

    @GET
    @Path("runNextMatchDay")
    @Produces(MediaType.APPLICATION_JSON)
    public MatchDay runNextMatchDay() {
        return leagueService.runNextMatchDay();
    }


    @GET
    @Path("runNextMatchDayMinute")
    @Produces(MediaType.APPLICATION_JSON)
    public MatchDay runNextMatchDayMinute() {
        return leagueService.runNextMinute();
    }

    @GET
    @Path("currentTable")
    @Produces(MediaType.APPLICATION_JSON)
    public Table getCurrentTable() {
        return leagueService.getCurrentTable();
    }

    @GET
    @Path("table/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public Table getTable(@PathParam("day") int day) {
        return leagueService.getTable(day);
    }

    @GET
    @Path("statistic/{teamName}")
    @Produces(MediaType.APPLICATION_JSON)
    public TeamStatistic getTeamStatistic(@PathParam("teamName") String teamName) {
        return statisticService.getTeamStatistics(leagueService.getTimeTable(), teamName);
    }
}



