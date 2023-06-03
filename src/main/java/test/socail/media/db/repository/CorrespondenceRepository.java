package test.socail.media.db.repository;

import org.springframework.data.repository.CrudRepository;
import test.socail.media.db.model.Correspondence;

import java.util.List;
import java.util.UUID;

public interface CorrespondenceRepository extends CrudRepository<Correspondence, UUID> {
    List<Correspondence> findByChatId(UUID chatId);

    Boolean existsByChatId(UUID chatId);
}
