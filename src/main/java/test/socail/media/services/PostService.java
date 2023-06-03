package test.socail.media.services;

import test.social.media.dto.PostCreateRequest;
import test.social.media.dto.PostDto;

import java.util.UUID;

public interface PostService {
    PostDto changePostById(String authorization, UUID id, PostDto postDto);
    void createPost(String authorization, PostCreateRequest postCreateRequest);
    void deletePostById(String authorization, UUID id);
    PostDto getPostById(UUID id);
}
