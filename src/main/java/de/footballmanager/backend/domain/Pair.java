package de.footballmanager.backend.domain;

public class Pair<First, Second> {

    private final First first;
    private final Second second;

    public Pair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

}
