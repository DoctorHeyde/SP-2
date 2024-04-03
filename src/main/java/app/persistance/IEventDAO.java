package app.persistance;

import app.entities.Event;

import java.time.LocalDate;

public interface IEventDAO {
    Event createEvent(String title, String startTime, String description, LocalDate dateOfEvent)
}
