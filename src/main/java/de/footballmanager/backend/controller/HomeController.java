package de.footballmanager.backend.controller;

import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;
import de.footballmanager.backend.parser.LeagueParser;
import de.footballmanager.backend.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

@Path("/home")
@RequestScoped
public class HomeController {

    @Autowired
    private LeagueService leagueService;

    private LeagueService getLeagueService() {
        if (leagueService == null) {
            leagueService = new LeagueService();
            leagueService.initLeague();
        }

        return leagueService;
    }

    @GET
    @Path("teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Team> getTeams() throws JAXBException, FileNotFoundException {
        return getLeagueService().getTeams();
    }


    @GET
    @Path("timeTable")
    @Produces(MediaType.APPLICATION_JSON)
    public TimeTable getTimeTable() throws JAXBException, FileNotFoundException {
        return getLeagueService().getTimeTable();
    }
}



