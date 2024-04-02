package app.controllers;

import app.persistance.EventDAO;
import jakarta.persistence.EntityManagerFactory;

public class EventController {
    private EntityManagerFactory emf;
    
    public EventController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    

}
