package googliapparatus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import googliapparatus.dto.GoogliResponseDto;
import googliapparatus.dto.SimilarResult;
import googliapparatus.dto.SongDto;
import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static googliapparatus.helper.SnippetHelper.findRelevantLyrics;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

@Service
public final class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(
            SearchService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SongEntityRepository songEntityRepository;

    private final WordsApiProxyService wordsApiProxyService;

    private final GoogliTweeter googliTweeter;

    public SearchService(final SongEntityRepository entityRepository,
                         final WordsApiProxyService apiProxyService,
                         final GoogliTweeter tweeter) {
        this.songEntityRepository = entityRepository;
        this.wordsApiProxyService = apiProxyService;
        this.googliTweeter = tweeter;
    }

    public GoogliResponseDto search(final String rawFilter) {
        boolean similar = false;
        boolean wholeWord = false;
        List<SongDto> songs = new ArrayList<>();
        List<SimilarResult> similarResults = new ArrayList<>();

        if (filterIsEmpty(rawFilter)) {
            return new GoogliResponseDto(emptyList(), emptyList());
        }
        LOG.info("search term: " + rawFilter);

        String filter = rawFilter.trim().toLowerCase();
        if (wholeWord) {
            filter = " " + filter + " ";
        }
        List<SongEntity> songEntities = songEntityRepository
                .findByLyricsContainsOrNameLowerContains(filter,
                        filter);
        for (SongEntity songEntity : songEntities) {
            var songDTO = objectMapper.convertValue(songEntity, SongDto.class);
            songs.add(songDTO);
            findRelevantLyrics(filter, songEntity, songDTO);
        }
        addSimilarResults(filter, similar, similarResults, songEntities);
        songs.sort(comparing(SongDto::getName));
        similarResults.sort(comparing(SimilarResult::count).reversed());

        tweetResultsAsync(filter, songs);
        return new GoogliResponseDto(songs, similarResults);
    }

    private void addSimilarResults(final String filter, final Boolean similar,
                                   final List<SimilarResult> similarResults,
                                   final List<SongEntity> songEntities) {
        if (songEntities.isEmpty() || similar) {
            Set<String> similarWords = wordsApiProxyService
                    .findSimilarWords(filter);
            for (String word : similarWords) {
                word = word.trim().toLowerCase();
                int songCount = songEntityRepository
                        .findByLyricsContainsOrNameLowerContains(word, word)
                        .size();
                if (songCount > 0) {
                    similarResults.add(new SimilarResult(songCount, word));
                }
            }
        }
    }

    private boolean filterIsEmpty(final String filter) {
        return filter == null || filter.equals("");
    }

    private void tweetResults(final String filter, final List<SongDto> songs) {
        String tweet = "\"" + filter + "\" returned " + songs.size()
                + " results";
        if (songs.size() > 0) {
            tweet += ":\n" + songs.get(0).getName();
        }
        if (songs.size() > 1) {
            tweet += "\n" + songs.get(1).getName();
        }
        if (songs.size() > 2) {
            tweet += "\n" + songs.get(2).getName();
        }
        if (songs.size() > 3) {
            tweet += "...";
        }
        googliTweeter.tweet(tweet);
    }

    private void tweetResultsAsync(final String filter,
                                   final List<SongDto> songs) {
        tweetResults(filter, songs);
    }
}
