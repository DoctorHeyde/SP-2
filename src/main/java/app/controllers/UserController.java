package app.controllers;

import java.util.stream.Collectors;

import app.dtos.EventDTO;
import app.entities.Event;
import app.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.UserDTO;
import app.persistance.UserDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;


public class UserController {

    private UserDAO userDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserController(EntityManagerFactory emf) {
        userDAO = UserDAO.getUserDAOInstance(emf);
    }

    public Handler getAllUsers() {
        return ctx -> {
            String json = objectMapper.writeValueAsString(userDAO.getAllUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toList()));
            System.out.println(json);
            ctx.status(HttpStatus.OK).json(json);
        };
    }

    public Handler updateUser() {
        return ctx -> {

            UserDTO updateUserAsDTO = ctx.bodyAsClass(UserDTO.class);

            User updateUser = userDAO.getByID(updateUserAsDTO.getEmail());

            userDAO.updateUser(updateUser);
            ctx.status(201).json("User has been updated ");


        };
    }
}