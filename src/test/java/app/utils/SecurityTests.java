package app.utils;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.controllers.SecurityController;
import app.dtos.TokenDTO;
import app.dtos.UserDTO;
import app.persistance.UserDAO;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import app.config.ApplicationConfig;
import static io.restassured.RestAssured.*;
import jakarta.persistence.EntityManagerFactory;

public class SecurityTests {
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
                .startServer(7777)
            ;
    }

    @BeforeEach
    public void setUpEach() {
        // Setup test database for each test
        
        
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
    public void createUser() {
        String requestBody = "{\"username\": \"test1\",\"password\": \"test1\"}";
        ResponseBody res = given()
            .body(requestBody)
        .when()
            .post("/auth/register")
            .peek()
            .body();
            
        
        String body = res.asString();
        String token = body.split(",")[0].split(":")[1].replace("\"", "");
        UserDTO newUser = (TokenUtil.verifyToken(token));
        assertNotNull(newUser);
        assertEquals("test1", newUser.getUsername());
    }

    @Test
    public void login() {
        String requestBody = "{\"username\": \"user\",\"password\": \"user\"}";
        given()
            .body(requestBody)
        .when()
            .post("/auth/login")
        .then()
            .log().body()
            .assertThat()
            .statusCode(200)
            .body("userName", equalTo("user"));
        ;
    }
    
    @Test
    public void protectedUser() throws JsonMappingException, JsonProcessingException {
        String requestBody = "{\"username\": \"user\",\"password\": \"user\"}";
        Response res =
            given()
                .body(requestBody)
            .when()
                .post("/auth/login");

        TokenDTO token = objectMapper.readValue(res.body().asString(), TokenDTO.class);
        Header header = new Header("Authorization", "Bearer " + token.getToken());
        
        given()
            .header(header)
        .when()
            .get("/protected/user_demo")
        .then()
            .log().body()
            .assertThat()
            .statusCode(200)
            .body("msg", equalTo("Hello from USER Protected"))
            ;
        
    }

    @Test
    public void userTriesToAccessAdmin() throws JsonMappingException, JsonProcessingException {
        String requestBody = "{\"username\": \"user\",\"password\": \"user\"}";
        Response res =
            given()
                .body(requestBody)
            .when()
                .post("/auth/login")
                .peek();
                

        TokenDTO token = objectMapper.readValue(res.body().asString(), TokenDTO.class);
        Header header = new Header("Authorization", "Bearer " + token.getToken());
        
        given()
            .header(header)
        .when()
            .get("/protected/admin_demo")
        .then()
            .log().headers()
            .log().body()
            .assertThat()
            .statusCode(403)
            .body("errrorMessage", equalTo("Unauthorized with roles: [USER]"));
    }

    @Test
    public void protectedAdmin() throws JsonMappingException, JsonProcessingException {
        String requestBody = "{\"username\": \"admin\",\"password\": \"admin\"}";
        Response res =
            given()
                .body(requestBody)
            .when()
                .post("/auth/login");

        TokenDTO token = objectMapper.readValue(res.body().asString(), TokenDTO.class);
        Header header = new Header("Authorization", "Bearer " + token.getToken());
        
        given()
            .header(header)
        .when()
            .get("/protected/admin_demo")
        .then()
            .log().body()
            .assertThat()
            .statusCode(200)
            .body("msg", equalTo("Hello from ADMIN Protected"))
            ;
    
    }    
}

