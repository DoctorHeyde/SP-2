package app.controllers;

import static org.hamcrest.Matchers.equalTo;
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

import app.TestUtils;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.controllers.SecurityController;
import app.dtos.TokenDTO;
import app.dtos.UserDTO;
import app.entities.User;
import app.persistance.UserDAO;
import app.utils.Routes;
import app.utils.TokenUtil;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import app.config.ApplicationConfig;
import static io.restassured.RestAssured.*;
import jakarta.persistence.EntityManagerFactory;

public class AdminControllerTests {
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
        
    }
    
    @AfterAll
    static void afterAll() {
        emfTest.close();
        appConfig.stopServer();
    }
    
    @Test
    public void defTest(){
        given().when().get("/test/hello").peek().then().statusCode(200);
    }

    @Test
    public void getAllUsers() throws JsonMappingException, JsonProcessingException {
        String requestBody = "{\"username\": \"admin\",\"password\": \"admin\"}";
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
            .get("/users")
            ;
        String tmp = getResponse.asString();
            UserDTO[] users = objectMapper.readValue(tmp, UserDTO[].class);
        Map<String,User> allUsers = TestUtils.getUsers(emfTest);
        for(UserDTO user : users){
            assertEquals(user.getUsername(), allUsers.get(user.getUsername()).getUsername());
        }
    }    
}