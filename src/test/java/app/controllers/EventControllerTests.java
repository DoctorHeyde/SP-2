package app.controllers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.TestUtils;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.EventDTO;
import app.dtos.TokenDTO;
import app.entities.Event;
import app.utils.Routes;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import jakarta.persistence.EntityManagerFactory;

public class EventControllerTests {
        private static ApplicationConfig appConfig;
    private static final String BASE_URL = "http://localhost:7777/api";
    private static EntityManagerFactory emfTest;
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeAll
    public static void beforeAll(){
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
    public void getEventById() throws JsonMappingException, JsonProcessingException {
        Event first = TestUtils.getEvents(emfTest).values().stream().findFirst().get();
        Response response = given().when()
            .get("/events/" + first.getId()).peek()
            ;

        EventDTO actualEvent = objectMapper.readValue(response.body().asString(), EventDTO.class);

        assertEquals(first.getTitle(), actualEvent.getTitle());
    }    

    
    @Test
    void registerUserToEvent() {
        String requestLoginBody = "{\"email\": \"user\",\"password\": \"user\"}";
        TokenDTO token = RestAssured
            .given()
            .contentType("application/json")
                .body(requestLoginBody)
            .when()
                .post("/auth/login")
                .then()
                .extract()
                .as(TokenDTO.class);

        Header header = new Header("Authorization", "Bearer " + token.getToken());        

        String requestBody = "{\"email\": \"user\",\"id\": \"1\"}";
        RestAssured.given()
            .contentType("application/json")
            .header(header)
            .body(requestBody)
            .when()
            .put("/event/registerUser")
            .then()
            .statusCode(200);
    }
}
