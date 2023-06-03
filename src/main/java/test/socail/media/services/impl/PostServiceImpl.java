package test.socail.media.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.socail.media.config.jwt.JwtUtils;
import test.socail.media.db.model.Post;
import test.socail.media.db.repository.PostRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.NotAllowedError;
import test.socail.media.services.PostService;
import test.social.media.dto.PostCreateRequest;
import test.social.media.dto.PostDto;


import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository repository;
    private final JwtUtils jwtUtils;

    @Override
    public PostDto changePostById(String authorization, UUID id, PostDto postDto) {
        Post post = repository.findById(id).orElseThrow(() -> new EntityNotFoundError("Post with id " + id + " does not exist"));
        String initiator = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        if(initiator.equals(post.getAuthor())) {
            post.setText(postDto.getText());
            post.setTitle(postDto.getTitle());
            post.setImage(postDto.getImage());
            repository.save(post);
            return mapToPostDto(post);
        }
        else {
            throw new NotAllowedError("You can't change someone else's post");
        }
    }

    @Override
    public void createPost(String authorization, PostCreateRequest postCreateRequest) {
        Post post = new Post();
        post.setText(postCreateRequest.getText());
        post.setTitle(postCreateRequest.getTitle());
        if(postCreateRequest.getImage() != null) {
            post.setImage(postCreateRequest.getImage().getBytes());
        }
        post.setPostCreationTime(Instant.now());
        post.setAuthor(jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ","")));
        repository.save(post);
    }

    @Override
    public void deletePostById(String authorization, UUID id) {
        Post post = repository.findById(id).orElseThrow(() -> new EntityNotFoundError("Post with id " + id + " does not exist"));
        String initiator = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        if(initiator.equals(post.getAuthor())) {
            repository.deleteById(id);
        }
        else {
            throw new NotAllowedError("You can't delete someone else's post");
        }
    }

    @Override
    public PostDto getPostById(UUID id) {
        Post post = repository.findById(id).orElseThrow(() -> new EntityNotFoundError("Post with id " + id + " does not exist"));
        return mapToPostDto(post);
    }

    private PostDto mapToPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setText(post.getText());
        postDto.setTitle(post.getTitle());
        postDto.setImage(post.getImage());
        postDto.setAuthor(post.getAuthor());
        postDto.setPostCreationTime(String.valueOf(post.getPostCreationTime()));
        return postDto;
    }
}
