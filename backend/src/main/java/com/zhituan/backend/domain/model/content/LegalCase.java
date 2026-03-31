package com.zhituan.backend.domain.model.content;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "legal_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String caseId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String lawCategory;
    private String riskLevel;
    private LocalDateTime happenedAt;
    private String source;
    private String coverUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String legalBasis;

    @Column(columnDefinition = "TEXT")
    private String lessonsLearned;

    private LocalDateTime createdAt;
}
