package googliapparatus.repository;

import googliapparatus.entity.SongEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongEntityRepository extends CrudRepository<SongEntity, String> {
    List<SongEntity> findAllByName(@Param("name") String name);

    List<SongEntity> findByLyricsContainsOrNameLowerContains(@Param("lyrics") String lyrics, @Param("nameLower") String nameLower);
}
