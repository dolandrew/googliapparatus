package googliapparatus.service;

import googliapparatus.WordsApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

class WordsApiProxyServiceTest {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private GoogliTweeter googliTweeter;

    @Autowired
    private WordsApiConfig wordsApiConfig;

    private final WordsApiProxyService underTest = new WordsApiProxyService(googliTweeter, restTemplate, wordsApiConfig);

    @Test
    void checkForLyrics() {
        underTest.findSimilarWords("bloody");
    }
}