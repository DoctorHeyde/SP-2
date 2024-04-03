package app.dtos;

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
    private String dateOfEvent;
    private int durationInHours;
    private int maxNumberOfStudents;
    private String locationOfEvent;
    private String instructor;
    private double price;
    private String category;
    private String image;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String canceledAt;
    private String users;
}
