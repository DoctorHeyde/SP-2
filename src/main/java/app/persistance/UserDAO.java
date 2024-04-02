package app.persistance;


import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UserDAO implements IUserDAO {
    private static UserDAO instance;
    private static EntityManagerFactory emf;

    private UserDAO() {
    }

    public static UserDAO getUserDAOInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new UserDAO();
            emf = _emf;
        }
        return instance;
    }

    @Override
    public User createUser(String username, String password) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = new User(username, password);
        Role userRole = em.find(Role.class, "user");
        if (userRole == null) {
            userRole = new Role("user");
            em.persist(userRole);
        }
        user.addRole(userRole);
        em.persist(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }

    @Override
    public User verifyUser(String username, String password) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username);
            if (!user.verifyUser(password))
                throw new EntityNotFoundException("Wrong password");
            return user;
        }
    }

    @Override
    public Role createRole(String role) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Role newRole = new Role(role);
        em.persist(newRole);
        em.getTransaction().commit();
        em.close();
        return newRole;
    }

    @Override
    public User addRoleToUser(String username, String role) throws EntityNotFoundException{
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = em.find(User.class, username);
        Role newRole = em.find(Role.class, role);
        if (user == null)
            throw new EntityNotFoundException("No user found with username: " + username);
        if (newRole == null)
            newRole = createRole(role);
        user.addRole(newRole);
        em.merge(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }
}