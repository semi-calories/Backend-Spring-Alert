package com.example.demo.dto.Recommend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendDto {
    // Image 없음
    private Integer foodCode;
    private String foodName;
    private String foodMainCategory;
    private String foodDetailedClassification;
    private Double foodWeight;
    private Double foodKcal;
    private Double foodCarbon;
    private Double foodProtein;
    private Double foodFat;
}
