package de.footballmanager.backend.controller;

import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;
import de.footballmanager.backend.parser.LeagueParser;
import de.footballmanager.backend.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}



