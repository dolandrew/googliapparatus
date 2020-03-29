package googliapparatus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import googliapparatus.dto.Counter;
import googliapparatus.dto.GoogliResponseDTO;
import googliapparatus.dto.SongDTO;
import googliapparatus.entity.SongEntity;
import googliapparatus.helper.SnippetHelper;
import googliapparatus.repository.SongEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
@EnableScheduling
public class SearchController {

    @Autowired
    private SongEntityRepository songEntityRepository;

    private Counter counter = new Counter();

    @Scheduled(initialDelay = 0L, fixedDelay = 30000)
    public void clearOutOldData() {
        counter.clearOutOldSessions();
        counter.clearOutOldSearches();
    }

    @GetMapping("/lyrics")
    public GoogliResponseDTO searchLyrics(String uuid, String filter) {
        counter.session(uuid);
        if (filterIsEmpty(filter)) {
            return new GoogliResponseDTO(emptyList(), counter);
        }
        filter = filter.trim().toLowerCase();
        List<SongEntity> songEntities = songEntityRepository.findByLyricsContainsOrNameLowerContains(filter, filter);
        List<SongDTO> songs = new ArrayList<>();
        SnippetHelper snippetHelper = new SnippetHelper();
        ObjectMapper objectMapper = new ObjectMapper();
        for (SongEntity songEntity : songEntities) {
            SongDTO songDTO = objectMapper.convertValue(songEntity, SongDTO.class);
            songs.add(songDTO);
            snippetHelper.findRelevantLyrics(filter, songEntity, songDTO);
        }
        counter.search();
        songs.sort(Comparator.comparing(SongDTO::getName));
        return new GoogliResponseDTO(songs, counter);
    }

    private boolean filterIsEmpty(String filter) {
        return filter == null || filter == "";
    }
}