package googliapparatus.controller;

import googliapparatus.GoogliApparatusApplication;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

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
            .body("size()", is(56))
            .body("link", everyItem(is(notNullValue())))
            .body("name", everyItem(is(notNullValue())))
                .body("lyricSnippets", hasItems(containsInAnyOrder("earthward till she <b>land</b>ed in the nasty part..."),
                        containsInAnyOrder("to his knees, sees s<b>land</b>er on wrap paper tie..."),
                        containsInAnyOrder("car and cruise the <b>land</b> of the brave and fr...")))
            .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_filterIsNotCaseSensitive() {
        with().queryParam("filter", "LaNd")
                .expect().statusCode(200)
                .and().body("name",
                    hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
                .body("size()", is(56))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .body("lyricSnippets", hasItems(containsInAnyOrder("earthward till she <b>land</b>ed in the nasty part..."),
                        containsInAnyOrder("to his knees, sees s<b>land</b>er on wrap paper tie..."),
                        containsInAnyOrder("car and cruise the <b>land</b> of the brave and fr...")))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_withFilterPhrase() {
        with().queryParam("filter", "bereft of oar")
                .expect().statusCode(200)
                .and().body("name", hasItems("Guelah Papyrus"))
                .body("size()", is(1))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .body("lyricSnippets", hasItems(contains("aboard a craft <b>bereft of oar</b> I rowed upstream to...")))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_withFilter_searchesBySongName() {
        with().queryParam("filter", "you enjoy myself")
                .expect().statusCode(200)
                .and().body("name", hasItems("You Enjoy Myself"))
                .body("size()", is(1))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_withFilter_resultsAreAlphabetical() {
        List<LinkedHashMap> response = with().queryParam("filter", "will")
                .expect().statusCode(200)
                .body("size()", is(110))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics").thenReturn().as(List.class);

        assertEquals("20-20 Vision", response.get(0).get("name"));
        assertEquals("All of These Dreams", response.get(1).get("name"));
        assertEquals("Amazing Grace", response.get(2).get("name"));

    }

    @Test
    public void testSearchLyrics_filterContainsSpaces() {
        with().queryParam("filter", " land ")
                .expect().statusCode(200)
                .and().body("name",
                    hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
                .body("size()", is(56))
                .body("link", everyItem(is(notNullValue())))
                .body("name", everyItem(is(notNullValue())))
                .body("lyricSnippets", hasItem(containsInAnyOrder("earthward till she <b>land</b>ed in the nasty part...")))

                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_emptyFilter() {
        with().queryParam("filter", "")
                .expect().statusCode(200)
                .body("size()", is(0))
                .when().get("/api/search/lyrics");
    }

    @Test
    public void testSearchLyrics_hashtagNoFilter() {
        expect().statusCode(200)
                .body("size()", is(0))
                .when().get("/api/search/lyrics");
    }

}
