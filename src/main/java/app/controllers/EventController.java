package app.controllers;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
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
            String jsonBody = ctx.body();
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonBody);
            
            String email = jsonNode.get("email").asText();
            Integer eventID = jsonNode.get("id").asInt();
            User user = userDAO.getByID(email);
            Event eventObj = eventDAO.getByID(eventID);
            EventDAO.addUserToEvent(eventObj, user);
        };
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

