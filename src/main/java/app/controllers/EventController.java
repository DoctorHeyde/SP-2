package app.controllers;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.User;
import app.persistance.EventDAO;
import app.persistance.UserDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class EventController {
    private EventDAO eventDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private UserDAO userDAO;
    
    public EventController(EntityManagerFactory emf) {
        this.eventDAO = EventDAO.getInstance(emf);
        this.userDAO = UserDAO.getUserDAOInstance(emf);
    }

    public Handler addUserToEvent() {
        return ctx -> {
            Event event = eventDAO.getByID(Integer.parseInt(ctx.pathParam("id")));
            User user = userDAO.getByID(ctx.pathParam("email"));
            EventDAO.addUserToEvent(event, user);

        };
    }

    public Handler getAllEvents() {
        return ctx -> {
            String json = objectMapper.writeValueAsString(eventDAO.getAllEvents().stream().map(e -> new EventDTO(e)).collect(Collectors.toList()));
            System.out.println(json);
            ctx.status(HttpStatus.OK).json(json);
        };
    }

    public Handler createEvent(){
        return ctx -> {
            Event event = ctx.bodyAsClass(Event.class);
            eventDAO.create(event);
        };
    }
}
