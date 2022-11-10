package googliapparatus.repository;

import googliapparatus.entity.SongEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongEntityRepository extends CrudRepository<SongEntity,
        String> {
    List<SongEntity> findAllByName(String name);
    List<SongEntity> findByLyricsContainsOrNameLowerContains(String lyrics,
                                                             String nameLower);
}
