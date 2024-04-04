package app.controllers;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.UserDTO;
import app.entities.User;
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

    public Handler deleteUser() {
        return ctx -> {
            String userId = ctx.pathParam("id");
            UserDTO user = ctx.attribute("user");
            if(!userId.equals(user.getEmail())){
                ctx.status(HttpStatus.FORBIDDEN).json(objectMapper.createObjectNode().put("msg","Delete not allowed"));
                return;
            }
            
            userDAO.deleteUser(userDAO.getByID(user.getEmail()));
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }
    
}