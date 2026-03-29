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

    @Column(length = 1000)
    private String summary;

    private String lawCategory;
    private String riskLevel;
    private LocalDateTime happenedAt;
    private String source;
    private String coverUrl;

    @Column(length = 10000)
    private String content;

    @Column(length = 3000)
    private String legalBasis;

    @Column(length = 3000)
    private String lessonsLearned;

    private LocalDateTime createdAt;
}
