package ru.practicum.main.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private Long author;
    private LocalDateTime createdOn;
    private Long event;
}
