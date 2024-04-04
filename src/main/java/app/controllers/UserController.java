package app.controllers;

import java.util.stream.Collectors;

import app.entities.User;
import app.exceptions.EntityNotFoundException;
import app.exceptions.NotAuthorizedException;
import app.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.dtos.UserDTO;
import app.entities.User;
import app.persistance.UserDAO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import app.entities.Role;


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
                // Getting email, password and newPassword entered by logged in user
                UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

                // Getting email, name and roles of the logged in user
                String header = ctx.header("Authorization");
                String token = header.split(" ")[1];
                UserDTO verifiedTokenUser = TokenUtil.verifyToken(token);

                // Checking if the email entered when resetting password (the above email) actually is the same
                // as the email of the logged in user who wants to reset password
                if (verifiedTokenUser.getEmail().equalsIgnoreCase(userDTO.getEmail())) {

                    // Verifying the password entered by the user when resetting the password
                    User verifiedUserEntity = userDAO.verifyUser(userDTO.getEmail(), userDTO.getPassword());

                    // Setting the new password
                    verifiedUserEntity.setNewPassword(userDTO.getNewPassword());

                    // Updating the password of the user
                    userDAO.updateUser(verifiedUserEntity);

                    ctx.status(201).json("Password has been reset");
                }
                else{
                    throw new NotAuthorizedException(401, "You have to enter the email that you've logged in with");
                }

            } catch (EntityNotFoundException e) {

                ctx.status(HttpStatus.NOT_FOUND);
                ctx.json(returnObject.put("Message: ", e.getMessage()));
            }
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
            
            userDAO.deleteUser(userDAO.getById(user.getEmail()));
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }
    
}

