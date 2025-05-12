package tests;

import io.restassured.RestAssured;
import models.lombok.*;
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
        RegisterRequestLombokModel registerData = new RegisterRequestLombokModel();
        registerData.setEmail("eve.holt@reqres.in");
        registerData.setPassword("pistol");

        RegisterResponseLombokModel response = step("Make registration request", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(registerResponseSpec)
                        .extract().as(RegisterResponseLombokModel.class));

        step("Check response", () -> {
            assertEquals(4, response.getId());
            assertNotNull(response.getToken());
        });
    }

    @Test
    @DisplayName("Unsuccessful registration with empty body")
    void unsuccessfulRegister400Test() {
        ErrorResponseLombokModel response = step("Make registration request with empty body", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponseLombokModel.class));

        step("Check error message", () ->
                assertEquals("Missing email or username", response.getError()));
    }

    @Test
    @DisplayName("Registration with non-existent user")
    void userNotFoundRegisterTest() {
        RegisterRequestLombokModel registerData = new RegisterRequestLombokModel();
        registerData.setEmail("not.exist@reqres.in");
        registerData.setPassword("pistol");

        ErrorResponseLombokModel response = step("Make registration request with non-existent user", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponseLombokModel.class));

        step("Check error message", () ->
                assertEquals("Note: Only defined users succeed registration", response.getError()));
    }

    @Test
    @DisplayName("Registration with missing password")
    void missingPasswordRegisterTest() {
        RegisterRequestLombokModel registerData = new RegisterRequestLombokModel();
        registerData.setEmail("eve.holt@reqres.in");

        ErrorResponseLombokModel response = step("Make registration request without password", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponseLombokModel.class));

        step("Check error message", () ->
                assertEquals("Missing password", response.getError()));
    }

    @Test
    @DisplayName("Registration with missing email")
    void missingEmailRegisterTest() {
        RegisterRequestLombokModel registerData = new RegisterRequestLombokModel();
        registerData.setPassword("pistol");

        ErrorResponseLombokModel response = step("Make registration request without email", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .body(registerData)
                        .post("/register")
                        .then()
                        .spec(missingFieldResponseSpec)
                        .extract().as(ErrorResponseLombokModel.class));

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
        UsersListResponseLombokModel response = step("Get users list", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .queryParam("page", "2")
                        .get("/users")
                        .then()
                        .spec(registerResponseSpec)
                        .extract().as(UsersListResponseLombokModel.class));

        step("Check response data", () -> {
            assertEquals(2, response.getPage());
            assertEquals(6, response.getData().size());
            assertEquals(7, response.getData().get(0).getId());
        });
    }

    @Test
    @DisplayName("Get single user")
    void getSingleUserTest() {
        UserDataLombokModel response = step("Get single user", () ->
                given(registerRequestSpec)
                        .header("x-api-key", API_KEY)
                        .get("/users/2")
                        .then()
                        .spec(registerResponseSpec)
                        .extract().jsonPath().getObject("data", UserDataLombokModel.class));

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