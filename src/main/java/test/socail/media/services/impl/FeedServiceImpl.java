package test.socail.media.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import test.socail.media.config.jwt.JwtUtils;
import test.socail.media.db.model.Interaction;
import test.socail.media.db.repository.FeedRepository;
import test.socail.media.db.repository.InteractionRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.mapper.PostMapper;
import test.socail.media.services.FeedService;
import test.social.media.dto.PostDtoList;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;

    private final InteractionRepository interactionRepository;
    private final PostMapper postMapper;
    private final JwtUtils jwtUtils;
    @Override
    public PostDtoList getUserPosts(String authorization, Pageable pageable) {
        String author = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("postCreationTime")));
        return postMapper.mapToPostListDto(feedRepository.findByAuthor(pageRequest, author));
    }

    @Override
    public PostDtoList getAuthorsPosts(String authorization, Pageable pageable) {
        String username = jwtUtils.getUserNameFromJwtToken(authorization.replace("Bearer ",""));
        if(interactionRepository.existsBySender(username)) {
            Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("postCreationTime")));
            List<Interaction> interactionList = interactionRepository.findBySender(username);
            List<String> authorList = interactionList.stream()
                    .map(Interaction::getAuthor)
                    .collect(Collectors.toList());
            return postMapper.mapToPostListDto(feedRepository.findAllByAuthors(pageRequest, authorList));
        }
        else {
            throw new EntityNotFoundError("You do not have the authors you are subscribed to");
        }
    }
}
