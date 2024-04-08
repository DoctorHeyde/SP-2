package app.persistance;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;

import app.controllers.SecurityController;
import app.controllers.UserController;
import app.entities.User;
import app.exceptions.EntityNotFoundException;
import app.utils.Routes;
import app.utils.TestUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class UserDAOTest {

    private static ApplicationConfig appConfig;
    private static EntityManagerFactory emfTest;
    private static UserDAO userDAO;
    private static HibernateConfig hibernateConfig = new HibernateConfig();

    @BeforeAll
    public static void beforeAll() {

        // Setup test database using docker testcontainers
        emfTest = hibernateConfig.getEntityManagerFactory(true);
        userDAO = UserDAO.getUserDAOInstance(emfTest);

        Routes routes = Routes.getInstance(emfTest);
        // Start server
        appConfig = ApplicationConfig.getInstance(emfTest);
        appConfig
                .initiateServer()
                .setExceptionHandling()
                .checkSecurityRoles()
                .setRoute(routes.eventResources())
                .setRoute(routes.testResources())
                .setRoute(routes.securityResources())
                .setRoute(routes.securedRoutes())
                .setRoute(routes.unsecuredRoutes())
                .startServer(7777);
    }

    @BeforeEach
    public void setUpEach() {
        // Setup test database for each test
        TestUtils.createUsersAndRoles(emfTest);
        TestUtils.createEvents(emfTest);
        TestUtils.addEventToUser(emfTest);
    }

    @AfterAll
    static void afterAll() {
        emfTest.close();
        appConfig.stopServer();
    }


    @Test
    void verifyUser() throws EntityNotFoundException {
        User expectedUser = new User("admin","admin", "admin", 10);
        User notExpectedUser = new User("admin","adminWRONG", "admin", 10);

        User actual1 = userDAO.verifyUser("admin", "admin");

        //userDAO.verifyUser(notExpectedUser.getEmail(), notExpectedUser.getPassword());

        assertEquals(expectedUser.getEmail(), actual1.getEmail());
        assertEquals(expectedUser.getName(), actual1.getName());
        assertEquals(expectedUser.getPhoneNumber(), actual1.getPhoneNumber());


        assertThrows(EntityNotFoundException.class, () -> {
            userDAO.verifyUser(notExpectedUser.getEmail(), notExpectedUser.getPassword());});

    }

    @Test
    void updateUserTest(){

        User expectedUser = new User("admin","adminChanged", "adminNameChanged", 20);

        userDAO.updateUser(expectedUser);

        User actual = userDAO.getById("admin");

        assertEquals(expectedUser.getName(), actual.getName());
        assertEquals(expectedUser.getPhoneNumber(), actual.getPhoneNumber());
        assertNotEquals("admin", actual.getName());

    }
}
