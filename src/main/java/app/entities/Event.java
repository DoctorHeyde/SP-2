package app.entities;


import app.dtos.EventDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime; 
import java.util.HashSet;
import java.util.Set;


@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonManagedReference
    private Set<User> users = new HashSet<>();


    public Event(String title, String startTime, String description, LocalDate dateOfEvent, int durationInHours, int maxNumberOfStudents, String locationOfEvent, String instructor, double price, String category, String image, Status status) {
        this.title = title;
        this.startTime = startTime;
        this.description = description;
        this.dateOfEvent = dateOfEvent;
        this.durationInHours = durationInHours;
        this.maxNumberOfStudents = maxNumberOfStudents;
        this.locationOfEvent = locationOfEvent;
        this.instructor = instructor;
        this.price = price;
        this.category = category;
        this.image = image;
        this.status = status;
    }

    public Event (EventDTO eventDTO){
        this.title = eventDTO.getTitle();
        this.startTime = eventDTO.getStartTime();
        this.description = eventDTO.getDescription();
        LocalDate dateOfEventFromDTO = LocalDate.parse(eventDTO.getDateOfEvent());
        this.dateOfEvent = dateOfEventFromDTO;
        this.durationInHours = eventDTO.getDurationInHours();
        this.maxNumberOfStudents = eventDTO.getMaxNumberOfStudents();
        this.locationOfEvent = eventDTO.getLocationOfEvent();
        this.instructor = eventDTO.getInstructor();
        this.price = eventDTO.getPrice();
        this.category = eventDTO.getCategory();
        this.image = eventDTO.getImage();

        this.status = eventDTO.getStatus();
    }

    @PrePersist
    private void eventCreatedAt() throws RuntimeException{
        LocalDateTime localDateTime = LocalDateTime.now();
        this.createdAt = localDateTime;

    }

    @PreUpdate
    private void eventUpdatedAt() throws RuntimeException{
        LocalDateTime localDateTime = LocalDateTime.now();
        this.updatedAt = localDateTime;

        // TODO: Be sure that this works
        if(status.toString().equalsIgnoreCase("canceled")){
            this.canceledAt = localDateTime;
        }
    }

    public void removeUser(User user) {
        if(user != null){
            users.remove(user);
            if(user.getEvents().contains(this)){
                user.removeEvent(this);
            }
        }
    }

    public void addUser(User user) {
        if (user !=null){
            users.add(user);
            if(!user.getEvents().contains(this)){
                user.addEvent(this);
            }
        }
    }
}
