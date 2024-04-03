package app.dtos;

import app.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String title;
    private String instructor;


    public EventDTO(Event event){
        title = event.getTitle();
        instructor = event.getInstructor();
    }

}
