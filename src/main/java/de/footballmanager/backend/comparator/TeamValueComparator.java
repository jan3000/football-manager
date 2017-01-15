package de.footballmanager.backend.comparator;

import de.footballmanager.backend.domain.league.TableEntry;
import de.footballmanager.backend.domain.club.Team;

import java.util.Comparator;
import java.util.Map;

public class TeamValueComparator implements Comparator<Team> {

    Map<Team, TableEntry> map;

    public TeamValueComparator(final Map<Team, TableEntry> map) {
        super();
        this.map = map;
    }

    public int compare(final Team o1, final Team o2) {
        TableEntry tableEntry2 = map.get(o2);
        TableEntry tableEntry1 = map.get(o1);
        if (tableEntry1.getPoints() < tableEntry2.getPoints()) {
            return 1;

        } else if (tableEntry1.getPoints() > tableEntry2.getPoints()) {
            return -1;
        } else {
            int goalDifferenceTeam1 = tableEntry1.getTotalGoals() - tableEntry1.getTotalReceivedGoals();
            int goalDifferenceTeam2 = tableEntry2.getTotalGoals() - tableEntry2.getTotalReceivedGoals();
            if (goalDifferenceTeam1 < goalDifferenceTeam2) {
                return 1;
            } else if (goalDifferenceTeam1 > goalDifferenceTeam2) {
                return -1;
            } else {
                if (tableEntry2.getTotalGoals() > tableEntry1.getTotalGoals()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

}
