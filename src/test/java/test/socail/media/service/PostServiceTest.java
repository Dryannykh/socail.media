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
import test.socail.media.db.model.Post;
import test.socail.media.db.repository.PostRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.NotAllowedError;
import test.socail.media.services.PostService;
import test.social.media.dto.PostCreateRequest;
import test.social.media.dto.PostDto;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PostRepository repository;

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
    public void createPost() {
        PostCreateRequest postCreateRequest = new PostCreateRequest();
        postCreateRequest.setTitle("1");
        postCreateRequest.setText("1");
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        postService.createPost(authorization, postCreateRequest);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void findExistingPostById() {
        when(repository.findById(any())).thenReturn(Optional.of(initPost()));
        assertEquals(initPostDto(), postService.getPostById(any()));
    }

    @Test
    public void findNotExistingPostById() {
        when(repository.findById(any())).thenThrow(EntityNotFoundError.class);
        assertThrows(EntityNotFoundError.class, () -> postService.getPostById(any()));
    }

    @Test
    public void deleteMyPostById() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(initPost()));
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        doNothing().when(repository).deleteById(id);
        postService.deletePostById(authorization, id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void deleteStrangerPostById() {
        when(repository.findById(any())).thenReturn(Optional.of(initPost()));
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("2");
        assertThrows(NotAllowedError.class, () -> postService.deletePostById(authorization, any()));
    }

    @Test
    public void changeStrangerPostById() {
        when(repository.findById(any())).thenReturn(Optional.of(initPost()));
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("2");
        assertThrows(NotAllowedError.class, () -> postService.changePostById(authorization, any(), initChangedPostDto()));
    }

    @Test
    public void changePostById() {
        when(repository.findById(any())).thenReturn(Optional.of(initPost()));
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        assertEquals(initChangedPostDto(), postService.changePostById(authorization, any(), initChangedPostDto()));
    }

    public Post initPost() {
        Post post = new Post();
        post.setText("1");
        post.setTitle("1");
        post.setAuthor("1");
        post.setPostCreationTime(time);
        return post;
    }

    public PostDto initPostDto() {
        PostDto postDto = new PostDto();
        postDto.setText("1");
        postDto.setTitle("1");
        postDto.setAuthor("1");
        postDto.setPostCreationTime(String.valueOf(time));
        return postDto;
    }

    public PostDto initChangedPostDto() {
        PostDto postDto = new PostDto();
        postDto.setText("2");
        postDto.setTitle("2");
        postDto.setAuthor("1");
        postDto.setPostCreationTime(String.valueOf(time));
        return postDto;
    }
}
