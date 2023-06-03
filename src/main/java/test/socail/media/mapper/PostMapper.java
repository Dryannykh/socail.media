package test.socail.media.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import test.socail.media.db.model.Post;
import test.social.media.dto.PostDtoList;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostDtoList mapToPostListDto(Page<Post> post);
}
