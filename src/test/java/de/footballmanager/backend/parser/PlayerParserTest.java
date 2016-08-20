package de.footballmanager.backend.parser;

import org.junit.Test;

public class PlayerParserTest {

    @Test
    public void test() {
        PlayerParser playerParser = new PlayerParser();
        playerParser.parsePlayerForLeague();
    }

}
