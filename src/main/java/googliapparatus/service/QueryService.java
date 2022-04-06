package googliapparatus.service;

import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class QueryService {
    private final SongEntityRepository songEntityRepository;

    public QueryService(SongEntityRepository songEntityRepository) {
        this.songEntityRepository = songEntityRepository;
    }

    @Transactional(readOnly = true)
    public List<SongEntity> query(String filter) {
        return songEntityRepository.findByLyricsContainsOrNameLowerContains(filter, filter);
    }
}
