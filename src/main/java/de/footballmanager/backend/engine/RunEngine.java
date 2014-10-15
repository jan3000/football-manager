package de.footballmanager.backend.engine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import de.footballmanager.backend.domain.League;
import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.MatchDay;
import de.footballmanager.backend.domain.Team;

public class RunEngine {

    public static void main(final String[] args) {
        System.out.println(StringUtils.repeat("-", 100));
        System.out.println("Football-Manager 2013");
        System.out.println(StringUtils.repeat("-", 100));

        List<Team> teams = Lists.newArrayList();
        teams.add(new Team("Hamburger SV      ", 99));
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
        League league = new League(teams);

        for (MatchDay matchDay : league.getTimeTable().getAllMatchDays()) {
            for (Match match : matchDay.getMatches()) {
                ResultEngine.calculateResult(match);
            }

        }
        System.out.println(league.getTimeTable().print());
        System.out.println(league.printCurrentTable());
    }
}
