package app.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import app.entities.Event;
import app.entities.Status;
import app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private long id;
    private String title;
    private String startTime;
    private String description;
    private LocalDate dateOfEvent;
    private int durationInHours;
    private int maxNumberOfStudents;
    private String locationOfEvent;
    private String instructor;
    private double price;
    private String category;
    private String image;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime canceledAt;
    private Set<User> users;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.startTime = event.getStartTime();
        this.description = event.getDescription();
        this.dateOfEvent = event.getDateOfEvent();
        this.durationInHours = event.getDurationInHours();
        this.maxNumberOfStudents = event.getMaxNumberOfStudents();
        this.locationOfEvent = event.getLocationOfEvent();
        this.instructor = event.getInstructor();
        this.price = event.getPrice();
        this.category = event.getCategory();
        this.image = event.getImage();
        this.status = event.getStatus();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
        this.canceledAt = event.getCanceledAt();
        this.users = event.getUsers();
    }
}
