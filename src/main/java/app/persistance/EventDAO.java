package app.persistance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.Status;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class EventDAO extends ADAO<Event, Integer> {
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

    @Override
    public List<Event> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public Event getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Event.class, id);
        }
    }

    @Override
    public void update(Event t) {
        try(var em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.merge(t);
            em.getTransaction().commit();
        }
    }


    public List<Event> getUpcomingEvent() {
        try (EntityManager em = emf.createEntityManager()) {
            var query = em.createQuery("select a from Event a where a.status = :status").setParameter("status", Status.UPCOMING);
            return query.getResultList();
        }
    }

    public static void cancelRegistration(Event eventObj, User user) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            eventObj.removeUser(user);
            em.merge(eventObj);
            em.getTransaction().commit();
        }
    }

    public Event updateEvent(Event updatedEvent) {

        try(var em = emf.createEntityManager()){

            em.getTransaction().begin();
            em.merge(updatedEvent);
            em.getTransaction().commit();

            Event eventUpdatedInDb = em.find(Event.class, updatedEvent.getId());

            return eventUpdatedInDb;
        }
    }
}
