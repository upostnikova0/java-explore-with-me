package ru.practicum.stats.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
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
    @Column(nullable = false, length = 256)
    private String uri;
    @Column(nullable = false, length = 50)
    private String ip;
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
