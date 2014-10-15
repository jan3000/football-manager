package de.footballmanager.backend.comparator;

import java.util.Comparator;
import java.util.Map;

import de.footballmanager.backend.domain.Result;

public class ResultComparator implements Comparator<Result> {

    private final Map<Result, Integer> map;

    public ResultComparator(final Map<Result, Integer> map) {
        super();
        this.map = map;
    }

    public int compare(final Result result1, final Result result2) {
        if (map.get(result1) > map.get(result2)) {
            return 1;
        } else {
            return -1;
        }
    }
}
