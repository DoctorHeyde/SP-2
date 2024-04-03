package app.controllers;

import app.persistance.UserDAO;
import app.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import jakarta.persistence.EntityManagerFactory;

public class InstructorController {
    private UserDAO userDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private TokenUtil tokenUtil = new TokenUtil();

    public InstructorController(EntityManagerFactory emf) {
        userDAO = UserDAO.getUserDAOInstance(emf);
    }

    public Handler createEvent(){

    }
}
