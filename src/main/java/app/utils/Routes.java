package app.utils;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import static io.javalin.apibuilder.ApiBuilder.*;

import app.controllers.EventController;
import app.controllers.SecurityController;
import app.controllers.UserController;

public class Routes {
    private static Routes instance;
    private static SecurityController securityController;
    private static UserController userController;
    private static EventController eventController;

    private Routes() {
    }

    public static Routes getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new Routes();
            securityController = new SecurityController(emf);
            userController = new UserController(emf);
            eventController = new EventController(emf);
        }
        return instance;
    }

    public EndpointGroup securityResources() {
        return () -> {
            before(securityController.authenticate());
            path("/auth", () -> {
                post("/login", securityController.login(), SecurityRoles.ANYONE);
                post("/register", securityController.register(), SecurityRoles.ANYONE);
                post("/addRoleToUser", securityController.addRoleToUser(), SecurityRoles.ADMIN);
                get(userController.getAllUsers(), SecurityRoles.ADMIN);
                delete("/delete/{id}", userController.deleteUser(), SecurityRoles.ADMIN, SecurityRoles.INSTRUCTOR,
                        SecurityRoles.STUDENT, SecurityRoles.USER);
            });
        };
    }

    public EndpointGroup eventResources() {
        return () -> {
            before(securityController.authenticate());
            path("/event", () -> {
                get(eventController.getAllEvents(), SecurityRoles.ADMIN, SecurityRoles.INSTRUCTOR);
                put("/{id}", eventController.updateEvent(), SecurityRoles.ADMIN, SecurityRoles.INSTRUCTOR);
                put("/registerUser", eventController.addUserToEvent(), SecurityRoles.USER);
                put("/cancelRegistration", eventController.cancelRegistration(), SecurityRoles.USER);
                get("/upcoming", eventController.getUpcomingEvents(), SecurityRoles.ANYONE);
                put("/cancelEvent/{id}", eventController.cancelEvent(), SecurityRoles.INSTRUCTOR, SecurityRoles.ADMIN);
                get("/{id}", eventController.getEventById(), SecurityRoles.ANYONE);
                get("/category/{category}", eventController.getEventByCategory(), SecurityRoles.ANYONE);
                get("/status/{status}", eventController.getEventByStatus(), SecurityRoles.ANYONE);
            });
        };
    }

    public EndpointGroup testResources() {
        return () -> {
            before(securityController.authenticate());
            path("/test", () -> {
                get("/hello", ctx -> ctx.result("Hello World!"), SecurityRoles.ANYONE);
            });
        };
    }

    public EndpointGroup registrationsRoutes() {
        return () -> {
            before(securityController.authenticate());
            path("/registrations", () -> {
                get("/{id}", eventController.getRegistrationsToEvent(), SecurityRoles.INSTRUCTOR);
            });
            path("/registration", () -> {
                get("/{userid}/{eventid}", eventController.getSingleRegistrationById(), SecurityRoles.ANYONE);
            });
        };
    }
}
