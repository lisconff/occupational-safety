package com.zhituan.backend.domain.model.ai;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reportId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String businessType;

    @Column(length = 2000)
    private String inputSummary;

    private Integer score;
    private String overallRiskLevel;
    private LocalDateTime createdAt;
}
