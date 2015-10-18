package de.footballmanager.backend.controller;

import de.footballmanager.backend.parser.TeamParser;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

@Path("/home")
@RequestScoped
public class HomeController {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get() throws JAXBException, FileNotFoundException {

        TeamParser.parse();
        return "helliHello!";
    }
}



