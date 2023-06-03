package test.socail.media.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.socail.media.services.FeedService;
import test.social.media.controller.FeedApi;
import test.social.media.dto.PostDtoList;

@RestController
@RequiredArgsConstructor
public class FeedController implements FeedApi {

    private final FeedService feedService;
    @Override
    public ResponseEntity<PostDtoList> getAuthorsPosts(String authorization, Pageable pageable) {
        PostDtoList postDtoList = feedService.getAuthorsPosts(authorization, pageable);
        return new ResponseEntity<>(postDtoList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostDtoList> getUserPosts(String authorization, Pageable pageable) {
        PostDtoList postDtoList = feedService.getUserPosts(authorization, pageable);
        return new ResponseEntity<>(postDtoList, HttpStatus.OK);
    }
}