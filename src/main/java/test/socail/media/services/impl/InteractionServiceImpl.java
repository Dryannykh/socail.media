package test.socail.media.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.socail.media.config.jwt.JwtUtils;
import test.socail.media.db.model.Correspondence;
import test.socail.media.db.model.Interaction;
import test.socail.media.db.model.enums.Status;
import test.socail.media.db.repository.CorrespondenceRepository;
import test.socail.media.db.repository.InteractionRepository;
import test.socail.media.db.repository.UserRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.NotAllowedError;
import test.socail.media.services.InteractionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final CorrespondenceRepository correspondenceRepository;

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    @Override
    @Transactional
    public void acceptFriendRequest(String authorization, UUID requestId) {
        String subscriber = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        Interaction interaction = interactionRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundError("Request doesn't exist"));
        if(interaction.getAuthor().equals(subscriber)) {
            interaction.setStatus(Status.FRIEND);
            interactionRepository.save(interaction);
            Interaction friendInteraction = new Interaction();
            friendInteraction.setSender(subscriber);
            friendInteraction.setAuthor(interaction.getSender());
            friendInteraction.setStatus(Status.FRIEND);
            interactionRepository.save(friendInteraction);
        }
        else {
            throw new NotAllowedError("You can't decline stranger request");
        }
    }

    @Override
    public void declineFriendRequest(String authorization, UUID requestId) {
        String sender = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        Interaction interaction = interactionRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundError("Request doesn't exist"));
        if(interaction.getAuthor().equals(sender)) {
            interactionRepository.delete(interaction);
        }
        else {
            throw new NotAllowedError("You can't decline stranger request");
        }
    }

    @Override
    public void unsubscribeFromAuthor(String authorization, String authorUsername) {
        String senderUsername = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        if(!senderUsername.equals(authorUsername)) {
            userRepository.findByUsername(authorUsername).orElseThrow(() -> new EntityNotFoundError("The author does not exist"));
            Interaction interaction = interactionRepository.findBySenderAndAuthor(senderUsername, authorUsername).orElseThrow(() -> new EntityNotFoundError("You are not subscribe to the author"));
            if(interaction.getStatus().equals(Status.FRIEND) && interactionRepository.findBySenderAndAuthor(authorUsername, senderUsername).isPresent()) {
                Interaction friendInteraction = interactionRepository.findBySenderAndAuthor(authorUsername, senderUsername).get();
                friendInteraction.setStatus(Status.SUBSCRIBER);
                interactionRepository.save(friendInteraction);
            }
            interactionRepository.delete(interaction);
        }
        else {
            throw new NotAllowedError("You can't unsubscribe from yourself");
        }
    }

    @Override
    public void subscribeToAuthor(String authorization, String authorUsername) {
        String senderUsername = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        if(!senderUsername.equals(authorUsername)) {
            userRepository.findByUsername(authorUsername).orElseThrow(() -> new EntityNotFoundError("The author does not exist"));
            if(interactionRepository.findBySenderAndAuthor(senderUsername, authorUsername).isPresent()) {
                throw new NotAllowedError("You already subscribe to author");
            }
            Interaction interaction = new Interaction();
            interaction.setSender(senderUsername);
            interaction.setAuthor(authorUsername);
            interaction.setStatus(Status.SUBSCRIBER);
            interactionRepository.save(interaction);
        }
        else {
            throw new NotAllowedError("You can't follow yourself");
        }
    }

    @Override
    public String getCorrespondence(String authorization, UUID chatId) {
        String user = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        StringBuilder finalVersionCorrespondence = new StringBuilder();
        if(correspondenceRepository.existsByChatId(chatId)) {
            List<Correspondence> correspondences = correspondenceRepository.findByChatId(chatId);
            if(correspondences.get(0).getSender().equals(user) || correspondences.get(0).getRecipient().equals(user)) {
                for(Correspondence correspondence : correspondences) {
                    finalVersionCorrespondence.append("Отправитель: ").append(correspondence.getSender()).append("\n");
                    finalVersionCorrespondence.append("Получатель: ").append(correspondence.getRecipient()).append("\n");
                    finalVersionCorrespondence.append("Сообщение: ").append(correspondence.getMessage()).append("\n");

                }
                return finalVersionCorrespondence.toString();
            }
            else {
                throw new NotAllowedError("You can't read stranger chat");
            }
        }
        else {
            throw new EntityNotFoundError("The chat does not exist");
        }
    }
}
