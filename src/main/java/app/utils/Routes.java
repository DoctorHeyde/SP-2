package app.utils;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import static io.javalin.apibuilder.ApiBuilder.*;
import app.controllers.SecurityController;

public class Routes {
    private static Routes instance;
    private static SecurityController securityController;
    private Routes() {
    }

    public static Routes getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new Routes();
            securityController = new SecurityController(emf);
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
}
