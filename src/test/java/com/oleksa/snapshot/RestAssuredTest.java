package com.oleksa.snapshot;

//import static io.restassured.RestAssured.*;
//        io.restassured.matcher.RestAssuredMatchers.*
//        org.hamcrest.Matchers.*

import com.oleksa.snapshot.entity.Room;
import io.qameta.allure.AllureConstants;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class RestAssuredTest {

    @BeforeAll
    public static void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.port = 8888;
        List<Filter> filters = filters();
        ArrayList<Filter> filters1 = new ArrayList<>(filters);
        filters1.add(new AllureRestAssured());
        RestAssured.filters(filters1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "1234", "null", "", "ABC"})
    public void testGet500(String id) {

        Response response = given()
        .when()
                .get("https://localhost:8888/room/{id}", id);
        response.prettyPrint();
        response.then()
                .statusCode(500);
    }

    @Test
    public void testGet() {
        Response response = given()
            .when()
                .request()
                .baseUri("https://localhost")
                .pathParam("id", RoomController.FIRST)
                .get("/room/{id}");
        response.prettyPrint();
        response.then()
                .statusCode(200)
                .body("name", equalTo("first"),
                    "id", Matchers.matchesPattern(".{8}-.{4}-.{4}-.{4}-.{12}"),
                "location", nullValue());
        Room as = response.as(Room.class);
        assertThat(as).isEqualTo(Room.builder().id(RoomController.FIRST).name("first").build());
    }

    private static Arguments[] postNames() {
        return new Arguments[] {
                Arguments.of(Room.builder().name("Autotest" + RandomStringUtils.randomAlphanumeric(10)).build()),
                Arguments.of(Room.builder().name(Integer.toString(RandomUtils.nextInt())).build()),
                Arguments.of(Room.builder().name(null).build()),
        };
    }

    @ParameterizedTest
    @MethodSource("postNames")
    public void testPost(Room room) {
        Response response = given()
                .request()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"name\" : \"" + room.getName() + "\"" +
                        "}")
            .when()
                .request().log().all()
                .post("https://localhost/room/");
        response.prettyPrint();
        UUID id = response.as(UUID.class);
        response.then()
                .statusCode(200)
                .body(Matchers.matchesPattern("\".{8}-.{4}-.{4}-.{4}-.{12}\""))
                .time(lessThan(1L), TimeUnit.SECONDS);

        Response response2 = given()
                .when()
                .get("https://localhost/room/{id}", id);
        response2.prettyPrint();
        response2.then()
                .statusCode(200)
                .body("name", equalTo(room.getName()));
    }

    @Test
    public void testUpdate() {
        Response response = given()
                .request()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"name\" : \"Update\"" +
                        "}")
                .when()
                .post("https://localhost:8888/room/");
        response.prettyPrint();
        UUID id = response.as(UUID.class);
        response.then()
                .statusCode(200);

        Response response3 = given()
                .request()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"name\" : \"Updated\"" +
                        "}")
                .when()
                .patch("https://localhost:8888/room/{id}", id);
        response3.prettyPrint();
        response3.then()
                .statusCode(200);

        Response response2 = given()
                .when()
                .get("https://localhost:8888/room/{id}", id);
        response2.prettyPrint();
        response2.then()
                .statusCode(200)
                .body("name", equalTo("Updated"));
    }

    @Test
    public void testDelete() {
        Response response = given()
                .request()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"name\" : \"Delete\"" +
                        "}")
                .when()
                .post("https://localhost:8888/room/");
        response.prettyPrint();
        UUID id = response.as(UUID.class);
        response.then()
                .statusCode(200);

        Response response3 = given()
                .request()
                .param("roomId", id)
                .when()
                .delete("https://localhost:8888/room/");
        response3.prettyPrint();
        response3.then()
                .statusCode(200)
                .extract()
                .response();
        assertThat(response3.header("abc")).startsWith("xyz");

        Response response2 = given()
                .when()
                .get("https://localhost:8888/room/{id}", id);
        response2.prettyPrint();
        response2.then()
                .statusCode(200)
                .body("name", not(equalTo("Delete")));
    }
}
