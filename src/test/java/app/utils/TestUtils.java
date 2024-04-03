package app.utils;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import app.entities.Event;
import app.entities.Role;
import app.entities.Status;
import app.entities.User;
import app.persistance.UserDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


/**
 * Class to all the setup you need for testing
 */
public class TestUtils {
    public static void createUsersAndRoles(EntityManagerFactory emfTest) {
        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role r").executeUpdate();
            
            User u1 = new User("admin", "admin", "admin", 10);
            User u2 = new User("user", "user", "user", 10);

            Role r1 = new Role("admin");
            Role r2 = new Role("user");

            u1.addRole(r1);
            u2.addRole(r2);
                
            em.persist(u1);
            em.persist(u2);
            em.persist(r1);
            em.persist(r2);
            
            em.getTransaction().commit();
        }
    }    
    public static Map<String, User> getUsers(EntityManagerFactory emfTest) {
        return UserDAO.getUserDAOInstance(emfTest).getAllUsers().stream().collect(Collectors.toMap(u -> u.getEmail(), u -> u));
    }
    
    public static void createEvents(EntityManagerFactory emfTest) {
        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Event e").executeUpdate();
            
            Event e1 = new Event("title1", "startTime", "description", LocalDate.now(), 100, 10, "locationOfEvent", "instructor", 100d, "category", "image", Status.ACTIVE);
            Event e2 = new Event("title2", "startTime", "description", LocalDate.now(), 100, 10, "locationOfEvent", "instructor", 100d, "category", "image", Status.ACTIVE);
            
            em.persist(e1);
            em.persist(e2);
            em.getTransaction().commit();
        }
    }
    public static Map<String, Event> getEvents(EntityManagerFactory emfTest) {
        try(EntityManager em = emfTest.createEntityManager()){
            return em.createQuery("FROM Event e", Event.class).getResultList().stream().collect(Collectors.toMap(e -> e.getTitle(), e -> e));
        }
    }    
}
