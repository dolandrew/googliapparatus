package googliapparatus.controller;

import googliapparatus.GoogliApparatusApplication;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        classes = GoogliApparatusApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RunWith(SpringRunner.class)
public class SearchControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Before
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = serverPort;
    }

    @Test
    public void testSearchLyrics_withFilter() {
        with().queryParam("filter", "land")
            .expect().statusCode(200)
            .and().body("name",
                hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
            .body("size()", is(54))
            .body("link", everyItem(is(notNullValue())))
            .body("name", everyItem(is(notNullValue())))
            .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_filterIsNotCaseSensitive() {
        with().queryParam("filter", "LaNd")
                .expect().statusCode(200)
                .and().body("name",
                hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
                .body("size()", is(54))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_withFilterPhrase() {
        with().queryParam("filter", "bereft of oar")
                .expect().statusCode(200)
                .and().body("name",
                hasItems("Guelah Papyrus"))
                .body("size()", is(1))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_filterContainsSpaces() {
        with().queryParam("filter", " land ")
                .expect().statusCode(200)
                .and().body("name",
                hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
                .body("size()", is(54))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_emptyFilter() {
        with().queryParam("filter", "")
                .expect().statusCode(200)
                .body("size()", is(566))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_hashtagNoFilter() {
        expect().statusCode(200)
                .body("size()", is(566))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }

}
