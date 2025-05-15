package tests;

import io.restassured.RestAssured;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static specs.RegisterSpec.*;

public class ReqresTests {

    private static final String API_KEY = "reqres-free-v1";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    @DisplayName("Successful registration")
    void successfulRegisterTest() {
        RegisterRequest registerData = new RegisterRequest("eve.holt@reqres.in", "pistol");

        RegisterResponse response = step("Make registration request", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(registerResponseSpec)
                        .extract().as(RegisterResponse.class));

        step("Check response", () -> {
            assertEquals(4, response.getId());
            assertNotNull(response.getToken());
        });
    }

    @Test
    @DisplayName("Unsuccessful registration with empty body")
    void unsuccessfulRegister400Test() {
        ErrorResponse response = step("Make registration request with empty body", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponse.class));

        step("Check error message", () ->
                assertEquals("Missing email or username", response.getError()));
    }

    @Test
    @DisplayName("Registration with non-existent user")
    void userNotFoundRegisterTest() {
        RegisterRequest registerData = new RegisterRequest("not.exist@reqres.in", "pistol");

        ErrorResponse response = step("Make registration request with non-existent user", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponse.class));

        step("Check error message", () ->
                assertEquals("Note: Only defined users succeed registration", response.getError()));
    }

    @Test
    @DisplayName("Registration with missing password")
    void missingPasswordRegisterTest() {
        RegisterRequest registerData = new RegisterRequest("eve.holt@reqres.in", null);

        ErrorResponse response = step("Make registration request without password", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponse.class));

        step("Check error message", () ->
                assertEquals("Missing password", response.getError()));
    }

    @Test
    @DisplayName("Registration with missing email")
    void missingEmailRegisterTest() {
        RegisterRequest registerData = new RegisterRequest(null, "pistol");

        ErrorResponse response = step("Make registration request without email", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponse.class));

        step("Check error message", () ->
                assertEquals("Missing email or username", response.getError()));
    }

    @Test
    @DisplayName("Registration with wrong body format")
    void wrongBodyRegisterTest() {
        step("Make registration request with invalid body format", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body("{%}")
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec));
    }

    @Test
    @DisplayName("Registration with unsupported media type")
    void unsupportedMediaTypeRegisterTest() {
        step("Make registration request without content type", () ->
                given(registerRequestWithoutContentType)
                        .header("x-api-key", API_KEY)
                        .post("/register")
                        .then()
                        .spec(unsupportedMediaTypeResponseSpec));
    }

    @Test
    @DisplayName("Get users list")
    void getUsersListTest() {
        UsersListResponse response = step("Get users list", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .queryParam("page", "2")
                        .get("/users")
                        .then()
                        .spec(registerResponseSpec)
                        .extract().as(UsersListResponse.class));

        step("Check response data", () -> {
            assertEquals(2, response.getPage());
            assertEquals(6, response.getData().size());
            assertEquals(7, response.getData().get(0).getId());
        });
    }

    @Test
    @DisplayName("Get single user")
    void getSingleUserTest() {
        UserData response = step("Get single user", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .get("/users/2")
                        .then()
                        .spec(registerResponseSpec)
                        .extract().jsonPath().getObject("data", UserData.class));

        step("Check user data", () -> {
            assertEquals(2, response.getId());
            assertEquals("janet.weaver@reqres.in", response.getEmail());
        });
    }

    @Test
    @DisplayName("Get non-existent user")
    void getUserNotFoundTest() {
        step("Get non-existent user", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .get("/users/999")
                        .then()
                        .spec(notFoundResponseSpec));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUserTest() {
        step("Delete user", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .delete("/users/2")
                        .then()
                        .spec(noContentResponseSpec));
    }
}