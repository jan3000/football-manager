package de.footballmanager.backend.domain.util;

import org.joda.time.DateTime;

public abstract class Event {

    private DateTime startDate;
    private DateTime endDate;

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }
}
