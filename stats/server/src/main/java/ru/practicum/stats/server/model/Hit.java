package ru.practicum.stats.server.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String app;
    @Column(length = 256)
    private String uri;
    @Column(length = 50)
    private String ip;
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
