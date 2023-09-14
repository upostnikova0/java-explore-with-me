package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.enums.PublicStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final EventService eventService;

    /**
     * Добавить комментарий можно только к опубликованному событию.
     */
    @Override
    @Transactional
    public CommentDto createComment(Long userId, NewCommentDto newComment) {
        User author = userService.findById(userId);
        Event event = eventService.findById(newComment.getEventId());
        if (!event.getState().equals(PublicStatus.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "event was not found");
        }
        Comment comment = commentMapper.toEntity(newComment);
        comment.setAuthor(author);
        comment.setEvent(event);
        CommentDto savedComment = commentMapper.toDto(commentRepository.save(comment));
        log.info("new comment was saved successfully: {}", savedComment);
        return savedComment;
    }

    /**
     * Обновить комментарий может только автор.
     * Обновление комментария возможно только в течение 1 часа после даты публикации.
     */
    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto commentForUpdate) {
        User user = userService.findById(userId);
        Comment comment = findById(commentId);
        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only author can update comment");
        }
        if (LocalDateTime.now().isAfter(comment.getCreatedOn().plusHours(1))) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "the comment can be edited within 1 hour after publication");
        }
        comment.setText(commentForUpdate.getText());
        CommentDto updatedComment = commentMapper.toDto(commentRepository.save(comment));
        log.info("updated comment was saved successfully: {}", updatedComment);
        return updatedComment;
    }

    /**
     * Удалить комментарий может только автор
     */
    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        User user = userService.findById(userId);
        Comment comment = findById(commentId);
        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only author can delete comment");
        }
        log.info("comment with id = {} was deleted", comment.getId());
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        CommentDto foundComment = commentMapper.toDto(findById(commentId));
        log.info("found comment: {}", foundComment);
        return foundComment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long eventId) {
        Event event = eventService.findById(eventId);
        List<CommentDto> foundComments = commentRepository
                .findAllByEventId(event.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        log.info("found comments: {}", foundComments);
        return foundComments;
    }

    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("comment with id %d was not found", commentId)));
    }
}
