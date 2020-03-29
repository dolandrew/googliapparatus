package googliapparatus.dto;

import java.util.List;

public class GoogliResponseDTO {
    private List<SongDTO> songs;
    private Counter counter;

    public GoogliResponseDTO(List<SongDTO> songs, Counter counter) {
        this.songs = songs;
        this.counter = counter;
    }

    public List<SongDTO> getSongs() {
        return songs;
    }

    public Counter getCounter() {
        return counter;
    }
}
