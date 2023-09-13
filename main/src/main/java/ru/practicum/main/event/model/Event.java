package ru.practicum.main.event.model;

import lombok.*;
import org.hibernate.annotations.Formula;
import ru.practicum.main.event.category.model.Category;
import ru.practicum.main.event.enums.PublicStatus;
import ru.practicum.main.event.location.model.Location;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Column(length = 2000, nullable = false)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "confirmed_requests", nullable = false)
    @Formula("(select count(req.id) from participation_requests as req where req.event_id = id and req.status like 'CONFIRMED')")
    private Integer confirmedRequests;
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime createdOn;
    @Column(length = 7000, nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @Column(name = "is_paid", nullable = false)
    private Boolean paid;
    @Column(name = "participants_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private PublicStatus state;
    @Column(length = 120, nullable = false)
    private String title;
}