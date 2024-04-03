package app.persistance;

import java.util.ArrayList;
import java.util.List;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.Status;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class EventDAO {
    private static EventDAO instance;
    private static EntityManagerFactory emf;

    private EventDAO() {
    }

    public static EventDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new EventDAO();
            emf = _emf;
        }
        return instance;
    }

    public List<Event> getAllEvents() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("From Event e", Event.class).getResultList();
        }       
    }

    public List<Event> getUpcomingEvent() {
        try (EntityManager em = emf.createEntityManager()) {
            var query = em.createQuery("select a from Event a where a.status = :status").setParameter("status", Status.UPCOMING);
            return query.getResultList();
        }
    }
}
