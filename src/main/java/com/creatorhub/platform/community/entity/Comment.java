package com.creatorhub.platform.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name ="modified_at")
    private LocalDateTime modifiedAt;

    public Comment updateContent(String newContent) {
        this.content = newContent;
        this.modifiedAt = LocalDateTime.now();
        return this;
    }
}