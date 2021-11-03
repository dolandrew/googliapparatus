package googliapparatus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import googliapparatus.dto.Counter;
import googliapparatus.dto.GoogliResponseDTO;
import googliapparatus.dto.SongDTO;
import googliapparatus.entity.SongEntity;
import googliapparatus.helper.SnippetHelper;
import googliapparatus.repository.SongEntityRepository;
import googliapparatus.service.GoogliTweeter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.jsoup.internal.StringUtil.isBlank;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
@EnableScheduling
public class SearchController {

    private final SongEntityRepository songEntityRepository;

    private final GoogliTweeter googliTweeter;

    private Counter counter = new Counter();

    private Logger log = LoggerFactory.getLogger(SearchController.class);

    public SearchController(SongEntityRepository songEntityRepository, GoogliTweeter googliTweeter) {
        this.songEntityRepository = songEntityRepository;
        this.googliTweeter = googliTweeter;
    }

    @Scheduled(fixedDelay = 30000)
    public void clearOutOldData() {
        counter.clearOutOldSessions();
        counter.clearOutOldSearches();
    }

    @Scheduled(fixedDelay = 900000)
    public void logSearchesTodayEveryQuarterHour() {
        log.info("searches today: " + counter.getSearchesPerDay());
        log.info("visits today: " + counter.getVisitsToday());
    }

    @GetMapping("/lyrics")
    public GoogliResponseDTO searchLyrics(String uuid, String filter) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        List<SongDTO> songs = new ArrayList<>();
        try {
            if (isBlank(uuid)) {
                return new GoogliResponseDTO(emptyList(), counter);
            }
            counter.session(uuid);
            if (filterIsEmpty(filter)) {
                return new GoogliResponseDTO(emptyList(), counter);
            }
            log.info("search term: " + filter);

            filter = filter.trim().toLowerCase();
            List<SongEntity> songEntities = songEntityRepository.findByLyricsContainsOrNameLowerContains(filter, filter);
            SnippetHelper snippetHelper = new SnippetHelper();
            ObjectMapper objectMapper = new ObjectMapper();
            for (SongEntity songEntity : songEntities) {
                SongDTO songDTO = objectMapper.convertValue(songEntity, SongDTO.class);
                songs.add(songDTO);
                snippetHelper.findRelevantLyrics(filter, songEntity, songDTO);
            }
            counter.search();
            songs.sort(Comparator.comparing(SongDTO::getName));

            googliTweeter.tweet("\"" + filter + "\" returned " + songs.size() + " results\n\n" + System.currentTimeMillis());
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception during search: " + e.getCause());
        }
        return new GoogliResponseDTO(songs, counter);
    }

    private boolean filterIsEmpty(String filter) {
        return filter == null || filter == "";
    }
}