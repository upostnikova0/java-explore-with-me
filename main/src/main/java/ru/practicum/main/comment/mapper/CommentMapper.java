package ru.practicum.main.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.model.Comment;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public Comment toEntity(NewCommentDto newComment) {
        return new Comment(
                null,
                newComment.getText(),
                null,
                LocalDateTime.now(),
                null
        );
    }

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getCreatedOn(),
                comment.getEvent().getId()
        );
    }
}
