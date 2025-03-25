package com.leverx.ratingsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    private boolean approved;

    @Column(nullable = false)
    private Integer rating;

    private LocalDateTime createdAt;
}
