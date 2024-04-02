package app.controllers;

import java.util.Set;

import app.dtos.UserDTO;
import io.javalin.http.Handler;

public interface ISecurityController {
    public Handler login();
    public Handler register();
    public Handler authenticate();
    public boolean authorize(UserDTO user, Set<String> allowedRoles);

}