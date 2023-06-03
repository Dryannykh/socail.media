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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import test.socail.media.services.FeedService;
import test.social.media.dto.PostDto;
import test.social.media.dto.PostDtoList;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class FeedControllerTest {
    @MockBean
    private FeedService feedService;

    @Autowired
    private MockMvc mockMvc;

    private MockitoSession session;

    String authorization = "authorization";

    private final PostDtoList postDtoList = initPostDto();

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
    public void getUserPosts() throws Exception {
        when(feedService.getUserPosts(any(), any())).thenReturn(postDtoList);
        MvcResult result = mockMvc.perform(get("/get-my-posts")
                        .header("Authorization", "Bearer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains(getJsonString(postDtoList.getContent())));
    }

    @Test
    public void getAuthorsPosts() throws Exception {
        when(feedService.getAuthorsPosts(any(), any())).thenReturn(postDtoList);
        MvcResult result = mockMvc.perform(get("/get-authors-posts")
                        .header("Authorization", "Bearer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains(getJsonString(postDtoList.getContent())));
    }

    public PostDtoList initPostDto() {
        PostDto postDto = new PostDto();
        postDto.setText("1");
        postDto.setAuthor("1");
        postDto.setTitle("1");
        PostDtoList postDtoList = new PostDtoList();
        postDtoList.content(Collections.singletonList(postDto));
        return postDtoList;
    }

    public String getJsonString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
