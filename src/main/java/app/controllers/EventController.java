package app.controllers;

import java.util.stream.Collectors;

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

    public Handler getEventById(){
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            EventDTO eventDTO = new EventDTO(eventDAO.getEventById(id));
            String json = objectMapper.writeValueAsString(eventDTO);
            ctx.status(HttpStatus.OK).json(json);
        };
    }
}

