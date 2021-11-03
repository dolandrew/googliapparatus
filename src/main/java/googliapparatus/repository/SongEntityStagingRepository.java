package googliapparatus.repository;

import googliapparatus.entity.SongEntityStaging;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongEntityStagingRepository extends CrudRepository<SongEntityStaging, String> {

    @Query("SELECT s from SongEntityStaging s")
    List<SongEntityStaging> getStagedSongs();
}
