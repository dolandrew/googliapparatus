package googliapparatus.controller;

import googliapparatus.GoogliApparatusApplication;
import googliapparatus.service.GoogliTweeter;
import googliapparatus.service.SongLoader;
import io.restassured.RestAssured;
import org.hibernate.SessionFactory;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.internal.SessionFactoryBuilderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        classes = GoogliApparatusApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(SpringExtension.class)
@EnableTransactionManagement
@MockBeans({@MockBean(GoogliTweeter.class), @MockBean(SongLoader.class)})
public class SearchControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    private HibernateTransactionManager transactionManager = new HibernateTransactionManager();

    @BeforeEach
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = serverPort;
    }

//    @Test
//    public void testSearchLyrics_emptyFilter() {
//        with().queryParam("filter", "")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200)
//                .body("songs.size()", is(0))
//                .when().get("/api/search/lyrics");
//    }
//
//    @Test
//    public void testSearchLyrics_filterContainsSpaces() {
//        with().queryParam("filter", " land ")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200)
//                .and().body("songs.name",
//                        hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
//                .body("songs.size()", is(56))
//                .body("songs.link", everyItem(is(notNullValue())))
//                .body("songs.name", everyItem(is(notNullValue())))
//                .body("songs.lyricSnippets", hasItem(containsInAnyOrder("earthward till she <b>land</b>ed in the nasty part...")))
//                .when().get("/api/search/lyrics");
//    }
//
//    @Test
//    public void testSearchLyrics_filterIsNotCaseSensitive() {
//        with().queryParam("filter", "LaNd")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200)
//                .and().body("songs.name",
//                        hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
//                .body("songs.size()", is(56))
//                .body("songs.link", everyItem(is(notNullValue())))
//                .body("songs.name", everyItem(is(notNullValue())))
//                .body("songs.lyricSnippets", hasItems(containsInAnyOrder("earthward till she <b>land</b>ed in the nasty part..."),
//                        containsInAnyOrder("to his knees, sees s<b>land</b>er on wrap paper tie..."),
//                        containsInAnyOrder("car and cruise the <b>land</b> of the brave and fr...")))
//                .when().get("/api/search/lyrics");
//    }
//
//    @Test
//    public void testSearchLyrics_hashtagNoFilter() {
//        expect().statusCode(400)
//                .body("error", is("Bad Request"))
//                .when().get("/api/search/lyrics");
//    }
//
//    @Test
//    public void testSearchLyrics_withFilter() {
//        with().queryParam("filter", "land")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200).and()
//                .body("songs.name",
//                        hasItems("The Lizards", "Esther", "The Mango Song", "Roses Are Free", "Limb By Limb", "Sand"))
//                .body("songs.size()", is(56))
//                .body("songs.link", everyItem(is(notNullValue())))
//                .body("songs.name", everyItem(is(notNullValue())))
//                .body("songs.lyricSnippets",
//                        hasItems(containsInAnyOrder("earthward till she <b>land</b>ed in the nasty part..."),
//                                containsInAnyOrder("to his knees, sees s<b>land</b>er on wrap paper tie..."),
//                                containsInAnyOrder("car and cruise the <b>land</b> of the brave and fr...")))
//                .when().get("/api/search/lyrics");
//    }
//
//    @Test
//    public void testSearchLyrics_withFilterPhrase() {
//        with().queryParam("filter", "bereft of oar")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200)
//                .and().body("songs.name", hasItems("Guelah Papyrus"))
//                .body("songs.size()", is(1))
//                .body("songs.link", everyItem(is(notNullValue())))
//                .body("songs.name", everyItem(is(notNullValue())))
//                .body("songs.lyricSnippets", hasItems(contains("aboard a craft <b>bereft of oar</b> I rowed upstream to...")))
//                .when().get("/api/search/lyrics");
//    }
//
//    @Test
//    public void testSearchLyrics_withFilter_resultsAreAlphabetical() {
//        LinkedHashMap response = with().queryParam("filter", "will")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200)
//                .body("songs.size()", is(110))
//                .body("songs.link", everyItem(is(notNullValue())))
//                .body("songs.name", everyItem(is(notNullValue())))
//                .when().get("/api/search/lyrics").thenReturn().as(LinkedHashMap.class);
//        assertEquals("20-20 Vision", ((LinkedHashMap) ((List) response.get("songs")).get(0)).get("name"));
//        assertEquals("All of These Dreams", ((LinkedHashMap) ((List) response.get("songs")).get(1)).get("name"));
//        assertEquals("Amazing Grace", ((LinkedHashMap) ((List) response.get("songs")).get(2)).get("name"));
//    }
//
//    @Test
//    public void testSearchLyrics_withFilter_searchesBySongName() {
//        with().queryParam("filter", "you enjoy myself")
//                .queryParam("uuid", "some-uuid")
//                .expect().statusCode(200)
//                .and().body("songs.name", hasItems("You Enjoy Myself"))
//                .body("songs.size()", is(1))
//                .body("songs.link", everyItem(is(notNullValue())))
//                .body("songs.name", everyItem(is(notNullValue())))
//                .when().get("/api/search/lyrics");
//    }

    @Test
    public void testSearchLyrics_withFilter_searchesBySongName() {
        with().queryParam("filter", "you enjoy myself")
                .queryParam("uuid", "some-uuid")
                .expect().statusCode(200)
                .and().body("songs.name", hasItems("You Enjoy Myself"))
                .body("songs.size()", is(1))
                .body("songs.link", everyItem(is(notNullValue())))
                .body("songs.name", everyItem(is(notNullValue())))
                .when().get("/api/search/lyrics");
    }
}
