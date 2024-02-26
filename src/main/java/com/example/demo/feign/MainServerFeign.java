package com.example.demo.feign;

import com.example.demo.dto.Recommend.request.RequestRecommendAPIDto;
import com.example.demo.dto.Recommend.response.ResponseRecommendListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name="MainServerFeign", url="http://34.236.139.24:8000")
public interface MainServerFeign {

    /**
     * test 연결
     */
    @GetMapping("/test")
    public String test();

    /**
     * 추천 알고리즘 요청
     */
    @PostMapping("/recommend/request")
    public ResponseRecommendListDto requestRecommend(
            @RequestBody RequestRecommendAPIDto requestRecommendAPIDto
            );

}