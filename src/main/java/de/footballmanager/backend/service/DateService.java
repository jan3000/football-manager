package de.footballmanager.backend.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class DateService {

    private DateTime today = new DateTime("2015-07-01");

    public DateTime getToday() {
        return new DateTime(today);
    }

    public void addDays(int numberOfDays) {
        today = today.plusDays(numberOfDays);
    }

    public void addOneWeek() {
        today = today.plusWeeks(1);
    }

    @Override
    public String toString() {
        return "DateService{" +
                "today=" + today +
                '}';
    }
}
