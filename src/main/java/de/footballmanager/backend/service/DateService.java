package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.Duration;
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

    public void finishDaysUntil(DateTime newDate) {
        Preconditions.checkArgument(newDate.isAfter(today), "new date must be in the future: ", today);
        Duration duration = new Duration(today, newDate);
        Long standardDays = duration.getStandardDays();
        addDays(Math.toIntExact(standardDays));
    }

    public DateTime setDayTime(DateTime date, int hour, int minute) {
        return date.withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0).withMillisOfSecond(0);
    }

    @Override
    public String toString() {
        return "DateService{" +
                "today=" + today +
                '}';
    }
}
