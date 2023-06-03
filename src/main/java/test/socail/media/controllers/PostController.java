package test.socail.media.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.socail.media.services.PostService;
import test.social.media.controller.PostApi;
import test.social.media.dto.PostCreateRequest;
import test.social.media.dto.PostDto;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PostController implements PostApi{

    private final PostService postService;

    @Override
    public ResponseEntity<PostDto> changePostById(String authorization, UUID id, PostDto postDto) {
        PostDto newPostDto = postService.changePostById(authorization, id, postDto);
        return new ResponseEntity<>(newPostDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> createPost(String authorization, PostCreateRequest postCreateRequest) {
        postService.createPost(authorization, postCreateRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePostById(String authorization, UUID id) {
        postService.deletePostById(authorization, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<PostDto> getPostById(String authorization, UUID id) {
        PostDto postDto = postService.getPostById(id);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }
}