package com.gitperform.gitperformance.dto.metrics;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PeriodComparisonRequest {
    private LocalDateTime firstPeriodStart;
    private LocalDateTime firstPeriodEnd;
    private LocalDateTime secondPeriodStart;
    private LocalDateTime secondPeriodEnd;
}
