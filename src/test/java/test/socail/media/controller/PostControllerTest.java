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
import test.socail.media.services.PostService;
import test.social.media.dto.PostCreateRequest;
import test.social.media.dto.PostDto;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    private MockitoSession session;

    String authorization = "authorization";

    Instant time = Clock.systemDefaultZone().instant();

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
    public void createPost() throws Exception {
        doNothing().when(postService).createPost(any(), any());
        mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonString(getPostCreateRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    public void deletePost() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(postService).deletePostById(authorization, id);
        mockMvc.perform(delete("/post/{id}", id)
                        .header("Authorization", "Bearer"))
                .andExpect(status().is(204));
    }

    @Test
    public void getPost() throws Exception {
        UUID id = UUID.randomUUID();
        when(postService.getPostById(id)).thenReturn(initPostDto());
        mockMvc.perform(get("/post/{id}", id)
                        .header("Authorization", "Bearer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("1"))
                .andExpect(jsonPath("$.title").value("1"));
    }

    @Test
    public void changePost() throws Exception {
        UUID id = UUID.randomUUID();
        when(postService.changePostById(authorization, id, initPostDto())).thenReturn(initPostDto());
        mockMvc.perform(put("/post/{id}", id)
                        .header("Authorization", "Bearer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonString(initPostDto())))
                .andExpect(status().isOk());
    }

    public String getJsonString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public PostCreateRequest getPostCreateRequest() {
        PostCreateRequest postCreateRequest = new PostCreateRequest();
        postCreateRequest.setText("1");
        postCreateRequest.setTitle("1");
        return postCreateRequest;
    }

    public PostDto initPostDto() {
        PostDto postDto = new PostDto();
        postDto.setText("1");
        postDto.setTitle("1");
        postDto.setAuthor("1");
        postDto.setPostCreationTime(String.valueOf(time));
        return postDto;
    }
}
