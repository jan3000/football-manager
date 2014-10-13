package de.footballmanager.backend;

import java.util.List;

import com.google.common.collect.Lists;

import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.Team;

public class LeagueTestUtil {

    public static Team createTeam(final String name) {
        Team team = new Team(name);
        return team;
    }

    public static Match createMatch(final String team1, final String team2) {
        Match match = new Match();
        match.setHomeTeam(new Team(team1));
        match.setGuestTeam(new Team(team2));
        return match;
    }

    public static List<Team> getLeagueTeams() {
        List<Team> teams = Lists.newArrayList();
        teams.add(new Team("Hamburger SV      ", 79));
        teams.add(new Team("Borussia Dortmund 09", 89));
        teams.add(new Team("Bayern Muenchen    ", 94));
        teams.add(new Team("Werder Bremen     ", 63));
        teams.add(new Team("Eintracht Frankfurt", 70));
        teams.add(new Team("1.FC Kueln        ", 45));
        teams.add(new Team("1. FC K'lautern  ", 42));
        teams.add(new Team("VfB Stuttgart    ", 57));
        teams.add(new Team("Bor. M'gladbach  ", 57));
        teams.add(new Team("SC Freiburg      ", 57));
        teams.add(new Team("FC Schalke 04    ", 77));
        teams.add(new Team("Bayer Leverkusen ", 77));
        teams.add(new Team("1 FC Nuernberg    ", 37));
        teams.add(new Team("Hertha BSC       ", 37));
        teams.add(new Team("FC Augsburg      ", 17));
        teams.add(new Team("For. Duesseldorf  ", 7));
        teams.add(new Team("Hannover 96      ", 67));
        teams.add(new Team("1899 Hoffeheim   ", 47));
        return teams;
    }
}
