package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @RequestBody @Valid NewCommentDto newComment) {
        log.info("POST COMMENT api: userId = {}, newComment = {}", userId, newComment);
        return commentService.createComment(userId, newComment);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody @Valid NewCommentDto updateComment) {
        log.info("PATCH COMMENT api: userId = {}, commentId = {}, updatedText = {}", userId, commentId, updateComment);
        return commentService.updateComment(userId, commentId, updateComment);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("DELETE COMMENT api: userId = {}, commentId = {}", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }
}
