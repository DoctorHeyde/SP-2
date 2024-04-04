package app.persistance;


import app.entities.Event;
import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public User createUser(String email, String password, String name, int phoneNumber, Set<String> roles) {

        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            User user = new User(email, password, name, phoneNumber);

            for (String role : roles){
                Role userRole = em.find(Role.class, role);
                user.addRole(userRole);
            }

            em.persist(user);
            em.getTransaction().commit();
            em.close();
            return user;
        }
    }

    @Override
    public User verifyUser(String email, String password) throws EntityNotFoundException {

        try (EntityManager em = emf.createEntityManager()) {

            User user = em.find(User.class, email);

            if (user == null)
                throw new EntityNotFoundException("No user found with email: " + email);
            if (!user.verifyPassword(password))
                throw new EntityNotFoundException("Wrong password");

            var query = em.createQuery("SELECT a FROM Event a JOIN User u ON u.email = :email")
                    .setParameter("email", user.getEmail());

            List<Event> eventRegistrationOfUser = query.getResultList();

            if(eventRegistrationOfUser.size() >0){
                user.setEvents(eventRegistrationOfUser.stream().collect(Collectors.toSet()));
            }

            user.getRoles().size();

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
    public User addRoleToUser(String email, String role) throws EntityNotFoundException{
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = em.find(User.class, email);
        Role newRole = em.find(Role.class, role);
        if (user == null)
            throw new EntityNotFoundException("No user found with email: " + email);
        if (newRole == null)
            newRole = createRole(role);
        user.addRole(newRole);
        em.merge(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }

    public List<User> getAllUsers() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("From User u", User.class).getResultList();
        }       
    }

    public User getByID(String id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(User.class, id);
        }
    }

    public void updateUser(User user) {

        try(var em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        }
    }
}