package googliapparatus.service;

import googliapparatus.WordsApiConfig;
import googliapparatus.dto.SimilarResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

@Service
public class SimilarResultsService {
    private final GoogliTweeter googliTweeter;

    private final QueryService queryService;

    private final RestTemplate restTemplate;

    private final WordsApiConfig wordsApiConfig;

    public SimilarResultsService(GoogliTweeter googliTweeter, RestTemplate restTemplate, WordsApiConfig wordsApiConfig, QueryService queryService) {
        this.googliTweeter = googliTweeter;
        this.restTemplate = restTemplate;
        this.wordsApiConfig = wordsApiConfig;
        this.queryService = queryService;
    }

    @Transactional(readOnly = true)
    public List<SimilarResult> getSimilarResults(String filter) {
        List<SimilarResult> similarResults = new ArrayList<>();
        Set<String> similarWords = findSimilarWords(filter);
        for (String word : similarWords) {
            word = word.trim().toLowerCase();
            int songCount = queryService.query(word).size();
            if (songCount > 0) {
                similarResults.add(new SimilarResult(songCount, word));
            }
        }
        return similarResults;
    }

    private Set<String> findSimilarWords(String query) {
        try {
            query = query.replaceAll("\"", "");
            ResponseEntity<LinkedHashMap> response = getWordsApiResponse(query);
            List<LinkedHashMap> results = (List<LinkedHashMap>) response.getBody().get("results");
            Set<String> list = new HashSet<>();
            for (LinkedHashMap result : results) {
                addIfNotNull(list, result, "similarTo");
                addIfNotNull(list, result, "typeOf");
                addIfNotNull(list, result, "pertainsTo");
                addIfNotNull(list, result, "also");
                addIfNotNull(list, result, "hasParts");
            }
            list.remove(query);
            return list;
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception from WordsAPI: " + e.getCause());
            return emptySet();
        }
    }

    private void addIfNotNull(Set<String> list, LinkedHashMap result, String category) {
        if (result.get(category) != null) {
            list.addAll((List<String>)result.get(category));
        }
    }

    private ResponseEntity<LinkedHashMap> getWordsApiResponse(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RapidAPI-Host", "wordsapiv1.p.rapidapi.com");
        headers.add("X-RapidAPI-Key", wordsApiConfig.getApiKey());
        String url = "https://wordsapiv1.p.rapidapi.com/words/" + query;
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
    }
}