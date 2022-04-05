package googliapparatus.service;

import googliapparatus.WordsApiConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

@Service
public class WordsApiProxyService {
    private final GoogliTweeter googliTweeter;

    private final RestTemplate restTemplate;

    private final WordsApiConfig wordsApiConfig;

    public WordsApiProxyService(GoogliTweeter googliTweeter, RestTemplate restTemplate, WordsApiConfig wordsApiConfig) {
        this.googliTweeter = googliTweeter;
        this.restTemplate = restTemplate;
        this.wordsApiConfig = wordsApiConfig;
    }

    public Set<String> findSimilarWords(String query) {
        try {
            query = query.replaceAll("\"", "");
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-RapidAPI-Host", "wordsapiv1.p.rapidapi.com");
            headers.add("X-RapidAPI-Key", "5bbfaec07amsh687a8d6af42b63fp1fca43jsne4a81f454fa2");
            String url = "https://wordsapiv1.p.rapidapi.com/words/" + query;
            HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
            var response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
            Set<String> list = new HashSet<>();
            List<LinkedHashMap> results = (List<LinkedHashMap>) response.getBody().get("results");
            for (LinkedHashMap result : results) {
                extracted(list, result, "similarTo");
                extracted(list, result, "typeOf");
                extracted(list, result, "pertainsTo");
                extracted(list, result, "also");
                extracted(list, result, "hasParts");
            }
            list.remove(query);
            return list;
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception from WordsAPI: " + e.getCause());
            return emptySet();
        }
    }

    private void extracted(Set<String> list, LinkedHashMap result, String key) {
        List<String> words = (List<String>) result.get(key);
        if (words != null) {
            list.addAll(words);
        }
    }
}