package app.controllers;

import app.persistance.EventDAO;

public class EventController {
    EventDAO eventDAO;
    public static EventController instance;
    private EventController() {
    }

    public static EventController getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new EventController();
            instance.eventDAO = EventDAO.getInstance(emf);
        }
        return instance;
    }

}
