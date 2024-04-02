package app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.persistance.UserDAO;
import app.utils.TokenUtil;
import io.javalin.http.Handler;
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
            
        };
    }

}
