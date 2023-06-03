package test.socail.media.db.repository;

import org.springframework.data.repository.CrudRepository;
import test.socail.media.db.model.Post;

import java.util.UUID;

public interface PostRepository extends CrudRepository<Post, UUID> {
}
