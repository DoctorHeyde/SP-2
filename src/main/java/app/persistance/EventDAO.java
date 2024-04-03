package app.persistance;

import java.util.List;

import app.entities.Event;
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
}
