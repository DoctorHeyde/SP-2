package app.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.EventDTO;
import app.dtos.UserDTO;
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

    public Handler getEventById(){
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            EventDTO eventDTO = new EventDTO(eventDAO.getEventById(id));
            String json = objectMapper.writeValueAsString(eventDTO);
            ctx.status(HttpStatus.OK).json(json);
        };
    }

    public Handler cancelRegistration() {
        return ctx -> {
            String jsonBody = ctx.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonBody);
            String email = jsonNode.get("email").asText();
            Integer eventID = jsonNode.get("id").asInt();
            User user = userDAO.getByID(email);
            Event eventObj = eventDAO.getByID(eventID);
            EventDAO.cancelRegistration(eventObj, user);
        };
    }
    public Handler getUpcomingEvents() {
        return ctx -> {
            List<Event> upComing = eventDAO.getUpcomingEvent();

            List<EventDTO> upComingAsDTO = upComing.stream()
                    .map(event -> new EventDTO(event.getTitle(), event.getDateOfEvent().toString())).collect(Collectors.toList());

            ctx.status(200).json(upComingAsDTO);
        };
    }

    public Handler getRegistrationsToEvent() {
        return ctx -> {
            int eventId = Integer.parseInt(ctx.pathParam("id"));
            Event event = eventDAO.getEventById(eventId);
            String json = objectMapper.writeValueAsString(event.getUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toSet()));
            ctx.status(HttpStatus.OK).json(json);
        };
    }

}

