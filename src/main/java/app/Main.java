package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.exceptions.EntityNotFoundException;
import app.utils.Routes;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    private static final int port = 7170;

    public static void main(String[] args) throws EntityNotFoundException {
        // JAVALIN SETUP
        startServer(HibernateConfig.getEntityManagerFactory(false));
        //closeServer();
    }

    public static void startServer(EntityManagerFactory emf) {
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance(emf);
        Routes routes = Routes.getInstance(emf);
        applicationConfig
                .initiateServer()
                .startServer(port)
                .setExceptionHandling()
                .setRoute(routes.securityResources())
                .setRoute(routes.testResources())
                .setRoute(routes.eventResources())
                .setRoute(routes.securedRoutes())
                //Add more endpoints here
                .checkSecurityRoles();
    }

    public static void closeServer() {
        ApplicationConfig.getInstance().stopServer();
    }
}
