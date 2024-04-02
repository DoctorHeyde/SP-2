package app.persistance;

import java.util.List;
import java.util.stream.Collectors;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

public class EventDAO extends ADAO<Event, EventDTO, Integer> {
    EntityManagerFactory emf;

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
}
