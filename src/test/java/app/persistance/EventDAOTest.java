package app.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.entities.Event;
import app.entities.Status;
import app.entities.User;
import app.utils.Routes;
import app.utils.TestUtils;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class EventDAOTest {
    private static ApplicationConfig appConfig;
    private static EntityManagerFactory emfTest;
    private static EventDAO eventdao;

    @BeforeAll
    public static void beforeAll() {

        // Setup test database using docker testcontainers
        emfTest = HibernateConfig.getEntityManagerFactory(true);
        eventdao = EventDAO.getInstance(emfTest);

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
    void testAddUserToEvent() {
        try (EntityManager em = emfTest.createEntityManager()) {
            Event event = em.createQuery("From Event e where e.title = 'title1'", Event.class).getSingleResult();
            User user = em.createQuery("From User u where u.email = 'user'", User.class).getSingleResult();
            EventDAO.addUserToEvent(event, user);
            Event eventUpdated = em.find(Event.class, event.getId());
            User userUpdated = em.find(User.class, user.getEmail());
            assertTrue(eventUpdated.getUsers().contains(user));
            assertTrue(userUpdated.getEvents().contains(event));
        }
    }

    @Test
    void testUpdate() {
        try (EntityManager em = emfTest.createEntityManager()) {
            Event event = em.createQuery("From Event e where e.title = 'title1'", Event.class).getSingleResult();
            event.setTitle("newTitle");
            Event eventUpdated = eventdao.update(event);
            assertEquals("newTitle", eventUpdated.getTitle());
        }
    }

    @Test
    void testGetAllEvents() {
        try (EntityManager em = emfTest.createEntityManager()) {
            assertEquals(4, eventdao.getAllEvents().size());
        }
    }

    @Test
    void testGetById() {
        try (EntityManager em = emfTest.createEntityManager()) {
            Event event = em.createQuery("From Event e where e.title = 'title1'", Event.class).getSingleResult();
            Event eventById = eventdao.getById(event.getId());
            assertEquals(event.getTitle(), eventById.getTitle());
        }
    }

    @Test
    void testGetUpcomingEvent() {
        try (EntityManager em = emfTest.createEntityManager()) {
            assertEquals(2, eventdao.getUpcomingEvent().size());
        }
    }

    @Test
    void testGetEventByStatus() {
        assertEquals(2, eventdao.getEventByStatus(Status.UPCOMING).size());
    }
}
