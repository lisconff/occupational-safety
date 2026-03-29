package com.zhituan.backend.domain.model.ai;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "risk_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String riskItemId;

    @Column(nullable = false)
    private String reportId;

    @Column(length = 1000)
    private String matchedText;

    private Integer startIndex;
    private Integer endIndex;
    private String riskLevel;

    @Column(length = 2000)
    private String reason;

    @Column(length = 2000)
    private String legalBasis;

    @Column(length = 2000)
    private String suggestion;
}
