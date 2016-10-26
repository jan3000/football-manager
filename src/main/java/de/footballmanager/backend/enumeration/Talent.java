package de.footballmanager.backend.enumeration;

public enum Talent {

    NO(39), LITTLE(49), LITTLE_MORE(59), NORMAL(69), BRONZE(79), SILVER(89), GOLD(99);


    private int maxStrength;

    Talent(int maxStrength) {
        this.maxStrength = maxStrength;
    }
}
