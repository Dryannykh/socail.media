package test.socail.media.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import test.socail.media.config.jwt.JwtUtils;
import test.socail.media.db.model.Correspondence;
import test.socail.media.db.model.Interaction;
import test.socail.media.db.model.User;
import test.socail.media.db.model.enums.Status;
import test.socail.media.db.repository.CorrespondenceRepository;
import test.socail.media.db.repository.InteractionRepository;
import test.socail.media.db.repository.UserRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.NotAllowedError;
import test.socail.media.services.InteractionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InteractionServiceTest {

    @Autowired
    private InteractionService interactionService;

    @MockBean
    private InteractionRepository interactionRepository;

    @MockBean
    private CorrespondenceRepository correspondenceRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtils jwtUtils;

    private MockitoSession session;

    String authorization = "authorization";

    UUID chatId = UUID.randomUUID();

    @BeforeEach
    public void init() {
        session = Mockito.mockitoSession()
                .initMocks(this)
                .startMocking();
    }

    @AfterEach
    public void closeSession() {
        session.finishMocking();
    }

    @Test
    public void acceptFriendRequest() {
        UUID requestId = UUID.randomUUID();
        Interaction interaction = new Interaction();
        interaction.setSender("2");
        interaction.setAuthor("1");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        when(interactionRepository.findById(requestId)).thenReturn(Optional.of(interaction));
        interactionService.acceptFriendRequest(authorization, requestId);
        verify(interactionRepository, times(2)).save(any());
    }

    @Test
    public void acceptStrangerFriendRequest() {
        UUID requestId = UUID.randomUUID();
        Interaction interaction = new Interaction();
        interaction.setSender("2");
        interaction.setAuthor("1");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("5");
        when(interactionRepository.findById(requestId)).thenReturn(Optional.of(interaction));
        assertThrows(NotAllowedError.class, () -> interactionService.acceptFriendRequest(authorization, requestId));
    }

    @Test
    public void acceptOrDeclineNotExistentFriendRequest() {
        when(interactionRepository.findById(any())).thenThrow(EntityNotFoundError.class);
        assertThrows(EntityNotFoundError.class, () -> interactionService.acceptFriendRequest(authorization, any()));
        assertThrows(EntityNotFoundError.class, () -> interactionService.declineFriendRequest(authorization, any()));
    }

    @Test
    public void declineFriendRequest() {
        UUID requestId = UUID.randomUUID();
        Interaction interaction = new Interaction();
        interaction.setSender("2");
        interaction.setAuthor("1");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        when(interactionRepository.findById(requestId)).thenReturn(Optional.of(interaction));
        interactionService.declineFriendRequest(authorization, requestId);
        verify(interactionRepository, times(1)).delete(any());
    }

    @Test
    public void declineStrangerFriendRequest() {
        UUID requestId = UUID.randomUUID();
        Interaction interaction = new Interaction();
        interaction.setSender("2");
        interaction.setAuthor("1");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("5");
        when(interactionRepository.findById(requestId)).thenReturn(Optional.of(interaction));
        assertThrows(NotAllowedError.class, () -> interactionService.declineFriendRequest(authorization, requestId));
    }

    @Test
    public void unsubscribeFromFriendAuthor() {
        User user = new User();
        user.setUsername("1");
        Interaction interaction = new Interaction();
        interaction.setSender("2");
        interaction.setAuthor("1");
        interaction.setStatus(Status.SUBSCRIBER);
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("2");
        when(userRepository.findByUsername("1")).thenReturn(Optional.of(user));
        when(interactionRepository.findBySenderAndAuthor("2", "1")).thenReturn(Optional.of(interaction));
        interactionService.unsubscribeFromAuthor(authorization, "1");
        verify(interactionRepository, times(1)).delete(any());
    }

    @Test
    public void subscribeToAuthor() {
        UUID requestId = UUID.randomUUID();
        User user = new User();
        user.setUsername("2");
        Interaction interaction = new Interaction();
        interaction.setSender("1");
        interaction.setAuthor("2");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        when(userRepository.findByUsername("2")).thenReturn(Optional.of(user));
        when(interactionRepository.findById(requestId)).thenReturn(null);
        when(interactionRepository.findBySenderAndAuthor("2", "1")).thenReturn(Optional.of(interaction));
        interactionService.subscribeToAuthor(authorization, "2");
        verify(interactionRepository, times(1)).save(any());
    }

    @Test
    public void subscribeAgainToAuthor() {
        User user = new User();
        user.setUsername("2");
        Interaction interaction = new Interaction();
        interaction.setSender("1");
        interaction.setAuthor("2");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        when(userRepository.findByUsername("2")).thenReturn(Optional.of(user));
        when(interactionRepository.findBySenderAndAuthor("1", "2")).thenReturn(Optional.of(interaction));
        assertThrows(NotAllowedError.class, () -> interactionService.subscribeToAuthor(authorization, "2"));
    }

    @Test
    public void getCorrespondence() {
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        when(correspondenceRepository.existsByChatId(chatId)).thenReturn(true);
        String correspondence = "Отправитель: 1\nПолучатель: 2\nСообщение: Привет\nОтправитель: 2\nПолучатель: 1\nСообщение: Привет!\n";
        when(correspondenceRepository.findByChatId(chatId)).thenReturn(getCorrespondenceList());
        assertEquals(correspondence, interactionService.getCorrespondence(authorization, chatId));
    }

    public List<Correspondence> getCorrespondenceList() {
        Correspondence correspondence = new Correspondence();
        Correspondence correspondence1 = new Correspondence();
        List<Correspondence> correspondences = new ArrayList<>();
        correspondence.setChatId(chatId);
        correspondence.setSender("1");
        correspondence.setRecipient("2");
        correspondence.setMessage("Привет");
        correspondence1.setChatId(chatId);
        correspondence1.setSender("2");
        correspondence1.setRecipient("1");
        correspondence1.setMessage("Привет!");
        correspondences.add(correspondence);
        correspondences.add(correspondence1);
        return correspondences;
    }
}
