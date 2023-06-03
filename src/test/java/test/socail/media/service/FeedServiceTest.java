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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import test.socail.media.config.jwt.JwtUtils;
import test.socail.media.db.model.Interaction;
import test.socail.media.db.model.Post;
import test.socail.media.db.repository.FeedRepository;
import test.socail.media.db.repository.InteractionRepository;
import test.socail.media.mapper.PostMapper;
import test.socail.media.services.FeedService;
import test.social.media.dto.PostDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FeedServiceTest {

    @Autowired
    private FeedService feedService;

    @MockBean
    private FeedRepository feedRepository;

    @MockBean
    private InteractionRepository interactionRepository;

    @Autowired
    private PostMapper postMapper;

    @MockBean
    private JwtUtils jwtUtils;

    private MockitoSession session;

    private final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("postCreationTime")));
    private final List<Post> postList = new ArrayList<>();


    String authorization = "authorization";

    @BeforeEach
    public void init() {
        session = Mockito.mockitoSession()
                .initMocks(this)
                .startMocking();
        initValues();
    }

    @AfterEach
    public void closeSession() {
        session.finishMocking();
    }

    @Test
    public void getUserPosts() {
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("1");
        when(feedRepository.findByAuthor(pageable, "1")).thenReturn(new PageImpl<>(postList));
        assertEquals(initPostDtoList(), feedService.getUserPosts(authorization, pageable).getContent());
    }

    @Test
    public void getAuthorsPosts() {
        Interaction interaction = new Interaction();
        List<Interaction> interactionList = new ArrayList<>();
        interaction.setSender("2");
        interaction.setAuthor("1");
        interactionList.add(interaction);
        when(jwtUtils.getUserNameFromJwtToken(authorization)).thenReturn("2");
        when(interactionRepository.existsBySender("2")).thenReturn(true);
        when(interactionRepository.findBySender("2")).thenReturn(interactionList);
        when(feedRepository.findAllByAuthors(pageable, List.of("1"))).thenReturn(new PageImpl<>(postList));
        assertEquals(initPostDtoList(), feedService.getAuthorsPosts(authorization, pageable).getContent());
    }

    public void initValues() {
        Post post = new Post();
        post.setText("1");
        post.setTitle("1");
        post.setAuthor("1");
        postList.add(post);
    }

    public List<PostDto> initPostDtoList() {
        List<PostDto> postDtoList = new ArrayList<>();
        PostDto post = new PostDto();
        post.setText("1");
        post.setTitle("1");
        post.setAuthor("1");
        postDtoList.add(post);
        return postDtoList;
    }
}
