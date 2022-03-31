package googliapparatus.dto;

import java.util.List;
import java.util.Map;

public class GoogliResponseDto {
    private final Map<String, Integer> similarResults;

    private final List<SongDto> songs;

    public GoogliResponseDto(List<SongDto> songs, Map<String, Integer> similarResults) {
        this.songs = songs;
        this.similarResults = similarResults;
    }

    public Map<String, Integer> getSimilarResults() {
        return similarResults;
    }

    public List<SongDto> getSongs() {
        return songs;
    }
}
