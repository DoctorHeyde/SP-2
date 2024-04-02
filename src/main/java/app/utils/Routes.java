package app.utils;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import static io.javalin.apibuilder.ApiBuilder.*;

import org.eclipse.jetty.io.EndPoint;

import app.controllers.AdminController;
import app.controllers.SecurityController;

public class Routes {
    private static Routes instance;
    private static SecurityController securityController;
    private static AdminController adminController;
    private Routes() {
    }

    public static Routes getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new Routes();
            securityController = new SecurityController(emf);
            adminController = new AdminController(emf);
        }
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
                get(adminController.getAllUsers(), SecurityRoles.ADMIN);
                get(adminController.getAllEvents(), SecurityRoles.ADMIN);
            });
        };
    }
}
