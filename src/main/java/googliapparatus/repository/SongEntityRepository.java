package googliapparatus.repository;

import googliapparatus.SongEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongEntityRepository extends CrudRepository<SongEntity, String> {

}
