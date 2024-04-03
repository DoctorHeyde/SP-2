package app.persistance;

import java.time.LocalDate;
import java.util.List;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.Status;
import app.entities.User;
import jakarta.persistence.EntityManager;
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

    @Override
    public List<EventDTO> getAll() {
        // try (var em = emf.createEntityManager()) {
        //     var query = em.createQuery("SELECT e FROM Event e", Event.class);
        //     List<Event> events = query.getResultList();
        //     List<EventDTO> eventDTOs = events.stream()
        //             .map(event -> new EventDTO(
        //                     event.getId(),
        //                     event.getTitle(),
        //                     event.getStartTime(),
        //                     event.getDescription(),
        //                     event.getDateOfEvent().toString(),
        //                     event.getDurationInHours(),
        //                     event.getMaxNumberOfStudents(),
        //                     event.getLocationOfEvent(),
        //                     event.getInstructor(),
        //                     event.getPrice(),
        //                     event.getCategory(),
        //                     event.getImage(),
        //                     event.getStatus().toString(),
        //                     event.getCreatedAt().toString(),
        //                     event.getUpdatedAt().toString(),
        //                     event.getCanceledAt() != null ? event.getCanceledAt().toString() : null,
        //                     event.getUsers().stream().map(User::getEmail).collect(Collectors.joining(", "))))
        //             .collect(Collectors.toList());
        //     return eventDTOs;
        // }
        return null;
    }

    @Override
    public Event getByID(Integer i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByID'");
    }

    @Override
    public void update(Event t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public Event createEvent(String title, String startTime, String description, LocalDate dateOfEvent, int durationInHours, int maxNumberOfStudents, String locationOfEvent, String instructor, double price, String category, String image, Status status){

        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Event event = new Event(title, startTime, description, dateOfEvent, durationInHours, maxNumberOfStudents, locationOfEvent, instructor, price, category, image, status);

//            for (String role : roles){
//                Role instructorRole = em.find(Role.class, role);
//                user.addRole(instructorRole);
//            }

            em.persist(event);
            em.getTransaction().commit();
            em.close();
            return event;
        }
    }

    public static void addUserToEvent(Event event, User user) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            event.addUser(user);
            em.merge(event);
            em.getTransaction().commit();
        }
    }
}
