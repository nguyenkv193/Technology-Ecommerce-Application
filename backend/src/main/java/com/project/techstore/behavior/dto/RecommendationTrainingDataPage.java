package com.project.techstore.behavior.dto;

import java.time.Instant;
import java.util.List;

public record RecommendationTrainingDataPage(
        List<UserInteractionExportDto> interactions,
        int page,
        int size,
        boolean last,
        Instant asOf
) {}
