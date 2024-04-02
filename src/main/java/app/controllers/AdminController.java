package app.controllers;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.EventDTO;
import app.dtos.UserDTO;
import app.persistance.EventDAO;
import app.persistance.UserDAO;
import app.utils.TokenUtil;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class AdminController {
    private UserDAO userDAO;
    private EventDAO eventDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private TokenUtil tokenUtil = new TokenUtil();

    public AdminController(EntityManagerFactory emf) {
        userDAO = UserDAO.getUserDAOInstance(emf);
        eventDAO = EventDAO.getInstance(emf);
    }

    public Handler getAllUsers() {
        return ctx -> {
            String json = objectMapper.writeValueAsString(userDAO.getAllUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toList()));
            System.out.println(json);
            ctx.status(HttpStatus.OK).json(json);
        };
    }
    
    public Handler getAllEvents() {
        return ctx -> {
            String json = objectMapper.writeValueAsString(eventDAO.getAllEvents().stream().map(e -> new EventDTO(e)).collect(Collectors.toList()));
            System.out.println(json);
            ctx.status(HttpStatus.OK).json(json);
        };
    }

}
