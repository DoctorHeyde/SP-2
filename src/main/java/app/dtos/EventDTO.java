package app.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import app.entities.Event;
import app.entities.Status;
import app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import java.time.LocalDate;

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
    private String dateOfEvent;
    private int durationInHours;
    private int maxNumberOfStudents;
    private String locationOfEvent;
    private String instructor;
    private double price;
    private String category;
    private String image;
    private Status status;
    private String createdAt;
    private String updatedAt;
    private String canceledAt;
    private Set<UserDTO> users;

    public EventDTO(String title, String dateOfEvent) {
        this.title = title;
        this.dateOfEvent = dateOfEvent;

    }

    public EventDTO(Event event) {

        this.id = event.getId();
        this.title = event.getTitle();
        this.startTime = event.getStartTime();
        this.description = event.getDescription();
        this.dateOfEvent = event.getDateOfEvent().toString();
        this.durationInHours = event.getDurationInHours();
        this.maxNumberOfStudents = event.getMaxNumberOfStudents();
        this.locationOfEvent = event.getLocationOfEvent();
        this.instructor = event.getInstructor();
        this.price = event.getPrice();
        this.category = event.getCategory();
        this.image = event.getImage();
        this.status = event.getStatus();
        this.users = event.getUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toSet());

        if (event.getCreatedAt() != null) this.createdAt = event.getCreatedAt().toString();
        if (event.getUpdatedAt() != null) this.updatedAt = event.getUpdatedAt().toString();
        if (event.getCanceledAt() != null) this.canceledAt = event.getCanceledAt().toString();
    }
}

