package de.footballmanager.backend.comparator;

import java.util.Comparator;
import java.util.Map;

import de.footballmanager.backend.domain.Team;

public class TeamValueComparator implements Comparator<Team> {

    Map<Team, Integer> map;

    public TeamValueComparator(final Map<Team, Integer> map) {
        super();
        this.map = map;
    }

    public int compare(final Team o1, final Team o2) {
        if (map.get(o1) < map.get(o2)) {
            return 1;

        } else {
            return -1;
        }
    }

}
