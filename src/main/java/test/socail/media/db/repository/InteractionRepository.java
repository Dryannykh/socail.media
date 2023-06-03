package test.socail.media.db.repository;

import org.springframework.data.repository.CrudRepository;
import test.socail.media.db.model.Interaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InteractionRepository extends CrudRepository<Interaction, UUID> {

    Optional<Interaction> findBySenderAndAuthor(String sender, String author);

    Boolean existsBySender(String sender);

    List<Interaction> findBySender(String sender);
}
