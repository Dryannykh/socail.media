package test.socail.media.services;

import org.springframework.data.domain.Pageable;
import test.social.media.dto.PostDtoList;

public interface FeedService {
    PostDtoList getUserPosts(String authorization, Pageable pageable);
    PostDtoList getAuthorsPosts(String authorization, Pageable pageable);
}
