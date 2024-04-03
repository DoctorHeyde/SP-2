package app.persistance;

import java.util.List;
import java.util.stream.Collectors;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

public class EventDAO extends ADAO<Event, EventDTO, Integer> {
    private static EntityManagerFactory emf;

    private static EventDAO instance;

    private EventDAO() {
    }

    public static EventDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new EventDAO();
            instance.emf = emf;
        }
        return instance;
    }

    public static void addUserToEvent(Event event, User user) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            event.addUser(user);
            em.merge(event);
            em.getTransaction().commit();
        }
    }

    public List<Event> getAllEvents() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("From Event e", Event.class).getResultList();
        }     
    }
}
