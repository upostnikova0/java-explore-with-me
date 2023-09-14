package ru.practicum.main.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthorId(Long authorId, PageRequest pageRequest);

    List<Comment> findAllByEventId(Long eventId);
}
