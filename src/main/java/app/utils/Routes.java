package app.utils;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import static io.javalin.apibuilder.ApiBuilder.*;

import org.eclipse.jetty.io.EndPoint;
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
        }
        securityController = new SecurityController(emf);
        userController = new UserController(emf);
        eventController = new EventController(emf);
        return instance;
    }

    public EndpointGroup securityResources() {
        return () -> {
            path("/auth", () -> {
                post("/login", securityController.login(), SecurityRoles.ANYONE);
                post("/register", securityController.register(), SecurityRoles.ANYONE);
                post("/addRoleToUser", securityController.addRoleToUser(), SecurityRoles.ADMIN);
            });
        };
    }

    public EndpointGroup eventResources() {
        return () -> {
            path("/event", () -> {
                put("/registerUser", eventController.addUserToEvent(), SecurityRoles.USER);
                put("/cancelRegistration", eventController.cancelRegistration(), SecurityRoles.USER);
                get("/upcoming", eventController.getUpcomingEvents(), SecurityRoles.ANYONE);
                put("/cancelEvent/{id}", eventController.cancelEvent(), SecurityRoles.INSTRUCTOR, SecurityRoles.ADMIN);
                put("/createEvent", eventController.createEvent(), SecurityRoles.INSTRUCTOR, SecurityRoles.ADMIN);
            });
        };
    }

    public EndpointGroup testResources() {
        return () -> {
            path("/test", () -> {
                get("/hello", ctx -> ctx.result("Hello World!"), SecurityRoles.ANYONE);
            });
        };
    }

    public EndpointGroup securedRoutes() {
        return () -> {
            before(securityController.authenticate());
            path("/users", () -> {
                get(userController.getAllUsers(), SecurityRoles.ADMIN);
                put("/update", userController.updateUser(), SecurityRoles.ADMIN, SecurityRoles.USER);
                delete("/delete/{id}", userController.deleteUser(), SecurityRoles.ADMIN,SecurityRoles.INSTRUCTOR,SecurityRoles.STUDENT,SecurityRoles.USER);

            });
            path("/events", () -> {
                get(eventController.getAllEvents(), SecurityRoles.ADMIN, SecurityRoles.INSTRUCTOR);
                put("/{id}", eventController.updateEvent(), SecurityRoles.ADMIN, SecurityRoles.INSTRUCTOR);
            });
            path("/registrations", () -> {
                get("/{id}", eventController.getRegistrationsToEvent(), SecurityRoles.INSTRUCTOR);
            });
            post("auth/logout", securityController.logout(), SecurityRoles.ANYONE);

        };
    }

    public EndpointGroup unsecuredRoutes(){
        return () -> {
            get("/events/{id}", eventController.getEventById(), SecurityRoles.ANYONE);
            get("/events/category/{category}", eventController.getEventByCategory(), SecurityRoles.ANYONE);
            get("/events/status/{status}", eventController.getEventByStatus(), SecurityRoles.ANYONE);
            get("/registration/{userid}/{eventid}", eventController.getSingleRegistrationById(), SecurityRoles.ANYONE);
        };
    }
}
