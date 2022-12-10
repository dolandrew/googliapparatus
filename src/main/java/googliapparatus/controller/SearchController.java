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

@RestController
@CrossOrigin
public final class SearchController {
    private static final Logger LOG = LoggerFactory.getLogger(
            SearchController.class);
    private final GoogliTweeter googliTweeter;
    private final SearchService searchService;

    public SearchController(final GoogliTweeter tweeter,
                            final SearchService service) {
        this.googliTweeter = tweeter;
        this.searchService = service;
    }

    @GetMapping("/api/search/lyrics")
    public GoogliResponseDto searchLyrics(@RequestParam final String filter) {
        try {
            return searchService.search(filter);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            googliTweeter.tweet("GoogliApparatus caught exception "
                    + "during search: " + e.getCause() + ": " + e.getMessage());
        }
        return new GoogliResponseDto(emptyList(), emptyList());
    }
}
