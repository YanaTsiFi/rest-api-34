package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;

public class RegisterSpec {
    private static ResponseSpecification createResponseSpec(int statusCode, boolean logBody) {
        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(STATUS);

        if (logBody) {
            builder.log(BODY);
        }

        return builder.build();
    }

    public static RequestSpecification registerRequestSpec = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().body()
            .log().headers()
            .contentType(JSON)
            .basePath("/api");

    public static ResponseSpecification registerResponseSpec = createResponseSpec(200, true);
    public static ResponseSpecification missingFieldResponseSpec = createResponseSpec(400, true);
    public static ResponseSpecification notFoundResponseSpec = createResponseSpec(404, true);
    public static ResponseSpecification noContentResponseSpec = createResponseSpec(204, false);
    public static ResponseSpecification unsupportedMediaTypeResponseSpec = createResponseSpec(415, true);

    public static RequestSpecification registerRequestWithoutContentType = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().headers();
}