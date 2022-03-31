package googliapparatus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import googliapparatus.dto.GoogliResponseDto;
import googliapparatus.dto.SongDto;
import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import googliapparatus.service.GoogliTweeter;
import googliapparatus.service.WordsApiProxyService;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static googliapparatus.helper.SnippetHelper.findRelevantLyrics;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@RestController
@CrossOrigin
public class SearchController {
    private final GoogliTweeter googliTweeter;

    private final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SongEntityRepository songEntityRepository;

    private final WordsApiProxyService wordsApiProxyService;

    public SearchController(SongEntityRepository songEntityRepository, GoogliTweeter googliTweeter, WordsApiProxyService wordsApiProxyService) {
        this.songEntityRepository = songEntityRepository;
        this.googliTweeter = googliTweeter;
        this.wordsApiProxyService = wordsApiProxyService;
    }

    @GetMapping("/api/search/lyrics")
    public GoogliResponseDto searchLyrics(@RequestParam(required = false) String uuid, @RequestParam String filter, @RequestParam(required = false) Boolean similar) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        if (similar == null) {
            similar = true;
        }
        List<SongDto> songs = new ArrayList<>();
        Map<String, Integer> similarWordMap = new HashMap<>();
        try {
            if (filterIsEmpty(filter)) {
                return new GoogliResponseDto(emptyList(), emptyMap());
            }
            log.info("search term: " + filter);

            filter = filter.trim().toLowerCase();
            List<SongEntity> songEntities = songEntityRepository.findByLyricsContainsOrNameLowerContains(filter, filter);
            for (SongEntity songEntity : songEntities) {
                SongDto songDTO = objectMapper.convertValue(songEntity, SongDto.class);
                songs.add(songDTO);
                findRelevantLyrics(filter, songEntity, songDTO);
            }
            if (songEntities.isEmpty() || similar) {
                List<String> similarWords = wordsApiProxyService.findSimilarWords(filter);
                for (String word : similarWords) {
                    word = word.trim().toLowerCase();
                    int songCount = songEntityRepository.findByLyricsContainsOrNameLowerContains(word, word).size();
                    if (songCount > 0) {
                        similarWordMap.put(word, songCount);
                    }
                }
            }
            songs.sort(Comparator.comparing(SongDto::getName));

            String finalFilter = filter;
            ((Runnable) () -> tweetResults(finalFilter, songs)).run();
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception during search: " + e.getCause() + ": " + e.getMessage());
        }
        return new GoogliResponseDto(songs, similarWordMap);
    }

    private boolean filterIsEmpty(String filter) {
        return filter == null || filter.equals("");
    }

    private void tweetResults(String filter, List<SongDto> songs) {
        String theTweet = "\"" + filter + "\" returned " + songs.size() + " results";
        if (songs.size() > 0) {
            theTweet += ":\n" + songs.get(0).getName();
        }
        if (songs.size() > 1) {
            theTweet += "\n" + songs.get(1).getName();
        }
        if (songs.size() > 2) {
            theTweet += "\n" + songs.get(2).getName();
        }
        if (songs.size() > 3) {
            theTweet += "...";
        }
        googliTweeter.tweet(theTweet);
    }
}