package googliapparatus.controller;

import googliapparatus.dto.GoogliResponseDto;
import googliapparatus.service.GoogliTweeter;
import googliapparatus.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

@RestController
@CrossOrigin
public class SearchController {
    private final GoogliTweeter googliTweeter;

    private final SearchService searchService;

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    public SearchController(GoogliTweeter googliTweeter,
                            SearchService searchService) {
        this.googliTweeter = googliTweeter;
        this.searchService = searchService;
    }

    @GetMapping("/api/search/lyrics")
    public GoogliResponseDto searchLyrics(@RequestParam String filter,
                                          @RequestParam(required = false, defaultValue = "true") Boolean similar,
                                          @RequestParam(required = false) boolean wholeWord) {
        try {
            return searchService.search(filter, similar, wholeWord);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            googliTweeter.tweet("GoogliApparatus caught exception during search: " + e.getCause() + ": " + e.getMessage());
        }
        return new GoogliResponseDto(emptyList(), emptyList());
    }
}