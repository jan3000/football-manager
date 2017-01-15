package de.footballmanager.backend.domain.util;

import de.footballmanager.backend.domain.persons.Person;
import org.joda.time.DateTime;

public class PersonEvent<P extends Person> extends Event {

    private P person;

    public PersonEvent(DateTime startDate, DateTime endDate, P person) {
        super.setStartDate(startDate);
        super.setEndDate(endDate);
        this.person = person;
    }
}
