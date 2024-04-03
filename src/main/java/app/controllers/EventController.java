package app.controllers;

import java.util.List;
import java.util.stream.Collectors;

import app.entities.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.EventDTO;
import app.persistance.EventDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class EventController {
    private EventDAO eventDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    public EventController(EntityManagerFactory emf) {
        eventDAO = EventDAO.getInstance(emf);
    }

    public Handler getAllEvents() {
        return ctx -> {
            String json = objectMapper.writeValueAsString(eventDAO.getAllEvents().stream().map(e -> new EventDTO(e)).collect(Collectors.toList()));
            System.out.println(json);
            ctx.status(HttpStatus.OK).json(json);
        };
    }

    public Handler getUpcomingEvent() {
        return ctx -> {
            List<EventDTO> upComing = eventDAO.getUpcomingEvent();
            ctx.status(200).json(upComing);
        };
    }
}
