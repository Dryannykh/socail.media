package test.socail.media.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import test.socail.media.services.InteractionService;
import test.social.media.dto.Author;


import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class InteractionControllerTest {
    @MockBean
    private InteractionService interactionService;

    @Autowired
    private MockMvc mockMvc;

    private MockitoSession session;

    String authorization = "authorization";

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
    public void subscribeToAuthor() throws Exception {
        doNothing().when(interactionService).subscribeToAuthor(authorization, "1");
        mockMvc.perform(post("/subscribe")
                        .header("Authorization", "Bearer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonString(initAuthor())))
                .andExpect(status().isOk());
    }

    @Test
    public void unsubscribeFromAuthor() throws Exception {
        doNothing().when(interactionService).unsubscribeFromAuthor(authorization, "1");
        mockMvc.perform(post("/unsubscribe")
                        .header("Authorization", "Bearer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonString(initAuthor())))
                .andExpect(status().isOk());
    }

    @Test
    public void acceptFriendRequest() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(interactionService).acceptFriendRequest(authorization, id);
        mockMvc.perform(get("/accept/{requestId}", id)
                        .header("Authorization", "Bearer"))
                .andExpect(status().isOk());
    }

    @Test
    public void declineFriendRequest() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(interactionService).declineFriendRequest(authorization, id);
        mockMvc.perform(get("/reject/{requestId}", id)
                        .header("Authorization", "Bearer"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCorrespondence() throws Exception {
        UUID chatId = UUID.randomUUID();
        when(interactionService.getCorrespondence(authorization, chatId)).thenReturn("correspondence");
        mockMvc.perform(get("/get-correspondence/{chatId}", chatId)
                        .header("Authorization", "Bearer"))
                .andExpect(status().isOk());
    }

    public String getJsonString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public Author initAuthor() {
        Author author = new Author();
        author.setAuthor("1");
        return author;
    }
}
