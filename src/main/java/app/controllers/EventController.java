package app.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.EventDTO;
import app.dtos.UserDTO;
import app.entities.Event;
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
            UserDTO user = ctx.attribute("user");
            List<Event> events = eventDAO.getAllEvents();
            if(user.hasRole("ADMIN")){
                String json = objectMapper.writeValueAsString(events.stream().map(e -> new EventDTO(e)).collect(Collectors.toList()));
                System.out.println(json);
                ctx.status(HttpStatus.OK).json(json);
            }
            if(user.hasRole("INSTRUCTOR")){
                List<EventDTO> eventDTOs = events.stream().filter(e -> e.getInstructor().equalsIgnoreCase(user.getName())).map(e -> new EventDTO(e)).collect(Collectors.toList());
                String json = objectMapper.writeValueAsString(eventDTOs);
                System.out.println(json);
                ctx.status(HttpStatus.OK).json(json);
            }
        };
    }

    
}
