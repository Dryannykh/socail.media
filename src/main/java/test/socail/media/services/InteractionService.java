package test.socail.media.services;

import java.util.UUID;

public interface InteractionService {
    void acceptFriendRequest(String authorization, UUID requestId);
    void declineFriendRequest(String authorization, UUID requestId);
    void unsubscribeFromAuthor(String authorization, String author);
    void subscribeToAuthor(String authorization, String author);
    String getCorrespondence(String authorization, UUID chatId);
}
