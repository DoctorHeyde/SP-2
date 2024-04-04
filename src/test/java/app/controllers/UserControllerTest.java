package app.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.utils.TestUtils;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.EventDTO;
import app.dtos.TokenDTO;
import app.entities.Event;
import app.entities.User;
import app.utils.Routes;
import app.utils.TestUtils;
import app.utils.TokenUtil;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import static io.restassured.RestAssured.*;
import jakarta.persistence.EntityManagerFactory;

public class UserControllerTest {
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
    public void getAllEvents() throws JsonMappingException, JsonProcessingException {
        String requestBody = "{\"email\": \"instructor\",\"password\": \"instructor\"}";
        Response logingResponse =
            given()
                .body(requestBody)
            .when()
                .post("/auth/login");

        TokenDTO token = objectMapper.readValue(logingResponse.body().asString(), TokenDTO.class);
        Header header = new Header("Authorization", "Bearer " + token.getToken());
        
        Response getResponse = given()
            .header(header)
        .when()
            .get("/events");

        //EventDTO[] events = objectMapper.readValue(getResponse.asString(), EventDTO[].class);
        //Map<String,Event> allEvents = TestUtils.getEvents(emfTest);
        //for(EventDTO event : events){
        //    assertEquals(event.getTitle(), allEvents.get(event.getTitle()).getTitle());
        //}
    }

    @Test
    void resetPassword(){

        String requestLoginBody = "{\"email\": \"instructor\",\"password\": \"instructor\"}";

        TokenDTO token = given()
                .contentType("application/json")
                .body(requestLoginBody)
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .as(TokenDTO.class);

        Header header = new Header("Authorization", "Bearer " + token.getToken());

        String updateBody = "{\"email\": \"instructor\", \"password\": \"instructor\", " +
                "\"newPassword\": \"instructorChanged\"}";

        given()
                .when()
                .header(header)
                .body(updateBody)
                .when()
                .request("PUT", "resetUserPassword")
                .then()
                .statusCode(201);
    }    

    @Test
    public void deleteUser() throws JsonMappingException, JsonProcessingException{
        String requestLoginBody = "{\"email\": \"user\",\"password\": \"user\"}";
        Response logingResponse =
            given()
                .body(requestLoginBody)
            .when()
                .post("/auth/login");

        TokenDTO token = objectMapper.readValue(logingResponse.body().asString(), TokenDTO.class);
        Header header = new Header("Authorization", "Bearer " + token.getToken());
        User user = TestUtils.getUsers(emfTest).values().stream().filter(u -> u.getName().equals("user")).findFirst().get();
        given()
            .header(header)
            .when()
            .delete("/users/delete/" + user.getEmail())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());
        
        Map<String, User> users = TestUtils.getUsers(emfTest);

        // assertNull(users.get("user"));
    }
    
    @Test
    public void deleteUserWrongId() throws JsonMappingException, JsonProcessingException{
        String requestLoginBody = "{\"email\": \"user\",\"password\": \"user\"}";
        Response logingResponse =
            given()
                .body(requestLoginBody)
            .when()
                .post("/auth/login");

        TokenDTO token = objectMapper.readValue(logingResponse.body().asString(), TokenDTO.class);
        Header header = new Header("Authorization", "Bearer " + token.getToken());
        given()
            .header(header)
            .when()
            .delete("/users/delete/" + "email")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.getCode())
            .body("msg", equalTo("Delete not allowed"));
    }
}