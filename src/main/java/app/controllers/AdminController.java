package app.controllers;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.UserDTO;
import app.persistance.UserDAO;
import app.utils.TokenUtil;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class AdminController {
    private UserDAO userDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private TokenUtil tokenUtil = new TokenUtil();

    public AdminController(EntityManagerFactory emf) {
        userDAO = UserDAO.getUserDAOInstance(emf);
    }

    public Handler getAllUsers() {
        return ctx -> {
            String json = objectMapper.writeValueAsString(userDAO.getAllUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toList()));
            System.out.println(json);
            ctx.status(HttpStatus.OK).json(json);
        };
    }

}
