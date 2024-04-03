package app.controllers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.TestUtils;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.TokenDTO;
import app.utils.Routes;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import jakarta.persistence.EntityManagerFactory;


import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;

public class EventControllerTests {
    private static ApplicationConfig appConfig;
    private static final String BASE_URL = "http://localhost:7777/api";
    private static EntityManagerFactory emfTest;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.baseURI = BASE_URL;
        objectMapper.findAndRegisterModules();

        // Setup test database using docker testcontainers
        emfTest = HibernateConfig.getEntityManagerFactory(true);

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
                .startServer(7777)
        ;
    }

    @BeforeEach
    public void setUpEach() {
        // Setup test database for each test
        TestUtils.createUsersAndRoles(emfTest);
        TestUtils.createEvents(emfTest);

    }

    @AfterAll
    static void afterAll() {
        emfTest.close();
        appConfig.stopServer();
    }

    @Test
    void registerUserToEvent() {
        String requestLoginBody = "{\"email\": \"user\",\"password\": \"user\"}";
        TokenDTO token = given()
                .contentType("application/json")
                .body(requestLoginBody)
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .as(TokenDTO.class);

        Header header = new Header("Authorization", "Bearer " + token.getToken());

        String requestBody = "{\"email\": \"user\",\"id\": \"1\"}";
        given()
                .contentType("application/json")
                .header(header)
                .body(requestBody)
                .when()
                .put("/event/registerUser")
                .then()
                .statusCode(200);
    }

    @Test
    void getUpcomingEvents() {
        String dateAsString =
                given()
                        .when()
                        .get("event/upcoming")
                        .then()
                        .statusCode(200)
                        .body("[0].dateOfEvent", notNullValue())
                        //.body("[0].dateOfEvent", matchesPattern("yyyy-MM-dd"))
                        .extract()
                        .path("[0].dateOfEvent");

        LocalDate date = LocalDate.parse(dateAsString);

        assertThat(date, greaterThan(LocalDate.now()));

    }


}

