package com.example.demo.dto.Recommend.response;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiOperation(
        value = "음식 추천 응답",
        notes = "사용자가 먹을 음식 목록을 추천한다.")
public class ResponseRecommendListDto {
    //이미지 포함된 DTO의 List
    private List<ResponseRecommendDto> recommendList = new ArrayList<>();
}
