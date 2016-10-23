package de.footballmanager.backend.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DateTimeService {

    private DateService dateService;
    private DateTime initialToday;

    @Before
    public void setUp() {
        dateService = new DateService();
        initialToday = dateService.getToday();
    }

    @Test
    public void todayIsSet() {
        assertNotNull(initialToday);
        assertEquals(1, initialToday.getDayOfMonth());
        assertEquals(7, initialToday.getMonthOfYear());
        assertEquals(2015, initialToday.getYear());
    }


    @Test
    public void todayIsImmutable() {
        initialToday = initialToday.plusDays(2);
        assertEquals(initialToday.minusDays(2), dateService.getToday());
    }


    @Test
    public void addSomeDays() {
        assertNotNull(initialToday);
        dateService.addDays(4);
        assertEquals(initialToday.plusDays(4), dateService.getToday());
    }

    @Test
    public void addOneWeek() {
        assertNotNull(initialToday);
        dateService.addOneWeek();
        assertEquals(initialToday.plusDays(7), dateService.getToday());
    }

}
