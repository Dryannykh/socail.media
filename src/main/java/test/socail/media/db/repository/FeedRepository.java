package test.socail.media.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import test.socail.media.db.model.Post;

import java.util.Collection;
import java.util.UUID;

public interface FeedRepository extends CrudRepository<Post, UUID>, PagingAndSortingRepository<Post, UUID> {
    Page<Post> findByAuthor(Pageable pageable, String author);

    @Query(value = "SELECT p FROM Post p WHERE p.author in :authorsList")
    Page<Post> findAllByAuthors(Pageable pageable, @Param("authorsList") Collection<String> authorsList);
}
