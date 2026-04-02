package com.project.monitoring.entity;

import com.project.monitoring.enums.MetricName;
import com.project.monitoring.enums.RuleOperator;
import com.project.monitoring.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "monitoring_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricName metricName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOperator operator;

    @Column(nullable = false)
    private String thresholdValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}