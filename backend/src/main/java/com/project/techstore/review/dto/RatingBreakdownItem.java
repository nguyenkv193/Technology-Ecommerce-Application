package com.project.techstore.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingBreakdownItem {
    private Integer stars;
    private Long count;
}
