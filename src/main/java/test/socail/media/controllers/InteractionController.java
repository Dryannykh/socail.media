package test.socail.media.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.socail.media.services.InteractionService;
import test.social.media.controller.InteractionApi;
import test.social.media.dto.Author;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InteractionController implements InteractionApi {

    private final InteractionService interactionService;

    @Override
    public ResponseEntity<Void> acceptFriendRequest(String authorization, UUID requestId) {
        interactionService.acceptFriendRequest(authorization, requestId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> declineFriendRequest(String authorization, UUID requestId) {
        interactionService.declineFriendRequest(authorization, requestId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> getCorrespondence(String authorization, UUID chatId) {
        String messages = interactionService.getCorrespondence(authorization, chatId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> subscribeToAuthor(String authorization, Author author) {
        interactionService.subscribeToAuthor(authorization, author.getAuthor());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> unsubscribeFromAuthor(String authorization, Author author) {
        interactionService.unsubscribeFromAuthor(authorization, author.getAuthor());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}