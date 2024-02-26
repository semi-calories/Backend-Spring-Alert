package com.example.demo.dto.Recommend.request;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiOperation(
        value = "음식 추천 요청",
        notes = "사용자가 먹을 음식 추천을 요청한다.")
public class RequestRecommendAPIDto {
    private Long userCode;
    private int eatTimes;
}
