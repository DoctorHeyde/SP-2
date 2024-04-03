package app.dtos;

import app.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String title;

    private String dateOfEvent;


    public EventDTO(Event event){
        title = event.getTitle();
    }

}
