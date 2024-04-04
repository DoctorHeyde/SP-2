package app.controllers;

import java.util.stream.Collectors;

import app.entities.User;
import app.exceptions.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.UserDTO;
import app.persistance.UserDAO;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public Handler resetPassword() {
        return ctx -> {

            ObjectNode returnObject = objectMapper.createObjectNode();

            try {
                UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

                User verifiedUserEntity = userDAO.verifyUser(userDTO.getEmail(), userDTO.getPassword());

                verifiedUserEntity.setNewPassword(userDTO.getNewPassword());
                userDAO.updateUser(verifiedUserEntity);

                ctx.status(201).json("Password has been reset");

            } catch (EntityNotFoundException e) {

                ctx.status(HttpStatus.NOT_FOUND);
                ctx.json(returnObject.put("Message: ", e.getMessage()));
            }
        };
    }
}
