package de.footballmanager.backend.application;

import com.google.common.collect.Sets;
import de.footballmanager.backend.controller.HomeController;
import de.footballmanager.backend.service.LeagueService;
import de.footballmanager.backend.service.TrialAndErrorTimeTableService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("rest")
public class SeedApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Sets.newHashSet();
        resources.add(HomeController.class);
        return resources;
    }

}
