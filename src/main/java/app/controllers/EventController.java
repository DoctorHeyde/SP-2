package app.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.EventDTO;
import app.dtos.UserDTO;
import app.entities.Event;
import app.entities.Status;
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
            User user = userDAO.getById(email);
            Event eventObj = eventDAO.getById(eventID);
            EventDAO.addUserToEvent(eventObj, user);

            // TODO: missing response, a statuscode or something
        };
    }

    public Handler getAllEvents() {
        return ctx -> {

            UserDTO user = ctx.attribute("user");
            List<Event> events = eventDAO.getAll();
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
            EventDTO eventDTO = new EventDTO(eventDAO.getById(id));
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
            User user = userDAO.getById(email);
            Event eventObj = eventDAO.getById(eventID);
            EventDAO.cancelRegistration(eventObj, user);

            // TODO: missing response, a statuscode or something
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

    public Handler getEventByCategory() {
        return ctx -> {
            String category = ctx.pathParam("category");
            List<Event> events = eventDAO.getEventByCategory(category);

            String json = objectMapper.writeValueAsString(events.stream().map(e -> new EventDTO(e)).collect(Collectors.toList()));
            ctx.status(HttpStatus.OK).json(json);
        };
    }
    
    public Handler getEventByStatus() {
        return ctx -> {
            Status status = Status.valueOf(ctx.pathParam("status").toUpperCase());
            List<Event> events = eventDAO.getEventByStatus(status);
        
            String json = objectMapper.writeValueAsString(events.stream().map(e -> new EventDTO(e)).collect(Collectors.toList()));
            ctx.status(HttpStatus.OK).json(json);
        };
    }
    public Handler getRegistrationsToEvent() {
        return ctx -> {
            int eventId = Integer.parseInt(ctx.pathParam("id"));
            Event event = eventDAO.getById(eventId);
            String json = objectMapper.writeValueAsString(event.getUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toSet()));
            ctx.status(HttpStatus.OK).json(json);
        };
    }

    public Handler cancelEvent() {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Event event = eventDAO.getById(id);
            event.setStatus(Status.CANCELED);
            eventDAO.update(event);
            ctx.status(200);
        };
    }

    public Handler updateEvent() {

        return ctx ->{

            int id = Integer.parseInt(ctx.pathParam("id"));

            EventDTO updatedEventAsDTO = ctx.bodyAsClass(EventDTO.class);

            Event updatedEvent = new Event(updatedEventAsDTO);
            updatedEvent.setId(id);
            Event updatedEventNowInDB = eventDAO.update(updatedEvent);

            EventDTO updatedEventInDBAsDTO = new EventDTO(updatedEventNowInDB);
            String json = objectMapper.writeValueAsString(updatedEventInDBAsDTO);

            ctx.status(201).json("Event after update: " + json);

        };
    }

    public Handler getSingleRegistrationById() {
        return ctx -> {
            var tmp = ctx.pathParamMap();
            System.out.println(tmp);
            String userId = ctx.pathParam("userid");
            int evetnId = Integer.parseInt(ctx.pathParam("eventid"));
            
            User user = userDAO.getById(userId);
            Event event = eventDAO.getById(evetnId);

            if (user != null && event != null) {
                ctx.status(HttpStatus.FOUND).json(
                    objectMapper.createObjectNode().put("msg", "Registration found")
                );
                
                return;
            }
            ctx.status(HttpStatus.NOT_FOUND).json(
                objectMapper.createObjectNode().put("msg", "Registration not found")
            );

        };
    }
}

