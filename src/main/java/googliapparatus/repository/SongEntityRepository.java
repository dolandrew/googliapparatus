package googliapparatus.repository;

import googliapparatus.entity.SongEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongEntityRepository extends CrudRepository<SongEntity, String> {
    List<SongEntity> findAllByNameLowerContains(String name);
    List<SongEntity> findAllByName(String name);
    List<SongEntity> findByLyricsContains(String lyrics);
}
