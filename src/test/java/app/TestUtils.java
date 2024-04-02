package app;
import java.util.Map;
import java.util.stream.Collectors;

import app.entities.Role;
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
            
            User u1 = new User("admin", "admin");
            User u2 = new User("user", "user");

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
        return UserDAO.getUserDAOInstance(emfTest).getAllUsers().stream().collect(Collectors.toMap(u -> u.getUsername(), u -> u));
    }
}
