package app.controllers;

import app.entities.Event;
import app.entities.User;
import app.persistance.EventDAO;
import app.persistance.UserDAO;
import io.javalin.http.Handler;
import jakarta.persistence.EntityManagerFactory;

public class EventController {
    private EventDAO eventDAO;
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



}
