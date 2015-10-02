package de.footballmanager.backend.controller;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/home")
@RequestScoped
public class HomeController {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get() {
        return "helliHello!";
    }
}



