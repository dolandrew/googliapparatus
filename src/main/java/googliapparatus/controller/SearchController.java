package googliapparatus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import googliapparatus.dto.GoogliResponseDto;
import googliapparatus.dto.SimilarResult;
import googliapparatus.dto.SongDto;
import googliapparatus.entity.SongEntity;
import googliapparatus.service.GoogliTweeter;
import googliapparatus.service.QueryService;
import googliapparatus.service.SimilarResultsService;
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
import java.util.List;

import static googliapparatus.helper.SnippetHelper.findRelevantLyrics;
import static java.util.Comparator.comparing;

@RestController
@CrossOrigin
public class SearchController {
    private final GoogliTweeter googliTweeter;

    private final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final QueryService queryService;

    private final SimilarResultsService similarResultsService;

    public SearchController(GoogliTweeter googliTweeter, QueryService queryService, SimilarResultsService similarResultsService) {
        this.queryService = queryService;
        this.googliTweeter = googliTweeter;
        this.similarResultsService = similarResultsService;
    }

    @GetMapping(path = "/api/search/lyrics", produces = "application/json")
    public GoogliResponseDto searchLyrics(@RequestParam String filter, @RequestParam(required = false, defaultValue = "true") Boolean similar, @RequestParam(required = false) boolean wholeWord) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        List<SongDto> songs = new ArrayList<>();
        List<SimilarResult> similarResults = new ArrayList<>();
        try {
            if (filterIsEmpty(filter)) {
                return new GoogliResponseDto(songs, similarResults);
            }
            log.info("search term: " + filter);

            filter = filter.trim().toLowerCase();
            if (wholeWord) {
                filter = " " + filter + " ";
            }
            List<SongEntity> songEntities = queryService.query(filter);
            for (SongEntity songEntity : songEntities) {
                SongDto songDTO = objectMapper.convertValue(songEntity, SongDto.class);
                songs.add(songDTO);
                findRelevantLyrics(filter, songEntity, songDTO);
            }
            songs.sort(comparing(SongDto::getName));

            similarResults.addAll(similarResultsService.getSimilarResults(filter));
            similarResults.sort(comparing(SimilarResult::getCount).reversed());

            tweetResultsAsync(filter, songs);
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception during search: " + e.getCause() + ": " + e.getMessage());
        }
        return new GoogliResponseDto(songs, similarResults);
    }

    private boolean filterIsEmpty(String filter) {
        return filter == null || filter.equals("");
    }

    private void tweetResultsAsync(String filter, List<SongDto> songs) {
        ((Runnable) () -> googliTweeter.tweetResults(filter, songs)).run();
    }
}