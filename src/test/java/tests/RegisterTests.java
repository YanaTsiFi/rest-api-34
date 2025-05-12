package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class RegisterTests {

    private static final String API_KEY = "reqres-free-v1";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    void successfulRegisterTest() {
        String registerData = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";

        given()
                .header("x-api-key", API_KEY)
                .body(registerData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("id", is(4))
                .body("token", notNullValue());
    }

    @Test
    void unsuccessfulRegister400Test() {
        String registerData = "";

        given()
                .header("x-api-key", API_KEY)
                .body(registerData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }

    @Test
    void userNotFoundRegisterTest() {
        String registerData = "{\"email\": \"not.exist@reqres.in\", \"password\": \"pistol\"}";

        given()
                .header("x-api-key", API_KEY)
                .body(registerData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Note: Only defined users succeed registration"));
    }

    @Test
    void missingPasswordRegisterTest() {
        String registerData = "{\"email\": \"eve.holt@reqres.in\"}";

        given()
                .header("x-api-key", API_KEY)
                .body(registerData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    void missingEmailRegisterTest() {
        String registerData = "{\"password\": \"pistol\"}";

        given()
                .header("x-api-key", API_KEY)
                .body(registerData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }

    @Test
    void wrongBodyRegisterTest() {
        String registerData = "{%}";

        given()
                .header("x-api-key", API_KEY)
                .body(registerData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400);
    }

    @Test
    void unsupportedMediaTypeRegisterTest() {
        given()
                .header("x-api-key", API_KEY)
                .log().uri()

                .when()
                .post("/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(415);
    }

    @Test
    void getUsersListTest() {
        given()
                .header("x-api-key", API_KEY)
                .queryParam("page", "2")
                .log().uri()

                .when()
                .get("/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("page", is(2))
                .body("data", hasSize(6))
                .body("data[0].id", is(7));
    }

    @Test
    void getSingleUserTest() {
        given()
                .header("x-api-key", API_KEY)
                .log().uri()

                .when()
                .get("/users/2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", is(2))
                .body("data.email", equalTo("janet.weaver@reqres.in"));
    }

    @Test
    void getUserNotFoundTest() {
        given()
                .header("x-api-key", API_KEY)
                .log().uri()

                .when()
                .get("/users/999")

                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    void deleteUserTest() {
        given()
                .header("x-api-key", API_KEY)
                .log().uri()

                .when()
                .delete("/users/2")

                .then()
                .log().status()
                .statusCode(204);
    }
}