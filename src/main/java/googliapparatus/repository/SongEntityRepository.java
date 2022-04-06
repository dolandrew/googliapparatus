package googliapparatus.repository;

import googliapparatus.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongEntityRepository extends JpaRepository<SongEntity, String> {
    List<SongEntity> findAllByName(String name);

    List<SongEntity> findByLyricsContainsOrNameLowerContains(@Param("lyrics") String lyrics, @Param("nameLower") String nameLower);
}
