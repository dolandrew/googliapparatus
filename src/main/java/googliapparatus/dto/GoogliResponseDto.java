package googliapparatus.dto;

import java.util.List;

public class GoogliResponseDto {
    private final List<SimilarResult> similarResults;

    private final List<SongDto> songs;

    public GoogliResponseDto(List<SongDto> songs, List<SimilarResult> similarResults) {
        this.songs = songs;
        this.similarResults = similarResults;
    }

    public List<SimilarResult> getSimilarResults() {
        return similarResults;
    }

    public List<SongDto> getSongs() {
        return songs;
    }
}
