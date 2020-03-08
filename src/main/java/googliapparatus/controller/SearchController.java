package googliapparatus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import googliapparatus.dto.SongDTO;
import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
public class SearchController {

    @Autowired
    private SongEntityRepository songEntityRepository;

    @GetMapping("/lyrics")
    public List<SongDTO> searchLyrics(String filter) {
        List<SongEntity> songEntities = songEntityRepository.findByLyricsContains(validateAndSanitizeFilter(filter));
        List<SongDTO> songs = new ArrayList<>();
        for (SongEntity songEntity : songEntities) {
            SongDTO songDTO = new ObjectMapper().convertValue(songEntity, SongDTO.class);
            songs.add(songDTO);
        }
        return songs;
    }

    private String validateAndSanitizeFilter(String filter) {
        if (filter == null) {
            filter = "";
        }
        filter = filter.trim().toLowerCase();
        return filter;
    }
}